package com.puhua.module.ai.service.openapi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseItem;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import com.puhua.module.ai.model.image.ImageModel;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.infra.api.file.FileApi;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private ModelRepositoryService modelRepositoryService;

    @Resource
    RateLimiterRedisDAO rateLimiterRedisDAO;

    @Resource
    MemberApi memberApi;

    @Resource
    TaskMapper taskMapper;

    @Resource
    FileApi fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenerateImageResponseVo generateImage(GenerateImageRequestVo vo) {
        Long userId = getLoginUserId();
        // 接口调用
        String modelName = vo.getModel();
        ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);
        if (model == null || CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        // 获取用户
        MemberUserInfoRespVO memberUser = memberApi.getMemberUser(userId);
        if (memberUser == null) {
            throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "User not found");
        }
        // 校验余额
        if (model.getInferencePrice() > 0 && memberUser.getBalance() <= 0) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Insufficient balance");
        }

        String qpsKey = "model:qps-" + userId + modelName;
        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            qpsKey = "model:qps-" + modelName;
        }
        if (!rateLimiterRedisDAO.tryAcquire(qpsKey, model.getQps(), 1, TimeUnit.SECONDS)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "RateLimit Request was rejected due to rate limiting. ");
        }

        ImageModel imageModel = modelRepositoryService.getImageModel(model.getName());
        String body = JSONObject.toJSONString(vo);
        long traceId = IdUtil.getSnowflakeNextId();
        log.info("traceId:{},[image]请求：{}", traceId, body);
        GenerateImageResponseVo responseVo = imageModel.generateImage(vo);
        for (GenerateImageResponseItem datum : responseVo.getData()) {
            String fileUrl = datum.getUrl();
            // 打开网络文件的输入流
            try {
                URL url = new URL(fileUrl);
                try (InputStream in = url.openStream()) {
                    // 创建临时文件（前缀、后缀、目录可自定义）
                    Path tempFile = Files.createTempFile("download-", ".tmp");
                    // 将网络流的内容复制到临时文件
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    File file = tempFile.toFile();
                    String s3Url = fileApi.createFile("task/" + userId + IdUtil.fastSimpleUUID() + ".jpg", FileUtil.readBytes(file));
                    datum.setUrl(s3Url);
                }
            } catch (Exception e) {
                log.error("资源转存失败,taskId:{},err:{}", traceId, e.getMessage());
            }
        }

        String result = JSONObject.toJSONString(responseVo);
        log.info("traceId:{},[image]result:{}", traceId, result);
        long totalFee = model.getInferencePrice() * Long.valueOf(vo.getBatchSize());
        if (model.getInferencePrice() > 0) {
            memberApi.changeBalance(userId, -1 * totalFee);
        }
        // 任务存储
        TaskDO task = TaskDO.builder()
                .id(traceId)
                .userId(userId)
                .modelId(String.valueOf(model.getId()))
                .modelName(modelName)
                .modelType(model.getType())
                .version(model.getVersion())
                .requestBody(body)
                .responseBody(result)
                .chargeMode(model.getChargeMode())
                .unitPrice(new BigDecimal(model.getInferencePrice()))
                .callTime(LocalDateTime.now())
                .totalFee(new BigDecimal(totalFee))
                .tokens(0L)
                .build();
        taskMapper.insert(task);
        return responseVo;
    }

}
