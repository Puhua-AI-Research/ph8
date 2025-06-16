package com.puhua.module.ai.api.text2img.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VEGenerateImageResponseVo {
    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "data")
    private VEGenerateImageResponseData data; // 顶层Data对象
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "request_id")
    private String requestId;
    @JSONField(name = "status")
    private Integer status;
    @JSONField(name = "time_elapsed")
    private String timeElapsed;

    @Data
    @NoArgsConstructor
    public static class VEGenerateImageResponseData {
        @JSONField(name = "algorithm_base_resp")
        private AlgorithmBaseResp algorithmBaseResp;
        @JSONField(name = "binary_data_base64")
        private List<String> binaryDataBase64;
        @JSONField(name = "image_urls")
        private List<String> imageUrls;
        @JSONField(name = "pe_result")
        private String peResult;
        @JSONField(name = "predict_tags_result")
        private String predictTagsResult;
        @JSONField(name = "rephraser_result")
        private String rephraserResult;
        @JSONField(name = "request_id")
        private String requestId;

        @Data
        @NoArgsConstructor
        public static class AlgorithmBaseResp {
            @JSONField(name = "status_code")
            private Integer statusCode;
            @JSONField(name = "status_message")
            private String statusMessage;
        }

    }
}