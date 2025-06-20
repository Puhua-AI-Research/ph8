package com.puhua.module.pay.job.notify;

import cn.hutool.core.util.StrUtil;
import com.puhua.framework.tenant.core.job.TenantJob;
import com.puhua.module.pay.service.notify.PayNotifyService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付通知 Job
 * 通过不断扫描待通知的 PayNotifyTaskDO 记录，回调业务线的回调接口
 *
 * @author 中航普华
 */
@Component
@Slf4j
public class PayNotifyJob {

    @Resource
    private PayNotifyService payNotifyService;

    @XxlJob("payNotifyJob")
    @TenantJob // 多租户
    public String execute() throws Exception {
        int notifyCount = payNotifyService.executeNotify();
        log.info("[execute][执行支付通知 ({}) 个]", notifyCount);
        return StrUtil.format("执行支付通知 ({}) 个",notifyCount);
    }

}
