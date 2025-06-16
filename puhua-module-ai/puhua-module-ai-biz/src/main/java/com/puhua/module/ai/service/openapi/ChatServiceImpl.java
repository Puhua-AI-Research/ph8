package com.puhua.module.ai.service.openapi;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.common.util.string.TokenUtils;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.api.llm.vo.completion.CompletionResult;
import com.puhua.module.ai.api.llm.vo.completion.Delta;
import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import com.puhua.module.ai.model.chat.ChatModel;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
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
import java.util.function.Function;

import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;
import static com.puhua.module.ai.enums.ModelApiConstants.SSE_TIME_OUT;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompletionResult nonStream(Long memberUserId, ChatCompletionRequest request, String action) {
        logRequest(action, request);

        // 验证模型并应用限流
        ModelRepositoryDO model = validateModelAndApplyRateLimit(memberUserId, request);
        String modelName = model.getName();
        // 调用模型
        ChatModel chatModel = modelRepositoryService.getChatModel(request.getModel());
        String response = chatModel.chat(request, action);

        // 处理响应
        CompletionResult result = parseResponse(response);

        // 计费并记录任务
        long tokens = calculateTotalTokens(result);
        chargeAndRecordTask(memberUserId, request, result, model, tokens);
        // 批量申请令牌
        String tpmKey = "model:tpm-" + memberUserId + modelName;
        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            tpmKey = "model:tpm-" + modelName;
        }
        long availablePermits = rateLimiterRedisDAO.getAvailablePermits(tpmKey);
        rateLimiterRedisDAO.tryAcquire(tpmKey, model.getTpm(), 1, TimeUnit.HOURS, Math.min(tokens, availablePermits));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter stream(Long memberUserId, ChatCompletionRequest request, String action) throws IOException {
        return handleStream(memberUserId, request, action, this::processStandardStreamLine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter webStream(Long memberUserId, ChatCompletionRequest request) throws IOException {
        return handleStream(memberUserId, request, "chat", this::processWebStreamLine);
    }

    /**
     * 验证模型并应用限流
     */
    private ModelRepositoryDO validateModelAndApplyRateLimit(Long memberUserId, ChatCompletionRequest request) {
        String modelName = request.getModel();

        // 获取模型信息
        ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);
        if (model == null || CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        // 获取用户
        MemberUserInfoRespVO memberUser = memberApi.getMemberUser(memberUserId);
        if (memberUser == null) {
            throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "User not found");
        }
        // 校验余额
        if (model.getInferencePrice() > 0 && memberUser.getBalance() <= 0) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Insufficient balance");
        }

        // 调整最大token数
        if (request.getMaxTokens() != null && request.getMaxTokens() > model.getMaxTokens()) {
            request.setMaxTokens(model.getMaxTokens());
        }

        // 应用限流
        applyRateLimits(memberUserId, modelName, model);

        return model;
    }

    /**
     * 应用速率限制
     */
    private void applyRateLimits(Long memberUserId, String modelName, ModelRepositoryDO model) {
        String rpmKey = "model:rpm-" + memberUserId + modelName;
        String tpmKey = "model:tpm-" + memberUserId + modelName;
        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            // 该类型模型为模型层面限流
            rpmKey = "model:rpm-" + modelName;
            tpmKey = "model:tpm-" + modelName;
        }

        if (!rateLimiterRedisDAO.tryAcquire(rpmKey, model.getRpm(), 1, TimeUnit.MINUTES)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "RateLimit Request was rejected due to rate limiting. ");
        }

        if (!rateLimiterRedisDAO.tryAcquire(tpmKey, model.getTpm(), 1, TimeUnit.HOURS)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "Request was rejected due to exceeding TPM (Tokens Per Minute) limits");
        }

        long availablePermits = rateLimiterRedisDAO.getAvailablePermits(tpmKey);
        if (availablePermits < 32) {
            // 小于32直接拒绝
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "Request was rejected due to exceeding TPM (Tokens Per Minute) limits");
        }
    }

    /**
     * 处理流式响应
     */
    private void processStreamResponse(HttpResponse response, SseEmitter emitter,
                                       List<CompletionResult> responseList,
                                       Function<String, CompletionResult> lineProcessor,
                                       Runnable completionCallback,
                                       Long memberUserId,  // 新增参数
                                       ModelRepositoryDO model) {  // 新增参数
        // 令牌计数器和累计值
        AtomicInteger chunkCounter = new AtomicInteger(0);
        AtomicLong accumulatedTokens = new AtomicLong(0);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data: ")) continue;

                String data = line.substring(6).trim();
                if (data.isEmpty() || !data.startsWith("{")) continue;

                try {
                    CompletionResult result = lineProcessor.apply(data);
                    responseList.add(result);

                    // 计算当前chunk的token数
                    long tokens = calculateTokensForChunk(result);
                    accumulatedTokens.addAndGet(tokens);

                    // 发送SSE事件
                    emitter.send(JSON.toJSONString(result));

                    // 每10个chunk申请一次令牌
                    if (chunkCounter.incrementAndGet() % 10 == 0) {
                        final long tokensToConsume = accumulatedTokens.getAndSet(0);
                        if (tokensToConsume > 0) {
                            rateLimiterExecutor.submit(() -> {
                                applyTpmRateLimit(memberUserId, model, tokensToConsume);
                            });
                        }
                    }
                } catch (Exception e) {
                    log.error("解析流数据异常: {}", data, e);
                }
            }

            // 处理剩余token
            final long remainingTokens = accumulatedTokens.get();
            if (remainingTokens > 0) {
                rateLimiterExecutor.submit(() -> {
                    applyTpmRateLimit(memberUserId, model, remainingTokens);
                });
            }

        } catch (Exception e) {
            log.error("流处理异常", e);
            emitter.completeWithError(e);
        } finally {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("SSE完成时发生异常", e);
            }
            completionCallback.run();
        }
    }

    /**
     * 计算单个chunk的token数
     */
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

    /**
     * 异步申请TPM令牌
     */
    private void applyTpmRateLimit(Long memberUserId, ModelRepositoryDO model, long tokens) {
        String modelName = model.getName();
        try {
            String tpmKey = "model:tpm-" + memberUserId + modelName;
            if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
                // 该类型模型为模型层面限流
                tpmKey = "model:tpm-" + modelName;
            }
            // 批量申请令牌
            rateLimiterRedisDAO.tryAcquire(tpmKey, model.getTpm(), 1, TimeUnit.HOURS, tokens);
        } catch (Exception e) {
            log.error("TPM令牌申请失败: userId={}, model={}, tokens={}",
                    memberUserId, modelName, tokens, e);
        }
    }

    private SseEmitter handleStream(Long memberUserId, ChatCompletionRequest request, String action,
                                    Function<String, CompletionResult> lineProcessor) throws IOException {
        logRequest(action, request);
        ModelRepositoryDO model = validateModelAndApplyRateLimit(memberUserId, request);
        ChatModel chatModel = modelRepositoryService.getChatModel(request.getModel());
        HttpResponse httpResponse = chatModel.stream(request, action);

        SseEmitter emitter = new SseEmitter(SSE_TIME_OUT);
        List<CompletionResult> responseList = new ArrayList<>();

        executor.execute(() -> processStreamResponse(
                httpResponse, emitter, responseList, lineProcessor,
                () -> chargeAndRecordTask(memberUserId, request, responseList, model),
                memberUserId,
                model
        ));

        return emitter;
    }

    /**
     * 处理标准流数据行
     */
    private CompletionResult processStandardStreamLine(String data) {
        return JSONObject.parseObject(data, CompletionResult.class);
    }

    /**
     * 处理Web流数据行
     */
    private CompletionResult processWebStreamLine(String data) {
        CompletionResult result = JSONObject.parseObject(data, CompletionResult.class);

        if (!result.getChoices().isEmpty()) {
            Delta delta = result.getChoices().get(0).getDelta();
            if (StrUtil.isNotBlank(delta.getReasoningContent())) {
                delta.setType("thinking");
                delta.setContent(delta.getReasoningContent());
                delta.setReasoningContent(null);
            } else {
                delta.setType("text");
            }
        }

        return result;
    }

    /**
     * 计费并记录任务（非流式）
     */
    private void chargeAndRecordTask(Long memberUserId, ChatCompletionRequest request,
                                     CompletionResult result, ModelRepositoryDO model, long tokens) {
        long fee = calculateFee(tokens, model.getInferencePrice());
        if (model.getInferencePrice() > 0) {
            memberApi.changeBalance(memberUserId, -fee);
        }

        recordTask(memberUserId, request, model, result.toString(), tokens, fee);
    }

    /**
     * 计费并记录任务（流式）
     */
    private void chargeAndRecordTask(Long memberUserId, ChatCompletionRequest request,
                                     List<CompletionResult> responseList, ModelRepositoryDO model) {
        long tokens = calculateTotalTokens(responseList);
        long fee = calculateFee(tokens, model.getInferencePrice());

        if (model.getInferencePrice() > 0) {
            memberApi.changeBalance(memberUserId, -fee);
        }

        recordTask(memberUserId, request, model, buildResponseContent(responseList), tokens, fee);
    }

    /**
     * 计算总token数（非流式）
     */
    private long calculateTotalTokens(CompletionResult result) {
        if (result.getUsage() != null && result.getUsage().getTotalTokens() > 0) {
            return result.getUsage().getTotalTokens();
        }
        return result.getChoices().stream()
                .mapToLong(choice -> TokenUtils.calculateTokens(choice.getText()))
                .sum();
    }

    /**
     * 计算总token数（流式）
     */
    private long calculateTotalTokens(List<CompletionResult> responseList) {
        return responseList.stream()
                .mapToLong(result -> {
                    if (result.getUsage() != null && result.getUsage().getTotalTokens() > 0) {
                        return result.getUsage().getTotalTokens();
                    }
                    return result.getChoices().stream()
                            .mapToLong(choice -> {
                                String content = choice.getDelta() != null ?
                                        choice.getDelta().getContent() + choice.getDelta().getReasoningContent() :
                                        "";
                                return TokenUtils.calculateTokens(content);
                            })
                            .sum();
                })
                .sum();
    }

    /**
     * 构建响应内容
     */
    private String buildResponseContent(List<CompletionResult> responseList) {
        StringBuilder content = new StringBuilder();
        responseList.forEach(result ->
                result.getChoices().forEach(choice -> {
                    if (choice.getDelta() != null) {
                        content.append(choice.getDelta().getContent());
                        if (choice.getDelta().getReasoningContent() != null) {
                            content.append(choice.getDelta().getReasoningContent());
                        }
                    }
                })
        );
        return content.toString();
    }

    /**
     * 计算费用
     */
    private long calculateFee(long tokens, Integer unitPrice) {
        if (tokens <= 0 || unitPrice == null || unitPrice <= 0) {
            return 0;
        }
        long units = (long) Math.ceil((double) tokens / 1000);
        return units * unitPrice;
    }

    /**
     * 记录任务
     */
    private void recordTask(Long memberUserId, ChatCompletionRequest request,
                            ModelRepositoryDO model, String response,
                            long tokens, long fee) {
        TaskDO task = TaskDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .userId(memberUserId)
                .modelId(String.valueOf(model.getId()))
                .modelName(model.getName())
                .modelType(model.getType())
                .version(model.getVersion())
                .requestBody(JSONObject.toJSONString(request))
                .responseBody(response)
                .chargeMode(model.getChargeMode())
                .unitPrice(new BigDecimal(model.getInferencePrice()))
                .callTime(LocalDateTime.now())
                .totalFee(new BigDecimal(fee))
                .tokens(tokens)
                .build();
        log.error("total tokens: {}", tokens);
        taskMapper.insert(task);
    }

    /**
     * 解析响应
     */
    private CompletionResult parseResponse(String response) {
        try {
            return JSONObject.parseObject(response, CompletionResult.class);
        } catch (Exception e) {
            log.error("解析响应异常: {}", response, e);
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "An unexpected server error occurred");
        }
    }

    /**
     * 记录请求日志
     */
    private void logRequest(String action, ChatCompletionRequest request) {
        if (log.isInfoEnabled()) {
            log.info("{} 请求: model={}, stream={}", action, request.getModel(), request.getStream());
        }
    }
}