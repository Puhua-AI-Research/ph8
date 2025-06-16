package com.puhua.module.member.controller.admin.memberOrder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 额度订单信息新增/修改 Request VO")
@Data
public class MemberOrderSaveReqVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29717")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1564")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16754")
    @NotNull(message = "商品编号不能为空")
    private Long spuId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "商品名称不能为空")
    private String spuName;

    @Schema(description = "价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "12890")
    @NotNull(message = "价格，单位：分不能为空")
    private Integer price;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "支付状态不能为空")
    private Integer payStatus;

    @Schema(description = "支付订单编号", example = "23449")
    private Long payOrderId;

    @Schema(description = "付款时间")
    private LocalDateTime payTime;

    @Schema(description = "支付渠道")
    private String payChannelCode;

    @Schema(description = "支付退款单号", example = "22994")
    private Long payRefundId;

    @Schema(description = "退款金额，单位：分", example = "28986")
    private Integer refundPrice;

    @Schema(description = "退款完成时间")
    private LocalDateTime refundTime;

}