package com.puhua.module.ai.controller.admin.apitags.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 模型tag Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiTagsRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13069")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "名称", example = "中航普华")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "分类", example = "1")
    @ExcelProperty("分类")
    private String type;

    @Schema(description = "分组", example = "1")
    @ExcelProperty("分组")
    private String groupName;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("enable_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}