package com.puhua.module.ai.api.text2img.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VEGenerateImageReqVo {
    @JSONField(name = "req_key")
    private String reqKey;

    @JSONField(name = "prompt")
    private String prompt;

    @JSONField(name = "model_version")
    private String modelVersion;

    @JSONField(name = "req_schedule_conf")
    private String reqScheduleConf;

    @JSONField(name = "llm_seed")
    private Integer llmSeed = -1;

    @JSONField(name = "seed")
    private Integer seed = -1;

    @JSONField(name = "scale")
    private Double scale;

    @JSONField(name = "ddim_steps")
    private Integer ddimSteps;

    @JSONField(name = "width")
    private Integer width;

    @JSONField(name = "height")
    private Integer height;

    @JSONField(name = "use_pre_llm")
    private Boolean usePreLlm;

    @JSONField(name = "use_sr")
    private Boolean useSr;

    @JSONField(name = "return_url")
    private Boolean returnUrl;

    @JSONField(name = "logo_info")
    private LogoInfo logoInfo;

    @Data
    public static class LogoInfo {
        @JSONField(name = "add_logo")
        private Boolean addLogo;

        @JSONField(name = "position")
        private Integer position;

        @JSONField(name = "language")
        private Integer language;

        @JSONField(name = "opacity")
        private Double opacity;

        @JSONField(name = "logo_text_content")
        private String logoTextContent;
    }
}