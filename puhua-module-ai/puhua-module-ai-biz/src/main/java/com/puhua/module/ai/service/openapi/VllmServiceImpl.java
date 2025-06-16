package com.puhua.module.ai.service.openapi;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.common.util.string.TokenUtils;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.api.llm.vo.completion.CompletionResult;
import com.puhua.module.ai.api.llm.vo.completion.Delta;
import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import com.puhua.module.ai.model.vllm.VllmModel;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;
import static com.puhua.module.ai.enums.ModelApiConstants.SSE_TIME_OUT;

@Slf4j
@Service
public class VllmServiceImpl implements VllmService {

    @Resource
    private ModelRepositoryService modelRepositoryService;
    @Resource
    private MemberApi memberApi;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private RateLimiterRedisDAO rateLimiterRedisDAO;

    // 动态线程池配置
    private final ExecutorService rateLimiterExecutor = new ThreadPoolExecutor(
            10,
            200,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 模型检查与限流
    private ModelRepositoryDO validateModel(String modelName, Long memberUserId) {
        ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);
        if (model == null || CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(cn.hutool.http.HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        // 获取用户
        MemberUserInfoRespVO memberUser = memberApi.getMemberUser(memberUserId);
        if (memberUser == null) {
            throw new ServiceException(cn.hutool.http.HttpStatus.HTTP_NOT_FOUND, "User not found");
        }
        // 校验余额
        if (model.getInferencePrice() > 0 && memberUser.getBalance() <= 0) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Insufficient balance");
        }

        String rpmKey = "model:rpm-" + memberUserId + modelName;
        String tpmKey = "model:tpm-" + memberUserId + modelName;
        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            // 模型层面限流
            rpmKey = "model:rpm-" + modelName;
            tpmKey = "model:tpm-" + modelName;
        }

        // RPM 限流
        if (!rateLimiterRedisDAO.tryAcquire(rpmKey, model.getRpm(), 1, TimeUnit.MINUTES)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "RateLimit Request was rejected due to rate limiting. ");
        }

        // TPM 可用令牌检查
        long availablePermits = rateLimiterRedisDAO.getAvailablePermits(tpmKey);
        if (availablePermits < 32) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "Request was rejected due to exceeding TPM (Tokens Per Minute) limits");
        }

        return model;
    }

    // 保存任务记录
    private void saveTask(Long memberUserId, ModelRepositoryDO model, VllmRequestVo request,
                          String response, long tokens) {
        long fee = calculatePrice(tokens, model.getInferencePrice());
        if (model.getInferencePrice() > 0) {
            memberApi.changeBalance(memberUserId, -fee);
        }

        TaskDO task = TaskDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .userId(memberUserId)
                .modelId(model.getModelId())
                .modelName(model.getModelName())
                .modelType(model.getType())
                .version(model.getVersion())
                .requestBody(JSON.toJSONString(request))
                .responseBody(response)
                .chargeMode(model.getChargeMode())
                .unitPrice(BigDecimal.valueOf(model.getInferencePrice()))
                .callTime(LocalDateTime.now())
                .totalFee(BigDecimal.valueOf(fee))
                .tokens(tokens)
                .build();

        taskMapper.insert(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompletionResult nonStream(Long memberUserId, VllmRequestVo vllmRequestVo) {
        String modelName = vllmRequestVo.getModel();
        log.info("Non-stream request: {} for model: {}", vllmRequestVo, modelName);

        ModelRepositoryDO model = validateModel(modelName, memberUserId);
        VllmModel vllmModel = modelRepositoryService.getVllmModel(modelName);

        try {
            String result = vllmModel.completionNoStream(vllmRequestVo);
            CompletionResult completionResult = JSON.parseObject(result, CompletionResult.class);
            long tokens = completionResult.getUsage().getTotalTokens();

            log.info("Tokens used: {}", tokens);
            saveTask(memberUserId, model, vllmRequestVo, result, tokens);

            // 申请实际使用的令牌
            String tpmKey = "model:tpm-" + memberUserId + modelName;
            if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
                tpmKey = "model:tpm-" + modelName;
            }

            long availablePermits = rateLimiterRedisDAO.getAvailablePermits(tpmKey);
            rateLimiterRedisDAO.tryAcquire(tpmKey, model.getTpm(), 1, TimeUnit.MINUTES, Math.min(tokens, availablePermits));

            return completionResult;
        } catch (Exception e) {
            log.error("Non-stream API call failed", e);
            throw new ServiceException(cn.hutool.http.HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }
    }

    // 流式处理
    private void processStreamResponse(SseEmitter emitter, HttpResponse response,
                                       ModelRepositoryDO model, Long memberUserId,
                                       VllmRequestVo request, boolean isWebStream) {
        List<CompletionResult> results = new ArrayList<>();
        StringBuilder fullResponse = new StringBuilder();
        // 令牌计数器和累计值
        AtomicInteger chunkCounter = new AtomicInteger(0);
        AtomicLong accumulatedTokens = new AtomicLong(0);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = StrUtil.removePrefix(line, "data: ");

                if (!StrUtil.startWith(line, "{")) {
                    log.debug("Invalid data: {}", line);
                    continue;
                }

                CompletionResult result = JSON.parseObject(line, CompletionResult.class);
                if (isWebStream && !result.getChoices().isEmpty()) {
                    adaptWebStreamFormat(result);
                }

                emitData(emitter, result, isWebStream);
                results.add(result);
                results.forEach(res -> fullResponse.append(res.getChoices().get(0).getDelta().getContent()));

                // 计算当前chunk的token数
                long tokens = calculateTokensForChunk(result);
                accumulatedTokens.addAndGet(tokens);

                // 每10个chunk申请一次令牌
                if (chunkCounter.incrementAndGet() % 10 == 0) {
                    final long tokensToConsume = accumulatedTokens.getAndSet(0);
                    if (tokensToConsume > 0) {
                        rateLimiterExecutor.submit(() -> {
                            applyTpmRateLimit(memberUserId, model, tokensToConsume);
                        });
                    }
                }
            }
        } catch (IOException e) {
            log.error("Stream processing error", e);
            emitter.completeWithError(e);
        } finally {
            emitter.complete();
            // 处理剩余token
            final long remainingTokens = accumulatedTokens.get();
            if (remainingTokens > 0) {
                rateLimiterExecutor.submit(() -> {
                    applyTpmRateLimit(memberUserId, model, remainingTokens);
                });
            }

            long totalTokens = results.isEmpty() ? 0 :
                    results.get(results.size() - 1).getUsage().getTotalTokens();

            saveTask(memberUserId, model, request, fullResponse.toString(), totalTokens);
        }
    }

    // 计算单个chunk的token数
    private long calculateTokensForChunk(CompletionResult result) {
        if (result.getUsage() != null && result.getUsage().getTotalTokens() > 0) {
            return result.getUsage().getTotalTokens();
        }

        return result.getChoices().stream()
                .mapToLong(choice -> {
                    String content = Optional.ofNullable(choice.getDelta())
                            .map(d -> d.getContent() + StrUtil.nullToEmpty(d.getReasoningContent()))
                            .orElse("");
                    return TokenUtils.calculateTokens(content);
                })
                .sum();
    }

    // 执行TPM令牌申请
    private void applyTpmRateLimit(Long memberUserId, ModelRepositoryDO model, long tokens) {
        String modelName = model.getName();
        try {
            String tpmKey = "model:tpm-" + memberUserId + modelName;
            if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
                tpmKey = "model:tpm-" + modelName;
            }
            // 批量申请令牌
            rateLimiterRedisDAO.tryAcquire(tpmKey, model.getTpm(), 1, TimeUnit.MINUTES, tokens);
        } catch (Exception e) {
            log.error("TPM令牌申请失败: userId={}, model={}, tokens={}",
                    memberUserId, modelName, tokens, e);
        }
    }

    // Web流式数据格式适配
    private void adaptWebStreamFormat(CompletionResult result) {
        Delta delta = result.getChoices().get(0).getDelta();
        if (StrUtil.isNotBlank(delta.getReasoningContent())) {
            delta.setType("thinking");
            delta.setContent(delta.getReasoningContent());
            delta.setReasoningContent(null);
        } else {
            delta.setType("text");
        }
    }

    // SSE数据发送
    private void emitData(SseEmitter emitter, CompletionResult result, boolean isWebStream) {
        try {
            String data = isWebStream ?
                    JSON.toJSONString(result) :
                    result.getChoices().get(0).getDelta().getContent();

            emitter.send(data);
        } catch (IOException e) {
            log.error("SSE send error", e);
            emitter.completeWithError(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter stream(Long memberUserId, VllmRequestVo vllmRequestVo) {
        return processStreamRequest(memberUserId, vllmRequestVo, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter webStream(Long memberUserId, VllmRequestVo vllmRequestVo) {
        return processStreamRequest(memberUserId, vllmRequestVo, true);
    }

    private SseEmitter processStreamRequest(Long memberUserId, VllmRequestVo vllmRequestVo,
                                            boolean isWebStream) {
        String modelName = vllmRequestVo.getModel();
        log.info("{} request: {}", isWebStream ? "WebStream" : "Stream", vllmRequestVo);

        ModelRepositoryDO model = validateModel(modelName, memberUserId);
        VllmModel vllmModel = modelRepositoryService.getVllmModel(modelName);
        SseEmitter emitter = new SseEmitter(SSE_TIME_OUT);

        try {
            HttpResponse response = vllmModel.completion(vllmRequestVo);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.HTTP_OK) {
                String body = EntityUtils.toString(response.getEntity());
                log.error("API error: {} - {}", statusCode, body);
                throw new ServiceException(cn.hutool.http.HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later..");
            }

            executor.execute(() ->
                    processStreamResponse(emitter, response, model, memberUserId, vllmRequestVo, isWebStream));
        } catch (IOException e) {
            log.error("Stream API call failed", e);
            throw new ServiceException(cn.hutool.http.HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later...");
        }

        return emitter;
    }

    private long calculatePrice(long tokens, long unitPrice) {
        if (tokens < 0 || unitPrice < 0) {
            throw new IllegalArgumentException("Invalid tokens or price value");
        }
        long units = (long) Math.ceil(tokens / 1000.0);
        return units * unitPrice;
    }
}