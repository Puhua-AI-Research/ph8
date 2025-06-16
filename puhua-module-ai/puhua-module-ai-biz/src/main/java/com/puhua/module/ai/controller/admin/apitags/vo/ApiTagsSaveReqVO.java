package com.puhua.module.ai.controller.admin.apitags.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.*;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 模型tag新增/修改 Request VO")
@Data
public class ApiTagsSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13069")
    private Long id;

    @Schema(description = "名称", example = "中航普华")
    private String name;

    @Schema(description = "分类", example = "1")
    private String type;

    @Schema(description = "分组", example = "1")
    private String groupName;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

}