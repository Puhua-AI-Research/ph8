package com.puhua.module.member.service.memberOrder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.controller.admin.memberOrder.vo.MemberOrderPageReqVO;
import com.puhua.module.member.controller.admin.memberOrder.vo.MemberOrderSaveReqVO;
import com.puhua.module.member.controller.app.memberOrder.vo.AppMemberOrderCreateReqVO;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import com.puhua.module.member.dal.dataobject.memberOrder.MemberOrderDO;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.dal.mysql.memberBalancelog.BalanceLogMapper;
import com.puhua.module.member.dal.mysql.memberOrder.MemberOrderMapper;
import com.puhua.module.member.dal.mysql.user.MemberMapper;
import com.puhua.module.member.enums.BusinessModeEnum;
import com.puhua.module.member.service.quotaProduct.QuotaProductService;
import com.puhua.module.pay.api.order.PayOrderApi;
import com.puhua.module.pay.api.order.dto.PayOrderCreateReqDTO;
import com.puhua.module.pay.api.order.dto.PayOrderRespDTO;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitReqDto;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitRespDto;
import com.puhua.module.pay.enums.order.PayOrderStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.framework.common.util.date.LocalDateTimeUtils.addTime;
import static com.puhua.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.module.member.enums.ErrorCodeConstants.*;

/**
 * 额度订单信息 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Slf4j
@Validated
public class MemberOrderServiceImpl implements MemberOrderService {

    private static final String PAY_APP_KEY = "api-platform";

    @Resource
    private PayOrderApi payOrderApi;

    @Resource
    private QuotaProductService productService;

    @Resource
    private MemberOrderMapper orderMapper;
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private MemberOrderMapper memberOrderMapper;
    @Resource
    private BalanceLogMapper balanceLogMapper;

    @Override
    public Long createOrder(MemberOrderSaveReqVO createReqVO) {
        // 插入
        MemberOrderDO order = BeanUtils.toBean(createReqVO, MemberOrderDO.class);
        orderMapper.insert(order);
        // 返回
        return order.getId();
    }

    @Override
    public void updateOrder(MemberOrderSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderExists(updateReqVO.getId());
        // 更新
        MemberOrderDO updateObj = BeanUtils.toBean(updateReqVO, MemberOrderDO.class);
        orderMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrder(Long id) {
        // 校验存在
        validateOrderExists(id);
        // 删除
        orderMapper.deleteById(id);
    }

    private void validateOrderExists(Long id) {
        MemberOrderDO memberOrderDO = orderMapper.selectById(id);
        if (memberOrderDO == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
    }

    @Override
    public MemberOrderDO getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public PageResult<MemberOrderDO> getOrderPage(MemberOrderPageReqVO pageReqVO) {
        return orderMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<PayOrderSubmitRespDto> createAppMemberOrder(Long loginUserId, AppMemberOrderCreateReqVO createReqVO) {
        String clientIP = getClientIP();

        // 1.校验商品
        QuotaProductDO product = productService.getQuotaProductByProductId(createReqVO.getSpuId());
        if (product == null) {
            throw exception(QUOTA_PRODUCT_NOT_EXISTS);
        }
        String spuName = product.getName();
        Integer price = product.getPrice().multiply(new BigDecimal(100)).intValue();

        // 2.订单
        MemberOrderDO memberOrderDO = MemberOrderDO
                .builder()
                .userId(loginUserId)
                .spuId(product.getProductId())
                .spuName(spuName)
                .price(price)
                .payStatus(PayOrderStatusEnum.WAITING.getStatus())
                .refundPrice(0)
                .build();

        memberOrderMapper.insert(memberOrderDO);

        // 2.1 创建支付单
        Long payOrderId = payOrderApi.createOrder(new PayOrderCreateReqDTO()
                .setAppKey(PAY_APP_KEY).setUserIp(clientIP) // 支付应用
                .setMerchantOrderId(memberOrderDO.getId().toString()) // 业务的订单编号
                .setSubject(spuName).setBody(product.getDescription()).setPrice(price) // 价格信息
                .setExpireTime(addTime(Duration.ofHours(2L)))).getCheckedData(); // 支付的过期时间
        // 2.2 更新支付单到 业务 订单
        memberOrderMapper.updateById(new MemberOrderDO().setId(memberOrderDO.getId())
                .setPayOrderId(payOrderId));
        String payChannel = getPayChannel(createReqVO.getPayChannelCode());
        PayOrderSubmitReqDto payOrderSubmitReqDto = PayOrderSubmitReqDto
                .builder()
                .userIp(clientIP)
                .channelCode(payChannel)
                .id(payOrderId)
                .build();

        // 返回
        CommonResult<PayOrderSubmitRespDto> result = payOrderApi.submitOrder(payOrderSubmitReqDto);
        // 提交完后，设置订单id
        result.getData().setOrderId(memberOrderDO.getId());
        return result;
    }

    @Override
    public MemberOrderDO getOrderByPayOrderId(Long payOrderId) {
        MemberOrderDO orderByPayOrderId = memberOrderMapper.getOrderByPayOrderId(payOrderId);
        if (orderByPayOrderId == null || !Objects.equals(orderByPayOrderId.getUserId(), getLoginUserId())) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return orderByPayOrderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkOrder(Long id, Long payOrderId) {
        // 1.1 校验订单是否存在
        validateOrderExists(id);
        // 加锁(单库单表，直接加行锁)
        MemberOrderDO order = orderMapper.selectByIdForUpdate(id);

        // 1.2 校验订单已支付
        if (PayOrderStatusEnum.isSuccess(order.getPayStatus())) {
            return;
        }

        // 2. 校验支付订单的合法性
        PayOrderRespDTO payOrder = validatePayOrderPaid(order, payOrderId);

        if (PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            order.setPayStatus(PayOrderStatusEnum.SUCCESS.getStatus());
            orderMapper.updateById(order);
            QuotaProductDO quotaProduct = productService.getQuotaProductByProductId(order.getSpuId());
            Integer quota = quotaProduct.getQuota();
            LambdaUpdateWrapper<MemberMapperDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(MemberMapperDO::getId, order.getUserId());
            updateWrapper.setSql(" balance = balance + " + quota);
            memberMapper.update(updateWrapper);

            // 存库
            BalanceLogDO balanceLogDO = BalanceLogDO
                    .builder()
                    .userId(order.getUserId())
                    .changeBalance(Long.valueOf(quota))
                    .businessMode(BusinessModeEnum.RECHARGE.getValue())
                    .logTime(new Date().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .changeType("recharge")
                    .build();
            balanceLogMapper.insert(balanceLogDO);
        }
    }

    /**
     * 校验支付订单的合法性
     *
     * @param order      交易订单
     * @param payOrderId 支付订单编号
     * @return 支付订单
     */
    private PayOrderRespDTO validatePayOrderPaid(MemberOrderDO order, Long payOrderId) {
        // 1. 校验支付单是否存在
        PayOrderRespDTO payOrder = payOrderApi.getOrder(payOrderId).getCheckedData();
        if (payOrder == null) {
            log.error("[validatePayOrderPaid][order({}) payOrder({}) 不存在，请进行处理！]", order.getId(), payOrderId);
            throw exception(ORDER_NOT_EXISTS);
        }

        // 2.1 校验支付单已支付
        if (!PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            log.error("[validatePayOrderPaid][order({}) payOrder({}) 未支付，请进行处理！payOrder 数据是：{}]",
                    order.getId(), payOrderId, JSONObject.toJSONString(payOrder));
            throw exception(ORDER_UPDATE_PAID_FAIL_PAY_ORDER_STATUS_NOT_SUCCESS);
        }
        // 2.2 校验支付金额一致
        if (ObjectUtil.notEqual(payOrder.getPrice(), order.getPrice())) {
            log.error("[validatePayOrderPaid][order({}) payOrder({}) 支付金额不匹配，请进行处理！order 数据是：{}，payOrder 数据是：{}]",
                    order.getId(), payOrderId, JSONObject.toJSONString(order), JSONObject.toJSONString(payOrder));
            throw exception(ORDER_UPDATE_PAID_FAIL_PAY_PRICE_NOT_MATCH);
        }
        // 2.2 校验支付订单匹配（二次）
        if (ObjectUtil.notEqual(payOrder.getMerchantOrderId(), order.getId().toString())) {
            log.error("[validatePayOrderPaid][order({}) 支付单不匹配({})，请进行处理！payOrder 数据是：{}]",
                    order.getId(), payOrderId, JSONObject.toJSONString(payOrder));
            throw exception(ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
        }
        return payOrder;
    }

    private String getPayChannel(String payMethod) {
        return switch (payMethod) {
            case "wx" -> "wx_native";
            case "alipay" -> "alipay_qr";
            default -> throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "无效的支付方式");
        };
    }

}