package com.puhua.module.member.controller.admin.memberOrder.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.puhua.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 额度订单信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MemberOrderPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1564")
    private Long userId;

    @Schema(description = "商品编号", example = "16754")
    private Long spuId;

    @Schema(description = "商品名称", example = "王五")
    private String spuName;

    @Schema(description = "支付状态", example = "2")
    private Integer payStatus;

    @Schema(description = "支付订单编号", example = "23449")
    private Long payOrderId;

    @Schema(description = "付款时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] payTime;

    @Schema(description = "支付渠道")
    private String payChannelCode;

    @Schema(description = "支付退款单号", example = "22994")
    private Long payRefundId;

    @Schema(description = "退款金额，单位：分", example = "28986")
    private Integer refundPrice;

    @Schema(description = "退款完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] refundTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}