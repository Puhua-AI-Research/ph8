package com.puhua.module.ai.service.openapi;

import com.puhua.module.ai.api.llm.vo.completion.CompletionResult;
import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface VllmService {
    /**
     * 普通文本输出
     *
     * @return
     */
    public CompletionResult nonStream(Long MemberUserId, VllmRequestVo vllmRequestVo) throws IOException;

    /**
     * 流式输出（原生）
     *
     * @return
     */
    public SseEmitter stream(Long MemberUserId, VllmRequestVo vllmRequestVo) throws IOException;

    /**
     * 流式输出（web）
     *
     * @return
     */
    public SseEmitter webStream(Long MemberUserId, VllmRequestVo vllmRequestVo) throws IOException;


}
