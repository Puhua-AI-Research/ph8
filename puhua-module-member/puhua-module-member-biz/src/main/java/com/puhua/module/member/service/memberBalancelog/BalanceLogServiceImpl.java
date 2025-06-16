package com.puhua.module.member.service.memberBalancelog;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.puhua.module.ai.api.task.TaskApi;
import com.puhua.module.ai.api.task.dto.TaskPageReqDto;
import com.puhua.module.ai.api.task.dto.TaskRespDto;
import com.puhua.module.member.enums.BusinessModeEnum;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.puhua.module.member.controller.admin.memberBalancelog.vo.*;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.util.object.BeanUtils;

import com.puhua.module.member.dal.mysql.memberBalancelog.BalanceLogMapper;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.member.enums.ErrorCodeConstants.*;

/**
 * 用户额度流水 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class BalanceLogServiceImpl implements BalanceLogService {

    @Resource
    private BalanceLogMapper balanceLogMapper;

    @Resource
    TaskApi taskApi;

    @Override
    public Long createBalanceLog(BalanceLogSaveReqVO createReqVO) {
        // 插入
        BalanceLogDO balanceLog = BeanUtils.toBean(createReqVO, BalanceLogDO.class);
        balanceLogMapper.insert(balanceLog);
        // 返回
        return balanceLog.getId();
    }

    @Override
    public void updateBalanceLog(BalanceLogSaveReqVO updateReqVO) {
        // 校验存在
        validateBalanceLogExists(updateReqVO.getId());
        // 更新
        BalanceLogDO updateObj = BeanUtils.toBean(updateReqVO, BalanceLogDO.class);
        balanceLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteBalanceLog(Long id) {
        // 校验存在
        validateBalanceLogExists(id);
        // 删除
        balanceLogMapper.deleteById(id);
    }

    private void validateBalanceLogExists(Long id) {
        if (balanceLogMapper.selectById(id) == null) {
            throw exception(BALANCE_LOG_NOT_EXISTS);
        }
    }

    @Override
    public BalanceLogDO getBalanceLog(Long id) {
        return balanceLogMapper.selectById(id);
    }

    @Override
    public PageResult<BalanceLogDO> getBalanceLogPage(BalanceLogPageReqVO pageReqVO) {
        return balanceLogMapper.selectPage(pageReqVO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateDailyTaskStatistics() {
        DateTime yesterday = DateUtil.yesterday();
        LocalDateTime[] localDateTimes = {DateUtil.beginOfDay(yesterday).toLocalDateTime(), DateUtil.endOfDay(yesterday).toLocalDateTime()};
        // 查询指定日期，所有用户任务消耗
        TaskPageReqDto reqVO = TaskPageReqDto.builder().callTime(localDateTimes).build();
        List<TaskRespDto> taskDOS = taskApi.taskList(reqVO);

        // 分组
        Map<Long, List<TaskRespDto>> userTasks = taskDOS.stream().collect(Collectors.groupingBy(TaskRespDto::getUserId));
        // 计算
        userTasks.keySet().forEach(userId -> {
            List<TaskRespDto> taskDOList = userTasks.get(userId);
            BigDecimal totalFee = taskDOList.stream().map(TaskRespDto::getTotalFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 存库
            BalanceLogDO balanceLogDO = BalanceLogDO
                    .builder()
                    .userId(userId)
                    .changeBalance(totalFee.longValue())
                    .businessMode(BusinessModeEnum.TIME_BASED.getValue())
                    .logTime(DateUtil.beginOfDay(new Date()).toLocalDateTime())
                    .changeType("token_cost")
                    .build();
            balanceLogMapper.insert(balanceLogDO);
        });
    }

}