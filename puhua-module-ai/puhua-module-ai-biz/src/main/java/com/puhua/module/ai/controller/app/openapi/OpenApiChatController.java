package com.puhua.module.ai.controller.app.openapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.enums.ModelType;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import com.puhua.module.ai.service.openapi.ChatService;
import com.puhua.module.ai.service.openapi.VllmService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("ai")
public class OpenApiChatController {

    @Resource
    private ApiKeyService apiKeyService;

    @Resource
    VllmService vllmService;

    @Resource
    MemberApi memberApi;

    @Resource
    ChatService chatService;

    // 策略接口
    private interface ResponseHandler {
        Object handleRequest(Long userId, Object request, String action) throws IOException;
    }

    // 流式LLM处理器
    private class StreamLlmHandler implements ResponseHandler {
        @Override
        public Object handleRequest(Long userId, Object request, String action) throws IOException {
            return chatService.stream(userId, (ChatCompletionRequest) request, action);
        }
    }

    // 非流式LLM处理器
    private class NonStreamLlmHandler implements ResponseHandler {
        @Override
        public Object handleRequest(Long userId, Object request, String action) throws IOException {
            return chatService.nonStream(userId, (ChatCompletionRequest) request, action);
        }
    }

    // 流式VLLM处理器
    private class StreamVllmHandler implements ResponseHandler {
        @Override
        public Object handleRequest(Long userId, Object request, String action) throws IOException {
            return vllmService.stream(userId, (VllmRequestVo) request);
        }
    }

    // 非流式VLLM处理器
    private class NonStreamVllmHandler implements ResponseHandler {
        @Override
        public Object handleRequest(Long userId, Object request, String action) throws IOException {
            return vllmService.nonStream(userId, (VllmRequestVo) request);
        }
    }

    // 获取处理器
    private ResponseHandler getHandler(String modelType, boolean isStream) {
        if (ModelType.LLM.getType().equals(modelType)) {
            return isStream ? new StreamLlmHandler() : new NonStreamLlmHandler();
        } else {
            return isStream ? new StreamVllmHandler() : new NonStreamVllmHandler();
        }
    }

    // 统一验证方法
    private MemberUserInfoRespVO validateRequest(String apiKey) {
        if (StrUtil.isBlank(apiKey)) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid token");
        }

        // 清理API Key
        String cleanApiKey = apiKey.replace("Bearer ", "");

        // 验证API Key
        ApiKeyDO apiKeyDO = apiKeyService.getApiKey(cleanApiKey);
        if (apiKeyDO == null) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid API key");
        }
        if (CommonStatusEnum.isDisable(apiKeyDO.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "API key is disabled");
        }

        // 验证用户
        MemberUserInfoRespVO memberUser = memberApi.getMemberUser(apiKeyDO.getMemberUserId());
        if (memberUser == null) {
            throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "User not found");
        }
        return memberUser;
    }

    @Operation(summary = "对话")
    @PostMapping("chat/completions")
    @PermitAll
    public Object chatCompletions(
            @RequestHeader("Authorization") String apiKey,
            @RequestBody JSONObject chatCompletionRequest) throws IOException {

        // 验证请求并获取用户信息
        MemberUserInfoRespVO user = validateRequest(apiKey);

        // 检测模型类型
        String modelType = detectModelType(chatCompletionRequest);
        if (modelType == null) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST,
                    "Unsupported model type or invalid request format");
        }

        // 获取请求类型
        boolean isStream = chatCompletionRequest.getBoolean("stream");

        // 创建请求对象
        Object requestObj = ModelType.LLM.getType().equals(modelType) ?
                JSONObject.toJavaObject(chatCompletionRequest, ChatCompletionRequest.class) :
                JSONObject.toJavaObject(chatCompletionRequest, VllmRequestVo.class);

        // 获取处理器并处理请求
        ResponseHandler handler = getHandler(modelType, isStream);
        return handler.handleRequest(user.getId(), requestObj, "chat");
    }

    @Operation(summary = "补全")
    @PostMapping("completions")
    @PermitAll
    public Object completions(
            @RequestHeader("Authorization") String apiKey,
            @RequestBody ChatCompletionRequest request) throws IOException {

        // 验证请求并获取用户信息
        MemberUserInfoRespVO user = validateRequest(apiKey);

        // 校验参数
        validatePenalty(request.getFrequencyPenalty(), request::setFrequencyPenalty);
        validatePenalty(request.getPresencePenalty(), request::setPresencePenalty);

        // 获取处理器
        ResponseHandler handler = request.getStream() ?
                new StreamLlmHandler() : new NonStreamLlmHandler();

        return handler.handleRequest(user.getId(), request, "complement");
    }

    // 辅助方法：验证惩罚参数
    private void validatePenalty(Double value, java.util.function.Consumer<Double> setter) {
        if (value != null && (value < 0.0 || value > 2.0)) {
            setter.accept(1.0); // 设置默认值
        }
    }

    // 模型检测方法
    public String detectModelType(JSONObject request) {
        try {
            if (!request.containsKey("messages")) return null;

            Object messagesObj = request.get("messages");
            if (!(messagesObj instanceof ArrayList)) return null;

            ArrayList<?> messages = (ArrayList<?>) messagesObj;
            if (messages.isEmpty()) return null;

            Object firstMessage = messages.get(0);
            if (!(firstMessage instanceof LinkedHashMap)) return null;

            LinkedHashMap<?, ?> message = (LinkedHashMap) firstMessage;
            Object content = message.get("content");

            if (content instanceof String) return ModelType.LLM.getType();
            if (content instanceof List) return ModelType.VLLM.getType();
        } catch (Exception e) {
            // 日志记录
        }
        return null;
    }
}