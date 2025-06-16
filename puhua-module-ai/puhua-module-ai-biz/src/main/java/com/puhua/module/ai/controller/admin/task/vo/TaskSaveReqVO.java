package com.puhua.module.ai.controller.admin.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户任务计费新增/修改 Request VO")
@Data
public class TaskSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "26002")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27431")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @Schema(description = "模型id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23575")
    @NotEmpty(message = "模型id不能为空")
    private String modelId;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "模型名称不能为空")
    private String modelName;

    @Schema(description = "模型类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "模型类型不能为空")
    private String modelType;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "版本号不能为空")
    private String version;

    @Schema(description = "请求内容")
    private String requestBody;

    @Schema(description = "返回内容")
    private String responseBody;

    @Schema(description = "计费方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "计费方式不能为空")
    private String chargeMode;

    @Schema(description = "调用单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "16412")
    @NotNull(message = "调用单价不能为空")
    private BigDecimal unitPrice;

    @Schema(description = "调用时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "调用时间不能为空")
    private LocalDateTime callTime;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    private BigDecimal totalFee;

}