package com.puhua.module.member.controller.admin.quotaProduct.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户积分配额商品新增/修改 Request VO")
@Data
public class QuotaProductSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32575")
    private Long id;

    @Schema(description = "商品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21654")
    @NotEmpty(message = "商品id不能为空")
    private String productId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中航普华")
    @NotEmpty(message = "商品名称不能为空")
    private String name;

    @Schema(description = "配额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配额不能为空")
    private Integer quota;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "22485")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Schema(description = "原价", example = "5284")
    private BigDecimal originalPrice;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "描述", example = "你猜")
    private String description;

}