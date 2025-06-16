package com.puhua.module.member.service.memberBalancelog;

import java.util.*;
import jakarta.validation.*;
import com.puhua.module.member.controller.admin.memberBalancelog.vo.*;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;

/**
 * 用户额度流水 Service 接口
 *
 * @author 中航普华
 */
public interface BalanceLogService {

    /**
     * 创建用户额度流水
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBalanceLog(@Valid BalanceLogSaveReqVO createReqVO);

    /**
     * 更新用户额度流水
     *
     * @param updateReqVO 更新信息
     */
    void updateBalanceLog(@Valid BalanceLogSaveReqVO updateReqVO);

    /**
     * 删除用户额度流水
     *
     * @param id 编号
     */
    void deleteBalanceLog(Long id);

    /**
     * 获得用户额度流水
     *
     * @param id 编号
     * @return 用户额度流水
     */
    BalanceLogDO getBalanceLog(Long id);

    /**
     * 获得用户额度流水分页
     *
     * @param pageReqVO 分页查询
     * @return 用户额度流水分页
     */
    PageResult<BalanceLogDO> getBalanceLogPage(BalanceLogPageReqVO pageReqVO);


    /**
     * 任务账单统计
     */
    void calculateDailyTaskStatistics();
}