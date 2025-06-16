package com.puhua.module.ai.api.text2video.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerateVideoResponseVo {

    private String url;

    @JSONField(name = "revised_prompt")
    @JsonProperty("revised_prompt")
    private String revised_prompt;

}