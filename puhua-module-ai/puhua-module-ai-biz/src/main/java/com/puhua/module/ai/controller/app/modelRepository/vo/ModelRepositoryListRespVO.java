package com.puhua.module.ai.controller.app.modelRepository.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "APP - 模型名称列表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ModelRepositoryListRespVO {

    private Long id;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Qwen")
    @ExcelProperty("模型名称")
    private String name;


    @Schema(description = "模型分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "模型分类", converter = DictConvert.class)
    @DictFormat("model_type")
    private String type;

    /**
     * 最大token数
     */
    private Integer maxTokens;


}