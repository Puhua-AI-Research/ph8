package com.puhua.module.ai.controller.admin.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户任务计费 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TaskRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "26002")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27431")
    @ExcelProperty("用户id")
    private Long userId;

    @Schema(description = "模型id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23575")
    @ExcelProperty("模型id")
    private String modelId;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("模型名称")
    private String modelName;

    @Schema(description = "模型类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "模型类型", converter = DictConvert.class)
    @DictFormat("model_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String modelType;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    private String version;

    @Schema(description = "请求内容")
    @ExcelProperty("请求内容")
    private String requestBody;

    @Schema(description = "返回内容")
    @ExcelProperty("返回内容")
    private String responseBody;

    @Schema(description = "计费方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "计费方式", converter = DictConvert.class)
    @DictFormat("model_charge_mode") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String chargeMode;

    @Schema(description = "调用单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "16412")
    @ExcelProperty("调用单价")
    private BigDecimal unitPrice;

    @Schema(description = "调用时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("调用时间")
    private LocalDateTime callTime;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("金额")
    private BigDecimal totalFee;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}