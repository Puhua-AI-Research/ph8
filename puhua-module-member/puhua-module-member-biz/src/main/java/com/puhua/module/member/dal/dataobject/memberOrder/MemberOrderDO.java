package com.puhua.module.member.dal.dataobject.memberOrder;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * 额度订单信息 DO
 *
 * @author 中航普华
 */
@TableName("member_order")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOrderDO extends BaseDO {

    /**
     * 订单编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 商品编号
     */
    private Long spuId;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 价格，单位：分
     */
    private Integer price;
    /**
     * 支付状态
     *
     * 枚举 {@link TODO pay_order_status 对应的类}
     */
    private Integer payStatus;
    /**
     * 支付订单编号
     */
    private Long payOrderId;
    /**
     * 付款时间
     */
    private LocalDateTime payTime;
    /**
     * 支付渠道
     *
     * 枚举 {@link TODO pay_channel_code 对应的类}
     */
    private String payChannelCode;
    /**
     * 支付退款单号
     */
    private Long payRefundId;
    /**
     * 退款金额，单位：分
     */
    private Integer refundPrice;
    /**
     * 退款完成时间
     */
    private LocalDateTime refundTime;

}