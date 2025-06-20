package com.puhua.module.pay.dal.mysql.channel;

import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.pay.dal.dataobject.channel.PayChannelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PayChannelMapper extends BaseMapperX<PayChannelDO> {

    default PayChannelDO selectByAppIdAndCode(Long appId, String code) {
        return selectOne(PayChannelDO::getAppId, appId, PayChannelDO::getCode, code);
    }

    default List<PayChannelDO> selectListByAppIds(Collection<Long> appIds){
        return selectList(PayChannelDO::getAppId, appIds);
    }

    default List<PayChannelDO> selectListByAppId(Long appId, Integer status) {
        return selectList(new LambdaQueryWrapperX<PayChannelDO>()
                .eq(PayChannelDO::getAppId, appId)
                .eq(PayChannelDO::getStatus, status));
    }

}
