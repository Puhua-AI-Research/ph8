package com.puhua.module.pay.service.wallet;

import cn.hutool.core.lang.Assert;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.date.DateUtils;
import com.puhua.module.pay.controller.admin.wallet.vo.wallet.PayWalletPageReqVO;
import com.puhua.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import com.puhua.module.pay.dal.dataobject.refund.PayRefundDO;
import com.puhua.module.pay.dal.dataobject.wallet.PayWalletDO;
import com.puhua.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import com.puhua.module.pay.dal.mysql.wallet.PayWalletMapper;
import com.puhua.module.pay.dal.redis.wallet.PayWalletLockRedisDAO;
import com.puhua.module.pay.enums.wallet.PayWalletBizTypeEnum;
import com.puhua.module.pay.service.order.PayOrderService;
import com.puhua.module.pay.service.refund.PayRefundService;
import com.puhua.module.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.pay.enums.ErrorCodeConstants.*;
import static com.puhua.module.pay.enums.wallet.PayWalletBizTypeEnum.PAYMENT;
import static com.puhua.module.pay.enums.wallet.PayWalletBizTypeEnum.PAYMENT_REFUND;

/**
 * 钱包 Service 实现类
 *
 * @author jason
 */
@Service
@Slf4j
public class PayWalletServiceImpl implements PayWalletService {

    /**
     * 通知超时时间，单位：毫秒
     */
    public static final long UPDATE_TIMEOUT_MILLIS = 120 * DateUtils.SECOND_MILLIS;

    @Resource
    private PayWalletMapper walletMapper;
    @Resource
    private PayWalletLockRedisDAO lockRedisDAO;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private PayWalletTransactionService walletTransactionService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private PayOrderService orderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private PayRefundService refundService;

    @Override
    public PayWalletDO getOrCreateWallet(Long userId, Integer userType) {
        PayWalletDO wallet = walletMapper.selectByUserIdAndType(userId, userType);
        if (wallet == null) {
            wallet = new PayWalletDO().setUserId(userId).setUserType(userType)
                    .setBalance(0).setTotalExpense(0).setTotalRecharge(0);
            wallet.setCreateTime(LocalDateTime.now());
            walletMapper.insert(wallet);
        }
        return wallet;
    }

    @Override
    public PayWalletDO getWallet(Long walletId) {
        return walletMapper.selectById(walletId);
    }

    @Override
    public PageResult<PayWalletDO> getWalletPage(PayWalletPageReqVO pageReqVO) {
        return walletMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayWalletTransactionDO orderPay(Long userId, Integer userType, String outTradeNo, Integer price) {
        // 1. 判断支付交易拓展单是否存
        PayOrderExtensionDO orderExtension = orderService.getOrderExtensionByNo(outTradeNo);
        if (orderExtension == null) {
            throw exception(PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        PayWalletDO wallet = getOrCreateWallet(userId, userType);
        // 2. 扣减余额
        return reduceWalletBalance(wallet.getId(), orderExtension.getOrderId(), PAYMENT, price);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayWalletTransactionDO orderRefund(String outRefundNo, Integer refundPrice, String reason) {
        // 1.1 判断退款单是否存在
        PayRefundDO payRefund = refundService.getRefundByNo(outRefundNo);
        if (payRefund == null) {
            throw exception(REFUND_NOT_FOUND);
        }
        // 1.2 校验是否可以退款
        Long walletId = validateWalletCanRefund(payRefund.getId(), payRefund.getChannelOrderNo());
        PayWalletDO wallet = walletMapper.selectById(walletId);
        Assert.notNull(wallet, "钱包 {} 不存在", walletId);

        // 2. 增加余额
        return addWalletBalance(walletId, String.valueOf(payRefund.getId()), PAYMENT_REFUND, refundPrice);
    }

    /**
     * 校验是否能退款
     *
     * @param refundId 支付退款单 id
     * @param walletPayNo 钱包支付 no
     */
    private Long validateWalletCanRefund(Long refundId, String walletPayNo) {
        // 1. 校验钱包支付交易存在
        PayWalletTransactionDO walletTransaction = walletTransactionService.getWalletTransactionByNo(walletPayNo);
        if (walletTransaction == null) {
            throw exception(WALLET_TRANSACTION_NOT_FOUND);
        }
        // 2. 校验退款是否存在
        PayWalletTransactionDO refundTransaction = walletTransactionService.getWalletTransaction(
                String.valueOf(refundId), PAYMENT_REFUND);
        if (refundTransaction != null) {
            throw exception(WALLET_REFUND_EXIST);
        }
        return walletTransaction.getWalletId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public PayWalletTransactionDO reduceWalletBalance(Long walletId, Long bizId,
                                                      PayWalletBizTypeEnum bizType, Integer price) {
        // 1. 获取钱包
        PayWalletDO payWallet = getWallet(walletId);
        if (payWallet == null) {
            log.error("[reduceWalletBalance][用户钱包({})不存在]", walletId);
            throw exception(WALLET_NOT_FOUND);
        }

        // 2. 加锁，更新钱包余额（目的：避免钱包流水的并发更新时，余额变化不连贯）
        return lockRedisDAO.lock(walletId, UPDATE_TIMEOUT_MILLIS, () -> {
            // 2. 扣除余额
            int updateCounts;
            switch (bizType) {
                case PAYMENT: {
                    updateCounts = walletMapper.updateWhenConsumption(payWallet.getId(), price);
                    break;
                }
                case RECHARGE_REFUND: {
                    updateCounts = walletMapper.updateWhenRechargeRefund(payWallet.getId(), price);
                    break;
                }
                default: {
                    // TODO 其它类型待实现
                    throw new UnsupportedOperationException("待实现");
                }
            }
            if (updateCounts == 0) {
                throw exception(WALLET_BALANCE_NOT_ENOUGH);
            }

            // 3. 生成钱包流水
            Integer afterBalance = payWallet.getBalance() - price;
            WalletTransactionCreateReqBO bo = new WalletTransactionCreateReqBO().setWalletId(payWallet.getId())
                    .setPrice(-price).setBalance(afterBalance).setBizId(String.valueOf(bizId))
                    .setBizType(bizType.getType()).setTitle(bizType.getDescription());
            return walletTransactionService.createWalletTransaction(bo);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public PayWalletTransactionDO addWalletBalance(Long walletId, String bizId,
                                                   PayWalletBizTypeEnum bizType, Integer price) {
        // 1. 获取钱包
        PayWalletDO payWallet = getWallet(walletId);
        if (payWallet == null) {
            log.error("[addWalletBalance][用户钱包({})不存在]", walletId);
            throw exception(WALLET_NOT_FOUND);
        }

        // 2. 加锁，更新钱包余额（目的：避免钱包流水的并发更新时，余额变化不连贯）
        return lockRedisDAO.lock(walletId, UPDATE_TIMEOUT_MILLIS, () -> {
            // 3. 更新钱包金额
            switch (bizType) {
                case PAYMENT_REFUND: { // 退款更新
                    walletMapper.updateWhenConsumptionRefund(payWallet.getId(), price);
                    break;
                }
                case RECHARGE: { // 充值更新
                    walletMapper.updateWhenRecharge(payWallet.getId(), price);
                    break;
                }
                case UPDATE_BALANCE: // 更新余额
                case BROKERAGE_WITHDRAW: // 分佣提现
                    walletMapper.updateWhenAdd(payWallet.getId(), price);
                    break;
                default: {
                    throw new UnsupportedOperationException("待实现：" + bizType);
                }
            }

            // 4. 生成钱包流水
            WalletTransactionCreateReqBO transactionCreateReqBO = new WalletTransactionCreateReqBO()
                    .setWalletId(payWallet.getId()).setPrice(price).setBalance(payWallet.getBalance() + price)
                    .setBizId(bizId).setBizType(bizType.getType()).setTitle(bizType.getDescription());
            return walletTransactionService.createWalletTransaction(transactionCreateReqBO);
        });
    }

    @Override
    public void freezePrice(Long id, Integer price) {
        int updateCounts = walletMapper.freezePrice(id, price);
        if (updateCounts == 0) {
            throw exception(WALLET_BALANCE_NOT_ENOUGH);
        }
    }

    @Override
    public void unfreezePrice(Long id, Integer price) {
        int updateCounts = walletMapper.unFreezePrice(id, price);
        if (updateCounts == 0) {
            throw exception(WALLET_FREEZE_PRICE_NOT_ENOUGH);
        }
    }

}
