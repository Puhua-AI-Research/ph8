package com.puhua.module.ai.api.llm.vo.completion;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ZhangYi
 * @Date 2024年12月24日 10:33
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delta {
    private String role;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JSONField(name = "reasoning_content")
    @JsonProperty("reasoning_content")
    private String reasoningContent;

    private JSONArray tool_calls;
}
