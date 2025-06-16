package com.puhua.module.ai.api.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户任务计费 Response VO")
@Data
public class TaskRespDto {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "26002")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27431")
    private Long userId;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalFee;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}