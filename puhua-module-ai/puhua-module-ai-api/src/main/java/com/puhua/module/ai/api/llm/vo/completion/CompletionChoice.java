package com.puhua.module.ai.api.llm.vo.completion;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A completion generated by OpenAI
 * <p>
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class CompletionChoice {
    /**
     * This index of this completion in the returned list.
     */
    private Integer index;

    /**
     * This text of this completion.
     */
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Delta delta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Delta message;
    /**
     * The reason why GPT stopped generating, for example "length".
     */
    @JSONField(name = "finish_reason")
    @JsonProperty("finish_reason")
    private String finishReason;
}
