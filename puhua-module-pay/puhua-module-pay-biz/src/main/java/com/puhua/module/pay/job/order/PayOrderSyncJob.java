package com.puhua.module.pay.job.order;

import cn.hutool.core.util.StrUtil;
import com.puhua.framework.tenant.core.job.TenantJob;
import com.puhua.module.pay.service.order.PayOrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 支付订单的同步 Job
 *
 * 由于支付订单的状态，是由支付渠道异步通知进行同步，考虑到异步通知可能会失败（小概率），所以需要定时进行同步。
 *
 * @author 中航普华
 */
@Component
@Slf4j
public class PayOrderSyncJob {

    /**
     * 同步创建时间在 N 分钟之内的订单
     *
     * 为什么同步 10 分钟之内的订单？
     *  因为一个订单发起支付，到支付成功，大多数在 10 分钟内，需要保证轮询到。
     *  如果设置为 30、60 或者更大时间范围，会导致轮询的订单太多，影响性能。当然，你也可以根据自己的业务情况来处理。
     */
    private static final Duration CREATE_TIME_DURATION_BEFORE = Duration.ofMinutes(10);

    @Resource
    private PayOrderService orderService;

    @XxlJob("payOrderSyncJob")
    @TenantJob // 多租户
    public String execute() {
        LocalDateTime minCreateTime = LocalDateTime.now().minus(CREATE_TIME_DURATION_BEFORE);
        int count = orderService.syncOrder(minCreateTime);
        log.info("[execute][同步支付订单 ({}) 个]", count);
        return StrUtil.format("同步支付订单 ({}) 个",count);
    }

}
