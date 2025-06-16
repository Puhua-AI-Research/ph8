package com.puhua.module.ai.controller.app.apiKey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "C端 - ApiKey管理新增/修改 Request VO")
@Data
public class AppApiKeyCreateReqVO {

    @Schema(description = "name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "name不能为空")
    private String name;
}