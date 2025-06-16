package com.puhua.module.ai.controller.app.apitags.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "C端 - 模型tag Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppApiTagsRespVO {

    @Schema(description = "名称", example = "中航普华")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "分类", example = "1")
    @ExcelProperty("分类")
    private String type;

}