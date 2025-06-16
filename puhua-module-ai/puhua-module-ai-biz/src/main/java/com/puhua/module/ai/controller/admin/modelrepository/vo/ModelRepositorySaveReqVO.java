package com.puhua.module.ai.controller.admin.modelrepository.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 模型库新增/修改 Request VO")
@Data
public class ModelRepositorySaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "9147")
    private Long id;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "模型名称不能为空")
    private String name;

    @Schema(description = "api地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://ph8.co")
    @NotEmpty(message = "api地址不能为空")
    private String url;

    @Schema(description = "模型分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "模型分类不能为空")
    private String type;

    @Schema(description = "密钥")
    private String apiKey;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "原价", example = "9767")
    private Integer originalPrice;

    @Schema(description = "模型中文名称", example = "赵六")
    private String modelName;

    @Schema(description = "推理费用", example = "17220")
    private String inferencePrice;

    @Schema(description = "模型简介")
    private String briefIntroduction;

    @Schema(description = "计费方式")
    private String chargeMode;

    @Schema(description = "封面logo", example = "https://ph8.co")
    private String coverLogo;

    @Schema(description = "封面", example = "https://ph8.co")
    private String coverUrl;

    @Schema(description = "结算周期")
    private String settlementInterval;

    @Schema(description = "示例")
    private String example;

    @Schema(description = "图片列表")
    private String images;

    @Schema(description = "curl示例")
    private String curlExample;

    @Schema(description = "模型介绍")
    private String introduction;

    @Schema(description = "标签")
    private String labels;

    @Schema(description = "tags")
    private String tags;

    @Schema(description = "厂商", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "厂商不能为空")
    private String manufacturers;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "更新时间")
    private LocalDateTime updateDate;

    @Schema(description = "更新内容")
    private String updateContent;

    @Schema(description = "模型id", example = "11176")
    private String modelId;

    @Schema(description = "上线时间")
    private LocalDateTime pubTime;

    @Schema(description = "支持芯片类型")
    private String supportChips;

}