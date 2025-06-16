package com.puhua.module.member.dal.mysql.memberBalancelog;

import java.util.*;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.member.controller.admin.memberBalancelog.vo.*;

/**
 * 用户额度流水 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface BalanceLogMapper extends BaseMapperX<BalanceLogDO> {

    default PageResult<BalanceLogDO> selectPage(BalanceLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BalanceLogDO>()
                .eqIfPresent(BalanceLogDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(BalanceLogDO::getLogTime, reqVO.getLogTime())
                .eqIfPresent(BalanceLogDO::getChangeType, reqVO.getChangeType())
                .eqIfPresent(BalanceLogDO::getBusinessMode, reqVO.getBusinessMode())
                .betweenIfPresent(BalanceLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BalanceLogDO::getId));
    }

}