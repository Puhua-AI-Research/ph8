package com.puhua.module.member.task;

import com.puhua.module.member.service.memberBalancelog.BalanceLogService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author ZhangYi
 * @Date 2025年03月14日 15:35
 * @Description: 用户任务统计任务
 */
@Component
@Slf4j
public class UserTaskJob {
    @Resource
    BalanceLogService balanceLogService;

    @XxlJob("TaskStatisticsJob")
    public String execute() {
        balanceLogService.calculateDailyTaskStatistics();
        return "";
    }

}
