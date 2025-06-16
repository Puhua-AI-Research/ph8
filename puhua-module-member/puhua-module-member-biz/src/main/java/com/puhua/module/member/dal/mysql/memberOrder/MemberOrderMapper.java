package com.puhua.module.member.dal.mysql.memberOrder;

import java.util.*;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.member.dal.dataobject.memberOrder.MemberOrderDO;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.member.controller.admin.memberOrder.vo.*;

/**
 * 额度订单信息 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface MemberOrderMapper extends BaseMapperX<MemberOrderDO> {

    default PageResult<MemberOrderDO> selectPage(MemberOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberOrderDO>()
                .eqIfPresent(MemberOrderDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberOrderDO::getSpuId, reqVO.getSpuId())
                .likeIfPresent(MemberOrderDO::getSpuName, reqVO.getSpuName())
                .eqIfPresent(MemberOrderDO::getPayStatus, reqVO.getPayStatus())
                .eqIfPresent(MemberOrderDO::getPayOrderId, reqVO.getPayOrderId())
                .betweenIfPresent(MemberOrderDO::getPayTime, reqVO.getPayTime())
                .eqIfPresent(MemberOrderDO::getPayChannelCode, reqVO.getPayChannelCode())
                .eqIfPresent(MemberOrderDO::getPayRefundId, reqVO.getPayRefundId())
                .eqIfPresent(MemberOrderDO::getRefundPrice, reqVO.getRefundPrice())
                .betweenIfPresent(MemberOrderDO::getRefundTime, reqVO.getRefundTime())
                .betweenIfPresent(MemberOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberOrderDO::getId));
    }

    default MemberOrderDO getOrderByPayOrderId(Long payOrderId) {
        return selectOne(new LambdaQueryWrapperX<MemberOrderDO>().eq(MemberOrderDO::getPayOrderId, payOrderId).last("limit 1"));
    }

    default MemberOrderDO selectByIdForUpdate(Long id) {
        return selectOne(new LambdaQueryWrapperX<MemberOrderDO>().eq(MemberOrderDO::getId, id).last("for update"));
    }
}