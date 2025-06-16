package com.puhua.module.ai.api.llm.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The OpenAI resources used by a request
 */
@Data
public class Usage {
    /**
     * The number of prompt tokens used.
     */
    @JSONField(name = "prompt_tokens")
    @JsonProperty("prompt_tokens")
    long promptTokens;

    /**
     * The number of completion tokens used.
     */
    @JSONField(name = "completion_tokens")
    @JsonProperty("completion_tokens")
    long completionTokens;

    /**
     * The number of total tokens used
     */
    @JSONField(name = "total_tokens")
    @JsonProperty("total_tokens")
    long totalTokens;

    double tps;
}
