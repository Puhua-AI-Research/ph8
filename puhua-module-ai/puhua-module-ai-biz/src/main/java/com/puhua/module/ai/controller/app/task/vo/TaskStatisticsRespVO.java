package com.puhua.module.ai.controller.app.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户任务计费 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatisticsRespVO {

    private Long totalModels;

    private Long totalTokens;

    private Integer totalApiTimes;

    private BigDecimal totalFee;
}