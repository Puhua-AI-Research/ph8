package com.puhua.module.ai.controller.app.modelRepository.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "C端 - 模型广场 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ModelRepositoryRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "30380")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("模型名称")
    private String name;

    @Schema(description = "最大token数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "最大token数", converter = DictConvert.class)
    private Integer  maxTokens;

    @Schema(description = "模型分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "模型分类", converter = DictConvert.class)
    @DictFormat("model_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String type;

    @Schema(description = "模型中文名称", example = "赵六")
    @ExcelProperty("模型中文名称")
    private String modelName;

    @JsonIgnore
    private String labels;

    @Schema(description = "标签列表", example = "赵六")
    @ExcelProperty("标签列表")
    private List<String> labelList;

    @Schema(description = "模型简介")
    @ExcelProperty("模型简介")
    private String briefIntroduction;

    @Schema(description = "封面logo", example = "https://ph8.co")
    @ExcelProperty("封面logo")
    private String coverLogo;

    @Schema(description = "封面", example = "https://ph8.co")
    @ExcelProperty("封面")
    private String coverUrl;

    @JsonIgnore
    private String images;


    @Schema(description = "图片列表")
    @ExcelProperty("图片列表")
    private List<String> imageList;

    @JsonIgnore
    private String tags;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String tag;

    @Schema(description = "模型介绍")
    @ExcelProperty("模型介绍")
    private String introduction;

    @Schema(description = "原价", example = "8")
    @ExcelProperty("原价")
    private Integer originalPrice;

    private String originalPriceStr;

    @Schema(description = "推理费用", example = "1")
    @ExcelProperty("推理费用")
    private Integer inferencePrice;

    private String inferencePriceStr;

    @Schema(description = "计费方式")
    @ExcelProperty(value = "计费方式", converter = DictConvert.class)
    @DictFormat("模型计费方式") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String chargeMode;

    @Schema(description = "结算周期")
    @ExcelProperty(value = "结算周期", converter = DictConvert.class)
    @DictFormat("model_settlement_interval") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String settlementInterval;

    @Schema(description = "示例")
    @ExcelProperty("示例")
    private String example;

    @Schema(description = "curl示例")
    @ExcelProperty("curl示例")
    private String curlExample;

    @Schema(description = "版本号")
    @ExcelProperty("版本号")
    private String version;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    private LocalDateTime updateDate;

    @Schema(description = "更新内容")
    @ExcelProperty("更新内容")
    private String updateContent;

    @Schema(description = "模型id", example = "26923")
    @ExcelProperty("模型id")
    private String modelId;

    @Schema(description = "上线时间")
    @ExcelProperty("上线时间")
    private LocalDateTime pubTime;

    @JsonIgnore
    private String supportChips;

    @Schema(description = "支持芯片类型")
    @ExcelProperty("支持芯片类型")
    private List<String> supportChipList;

}