package com.puhua.module.member.controller.app.quotaProduct.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "C端 - 用户积分配额商品列表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppQuotaProductRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32575")
    private Long id;

    @Schema(description = "商品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21654")
    private String productId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中航普华")
    private String name;

    @Schema(description = "配额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quota;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "22485")
    private BigDecimal price;

    @Schema(description = "原价", example = "5284")
    private BigDecimal originalPrice;

    @Schema(description = "描述", example = "你猜")
    private String description;

}