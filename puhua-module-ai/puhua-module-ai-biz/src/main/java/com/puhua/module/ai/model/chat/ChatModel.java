package com.puhua.module.ai.model.chat;

import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface ChatModel {

    /**
     * 普通文本输出
     *
     * @return str
     */
    public String chat(ChatCompletionRequest chatCompletionRequest, String action);

    /**
     * 流式输出（原生）
     *
     * @return sse
     */
    public HttpResponse stream(ChatCompletionRequest chatCompletionRequest, String action) throws IOException;
}
