package com.puhua.module.member.controller.admin.quotaProduct.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户积分配额商品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QuotaProductRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32575")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "商品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21654")
    @ExcelProperty("商品id")
    private String productId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中航普华")
    @ExcelProperty("商品名称")
    private String name;

    @Schema(description = "配额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("配额")
    private Integer quota;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "22485")
    @ExcelProperty("价格")
    private BigDecimal price;

    @Schema(description = "原价", example = "5284")
    @ExcelProperty("原价")
    private BigDecimal originalPrice;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("member_product_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "描述", example = "你猜")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}