package com.puhua.module.ai.controller.admin.task.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.puhua.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户任务计费分页 Request VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPageReqVO extends PageParam {

    @Schema(description = "用户id", example = "27431")
    private Long userId;

    @Schema(description = "模型id", example = "23575")
    private String modelId;

    @Schema(description = "模型名称", example = "王五")
    private String modelName;

    @Schema(description = "模型类型", example = "1")
    private String modelType;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "计费方式")
    private String chargeMode;

    @Schema(description = "调用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] callTime;

    @Schema(description = "金额")
    private BigDecimal totalFee;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}