package com.puhua.module.ai.api.vllm.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author ZhangYi
 * @Date 2024年12月23日 15:54
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VllmRequestVo {
    private String model;

    private List<Message> messages;

    private Boolean stream;

    private Double top_p;
    private String[] stop;
    private Integer n;

    private Double temperature;

    @JSONField(name = "max_tokens")
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JSONField(name = "presence_penalty")
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    @JSONField(name = "frequency_penalty")
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
}
