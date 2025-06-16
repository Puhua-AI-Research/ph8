package com.puhua.module.system.dal.mysql.sms;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.puhua.module.system.dal.dataobject.sms.SmsLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper extends BaseMapperX<SmsLogDO> {

    default PageResult<SmsLogDO> selectPage(SmsLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsLogDO>()
                .eqIfPresent(SmsLogDO::getChannelId, reqVO.getChannelId())
                .eqIfPresent(SmsLogDO::getTemplateId, reqVO.getTemplateId())
                .likeIfPresent(SmsLogDO::getMobile, reqVO.getMobile())
                .eqIfPresent(SmsLogDO::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(SmsLogDO::getSendTime, reqVO.getSendTime())
                .eqIfPresent(SmsLogDO::getReceiveStatus, reqVO.getReceiveStatus())
                .betweenIfPresent(SmsLogDO::getReceiveTime, reqVO.getReceiveTime())
                .orderByDesc(SmsLogDO::getId));
    }

}
