package com.puhua.module.ai.controller.admin.apiKey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.*;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - ApiKey管理新增/修改 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeySaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27338")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1986")
    @NotNull(message = "用户id不能为空")
    private Long memberUserId;

    @Schema(description = "key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "key不能为空")
    private String apiKey;

    @Schema(description = "name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}