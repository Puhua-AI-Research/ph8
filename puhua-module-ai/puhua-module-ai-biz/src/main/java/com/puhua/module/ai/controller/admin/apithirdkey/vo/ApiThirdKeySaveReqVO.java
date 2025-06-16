package com.puhua.module.ai.controller.admin.apithirdkey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 三方平台apiKey新增/修改 Request VO")
@Data
public class ApiThirdKeySaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "3205")
    private Long id;

    @Schema(description = "资源标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "7727")
    @NotEmpty(message = "资源标识不能为空")
    private String resourceId;

    @Schema(description = "ak")
    private String ak;

    @Schema(description = "sk")
    private String sk;

    @Schema(description = "key")
    private String apiKey;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}