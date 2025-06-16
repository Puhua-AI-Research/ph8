package com.puhua.module.ai.controller.app.apiKey.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "C端 - ApiKey管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppApiKeyRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27338")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "key", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("key")
    private String apiKey;

    @Schema(description = "name", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("name")
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("enable_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "最后使用时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最后使用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime lastUseTime;

}