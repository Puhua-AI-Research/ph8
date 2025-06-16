package com.puhua.module.ai.api.vllm.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private String type;

    private String text;
    @JSONField(name = "image_url")
    @JsonProperty("image_url")
    private  ImageUrl imageUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUrl {
        @JSONField(name = "url")
        @JsonProperty("url")
        private String url;
    }
}
