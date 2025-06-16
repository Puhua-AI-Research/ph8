package com.puhua.module.ai.controller.app.openapi.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolcengineApiResponse {
    @JSONField(name = "id")
    private String id;

    @JSONField(name = "model")
    private String model;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "created_at")
    private Long createdAt;

    @JSONField(name = "updated_at")
    private Long updatedAt;

    @JSONField(name = "content")
    private Content content;

    @JSONField(name = "usage")
    private Usage usage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        @JSONField(name = "video_url")
        private String videoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JSONField(name = "completion_tokens")
        private Long completionTokens;

        @JSONField(name = "total_tokens")
        private Long totalTokens;
    }
}