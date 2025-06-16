package com.puhua.module.ai.controller.app.apiKey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "C端 - ApiKey管理新增/修改 Request VO")
@Data
public class AppApiKeySaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27338")
    private Long id;

    @Schema(description = "name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}