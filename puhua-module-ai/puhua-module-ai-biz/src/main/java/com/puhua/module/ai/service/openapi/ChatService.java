package com.puhua.module.ai.service.openapi;

import com.puhua.module.ai.api.llm.vo.completion.CompletionResult;
import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface ChatService {
    /**
     * 普通文本输出
     *
     * @return res
     */
    CompletionResult nonStream(Long MemberUserId, ChatCompletionRequest chatCompletionRequest,String action) throws IOException;

    /**
     * 流式输出（原生）
     *
     * @return sse
     */
    SseEmitter stream(Long MemberUserId, ChatCompletionRequest chatCompletionRequest,String action) throws IOException;

    /**
     * 流式输出（原生、web,深度思考特殊处理）
     *
     * @return sse
     */
    SseEmitter webStream(Long MemberUserId, ChatCompletionRequest chatCompletionRequest) throws IOException;


}
