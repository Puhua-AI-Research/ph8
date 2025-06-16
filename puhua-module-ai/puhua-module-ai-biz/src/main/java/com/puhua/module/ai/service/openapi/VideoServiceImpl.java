package com.puhua.module.ai.service.openapi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.controller.app.openapi.vo.VolcengineApiResponse;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import com.puhua.module.ai.enums.ModelApiConstants;
import com.puhua.module.ai.model.video.VideoModel;
import com.puhua.module.ai.service.apithirdkey.ApiThirdKeyService;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.infra.api.file.FileApi;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    // 常量定义
    private static final String STATUS_SUCCEEDED = "succeeded";
    private static final String STATUS_RUNNING = "running";
    private static final String VIDEO_FILE_SUFFIX = ".mp4";
    private static final String TEMP_FILE_PREFIX = "download-";
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final String TASK_FILE_PATH_PREFIX = "task/";

    @Resource
    private ModelRepositoryService modelRepositoryService;
    @Resource
    private MemberApi memberApi;
    @Resource
    private FileApi fileApi;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private ApiThirdKeyService apiThirdKeyService;
    @Resource
    private RateLimiterRedisDAO rateLimiterRedisDAO;
    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject generate(Long userId, HashMap<String, Object> requestParams) {
        // 参数校验
        final String modelName = validateRequestModel(requestParams);

        // 获取并校验模型
        final ModelRepositoryDO model = validateModelAvailable(modelName);

        // 限流控制
        checkRateLimit(userId, model);

        // 执行生成操作
        final VideoModel videoModel = modelRepositoryService.getVideoModel(modelName);
        final JSONObject generateResult = videoModel.generateVideo(requestParams);

        // 保存任务记录
        final TaskDO task = buildTaskEntity(userId, model, requestParams, generateResult);
        taskMapper.insert(task);

        generateResult.put("id", task.getId());
        return generateResult;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject get(String id) {
        // 基于任务ID的分布式锁
        final String lockKey = "task:lock:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 获取分布式锁
            lock.lock();
            final TaskDO taskDO = taskMapper.selectById(id);
            if (taskDO == null) {
                throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "task not found. ");
            }

            final ModelRepositoryDO model = validateModelAvailable(taskDO.getModelName());

            // 获取用户
            MemberUserInfoRespVO memberUser = memberApi.getMemberUser(getLoginUserId());
            if (memberUser == null) {
                throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "User not found");
            }
            // 校验余额
            if (model.getInferencePrice() > 0 && memberUser.getBalance() <= 0) {
                throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Insufficient balance");
            }

            if (ModelApiConstants.VE_MODEL_MANUFACTURERS_NAME.equals(model.getManufacturers())) {
                return handleVolcengineTaskStatus(taskDO, model);
            } else {
                return buildStandardTaskResponse(taskDO);
            }

        } finally {
            try {
                // 确保释放
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (IllegalMonitorStateException e) {
                log.warn("Redisson锁释放异常 lockKey:{} error:{}", lockKey, e.getMessage());
            }
        }
    }

    /**
     * 火山引擎任务状态处理
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject handleVolcengineTaskStatus(TaskDO taskDO, ModelRepositoryDO model) {
        final VideoModel videoModel = modelRepositoryService.getVideoModel(model.getName());

        // 查询第三方状态（网络操作放在事务外）
        final JSONObject result = videoModel.checkTaskStatus(taskDO.getTaskId());
        final VolcengineApiResponse volcResponse = JSONObject.toJavaObject(result, VolcengineApiResponse.class);

        // 未完成直接返回
        if (!STATUS_SUCCEEDED.equals(volcResponse.getStatus())) {
            return result;
        }

        // 已成功但未处理
        if (!STATUS_SUCCEEDED.equals(taskDO.getStatus())) {
            try {
                // 文件转存（可能包含网络IO）
                final String storedUrl = transferVideoToStorage(taskDO.getId(), volcResponse.getContent().getVideoUrl());
                volcResponse.getContent().setVideoUrl(storedUrl);

                // 数据库更新
                updateTaskStatus(taskDO, volcResponse);

                // 费用计算（涉及外部系统调用）
                deductUserBalance(taskDO, model, volcResponse.getUsage().getTotalTokens());
            } catch (Exception e) {
                log.error("任务处理失败，进行事务回滚 taskId:{}, error:{}", taskDO.getId(), e.getMessage());
                throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
            }
        }

        return (JSONObject) JSONObject.toJSON(volcResponse);
    }

    private String validateRequestModel(HashMap<String, Object> requestParams) {
        if (!requestParams.containsKey("model")) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }
        return requestParams.get("model").toString();
    }

    private ModelRepositoryDO validateModelAvailable(String modelName) {
        final ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);
        if (model == null || CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }
        return model;
    }

    private void checkRateLimit(Long userId, ModelRepositoryDO model) {
        String rateLimitKey = String.format("model:rpm-%d-%s", userId, model.getName());
        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            rateLimitKey = String.format("model:rpm-%s", model.getName());
        }
        if (!rateLimiterRedisDAO.tryAcquire(rateLimitKey, model.getRpm(), 1, TimeUnit.MINUTES)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "RateLimit Request was rejected due to rate limiting. ");
        }
    }

    private TaskDO buildTaskEntity(Long userId, ModelRepositoryDO model,
                                   HashMap<String, Object> params, JSONObject generateResult) {
        return TaskDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .userId(userId)
                .taskId(generateResult.getString("id"))
                .modelId(String.valueOf(model.getId()))
                .modelName(model.getName())
                .endpoint(getEndpointOrDefault(params))
                .modelType(model.getType())
                .version(model.getVersion())
                .requestBody(JSONObject.toJSONString(params))
                .responseBody("")
                .taskMode(model.getTaskMode())
                .chargeMode(model.getChargeMode())
                .unitPrice(new BigDecimal(model.getInferencePrice()))
                .callTime(LocalDateTime.now())
                .totalFee(BigDecimal.ZERO)
                .tokens(0L)
                .status(STATUS_RUNNING)
                .build();
    }

    private String getEndpointOrDefault(HashMap<String, Object> params) {
        return params.containsKey("model") ? params.get("model").toString() : "";
    }

    private JSONObject buildStandardTaskResponse(TaskDO taskDO) {
        final JSONObject response = new JSONObject();
        response.put("id", taskDO.getId());
        response.put("status", taskDO.getStatus());
        if (STATUS_SUCCEEDED.equals(taskDO.getStatus())) {
            response.putAll(JSONObject.parseObject(taskDO.getResponseBody()));
        }
        return response;
    }

    /**
     * 更新状态
     */
    private void updateTaskStatus(TaskDO taskDO, VolcengineApiResponse response) {
        taskDO.setStatus(STATUS_SUCCEEDED);
        taskDO.setResponseBody(JSONObject.toJSONString(response.getContent()));
        if (taskMapper.updateById(taskDO) != 1) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Generation failed");
        }
    }

    /**
     * 余额扣除
     */
    private void deductUserBalance(TaskDO taskDO, ModelRepositoryDO model, Long totalTokens) {
        if (model.getInferencePrice() <= 0) {
            return;
        }

        final Long totalFee = calculatePrice(totalTokens, model.getInferencePrice());
        try {
            memberApi.changeBalance(taskDO.getUserId(), -totalFee);
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Generation video failed");
        }

    }

    /**
     * 文件操作
     **/
    private String transferVideoToStorage(Long taskId, String sourceUrl) {
        try (InputStream inputStream = new URL(sourceUrl).openStream()) {
            final Path tempFile = Files.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            final String fileName = TASK_FILE_PATH_PREFIX + taskId +
                    IdUtil.fastSimpleUUID() + VIDEO_FILE_SUFFIX;
            return fileApi.createFile(fileName, FileUtil.readBytes(tempFile.toFile()));
        } catch (Exception e) {
            log.error("视频转存失败 taskId:{}, error:{}", taskId, e.getMessage());
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Generation video file failed");
        }
    }

    private Long calculatePrice(Long tokens, Integer unitPrice) {
        final double tokenUnits = Math.ceil(tokens / 1000.0);
        return (long) (tokenUnits * unitPrice);
    }
}