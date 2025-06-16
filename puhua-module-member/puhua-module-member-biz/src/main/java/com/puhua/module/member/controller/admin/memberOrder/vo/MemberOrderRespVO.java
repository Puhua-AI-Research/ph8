package com.puhua.module.member.controller.admin.memberOrder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 额度订单信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MemberOrderRespVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29717")
    @ExcelProperty("订单编号")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1564")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16754")
    @ExcelProperty("商品编号")
    private Long spuId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("商品名称")
    private String spuName;

    @Schema(description = "价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "12890")
    @ExcelProperty("价格，单位：分")
    private Integer price;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "支付状态", converter = DictConvert.class)
    @DictFormat("pay_order_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer payStatus;

    @Schema(description = "支付订单编号", example = "23449")
    @ExcelProperty("支付订单编号")
    private Long payOrderId;

    @Schema(description = "付款时间")
    @ExcelProperty("付款时间")
    private LocalDateTime payTime;

    @Schema(description = "支付渠道")
    @ExcelProperty(value = "支付渠道", converter = DictConvert.class)
    @DictFormat("pay_channel_code") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String payChannelCode;

    @Schema(description = "支付退款单号", example = "22994")
    @ExcelProperty("支付退款单号")
    private Long payRefundId;

    @Schema(description = "退款金额，单位：分", example = "28986")
    @ExcelProperty("退款金额，单位：分")
    private Integer refundPrice;

    @Schema(description = "退款完成时间")
    @ExcelProperty("退款完成时间")
    private LocalDateTime refundTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}