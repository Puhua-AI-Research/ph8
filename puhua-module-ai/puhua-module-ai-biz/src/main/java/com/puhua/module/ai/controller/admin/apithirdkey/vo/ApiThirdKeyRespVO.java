package com.puhua.module.ai.controller.admin.apithirdkey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 三方平台apiKey Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiThirdKeyRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "3205")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "资源标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "7727")
    @ExcelProperty("资源标识")
    private String resourceId;

    @Schema(description = "ak")
    @ExcelProperty("ak")
    private String ak;

    @Schema(description = "sk")
    @ExcelProperty("sk")
    private String sk;

    @Schema(description = "key")
    @ExcelProperty("key")
    private String apiKey;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("enable_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}