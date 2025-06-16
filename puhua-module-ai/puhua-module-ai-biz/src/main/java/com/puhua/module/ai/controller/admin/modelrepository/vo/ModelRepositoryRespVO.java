package com.puhua.module.ai.controller.admin.modelrepository.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 模型库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ModelRepositoryRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "9147")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("模型名称")
    private String name;

    @Schema(description = "api地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://ph8.co")
    @ExcelProperty("api地址")
    private String url;

    @Schema(description = "模型分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "模型分类", converter = DictConvert.class)
    @DictFormat("model_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String type;

    @Schema(description = "密钥")
    @ExcelProperty("密钥")
    private String apiKey;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("enable_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "原价", example = "9767")
    @ExcelProperty("原价")
    private Integer originalPrice;

    @Schema(description = "模型中文名称", example = "赵六")
    @ExcelProperty("模型中文名称")
    private String modelName;

    @Schema(description = "推理费用", example = "17220")
    @ExcelProperty("推理费用")
    private String inferencePrice;

    @Schema(description = "模型简介")
    @ExcelProperty("模型简介")
    private String briefIntroduction;

    @Schema(description = "计费方式")
    @ExcelProperty(value = "计费方式", converter = DictConvert.class)
    @DictFormat("model_charge_mode") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String chargeMode;

    @Schema(description = "封面", example = "https://ph8.co")
    @ExcelProperty("封面")
    private String coverUrl;

    @Schema(description = "封面logo", example = "https://ph8.co")
    @ExcelProperty("封面logo")
    private String coverLogo;

    @Schema(description = "结算周期")
    @ExcelProperty(value = "结算周期", converter = DictConvert.class)
    @DictFormat("model_settlement_interval") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String settlementInterval;

    @Schema(description = "示例")
    @ExcelProperty("示例")
    private String example;

    @Schema(description = "图片列表")
    @ExcelProperty("图片列表")
    private String images;

    @Schema(description = "curl示例")
    @ExcelProperty("curl示例")
    private String curlExample;

    @Schema(description = "模型介绍")
    @ExcelProperty("模型介绍")
    private String introduction;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String labels;

    @Schema(description = "tags")
    @ExcelProperty("tags")
    private String tags;

    @Schema(description = "厂商", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("厂商")
    private String manufacturers;

    @Schema(description = "版本号")
    @ExcelProperty("版本号")
    private String version;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    private LocalDateTime updateDate;

    @Schema(description = "更新内容")
    @ExcelProperty("更新内容")
    private String updateContent;

    @Schema(description = "模型id", example = "11176")
    @ExcelProperty("模型id")
    private String modelId;

    @Schema(description = "上线时间")
    @ExcelProperty("上线时间")
    private LocalDateTime pubTime;

    @Schema(description = "支持芯片类型")
    @ExcelProperty(value = "支持芯片类型", converter = DictConvert.class)
    @DictFormat("model_chips") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String supportChips;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}