package com.puhua.module.ai.api.llm.vo.completion.chat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A chat completion generated by OpenAI
 */
@Data
public class ChatCompletionChoice {

    /**
     * This index of this completion in the returned list.
     */
    Integer index;

    /**
     * The {@link ChatMessageRole#ASSISTANT} message or delta (when streaming) which was generated
     */
    @JsonAlias("delta")
    ChatMessage message;

    /**
     * The reason why GPT stopped generating, for example "length".
     */
    @JSONField(name = "finish_reason")
    @JsonProperty("finish_reason")
    String finishReason;
}
