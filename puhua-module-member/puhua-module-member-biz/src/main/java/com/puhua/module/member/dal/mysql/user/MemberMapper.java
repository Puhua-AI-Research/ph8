package com.puhua.module.member.dal.mysql.user;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.member.controller.admin.user.vo.UserPageReqVO;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * C端用户 Mapper
 *
 * @author ZhangYi
 */
@Mapper
public interface MemberMapper extends BaseMapperX<MemberMapperDO> {

    default PageResult<MemberMapperDO> selectPage(UserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX
                <MemberMapperDO>()
                .eqIfPresent(MemberMapperDO::getEmail, reqVO.getEmail())
                .likeIfPresent(MemberMapperDO::getNickname, reqVO.getNickname())
                .eqIfPresent(MemberMapperDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MemberMapperDO::getLastLoginIp, reqVO.getLastLoginIp())
                .betweenIfPresent(MemberMapperDO::getLastLoginDate, reqVO.getLastLoginDate())
                .betweenIfPresent(MemberMapperDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberMapperDO::getId));
    }

    default MemberMapperDO selectByUsername(String username) {
        return selectOne(MemberMapperDO::getEmail, username);
    }

    default MemberMapperDO selectByMobile(String mobile) {
        return selectOne(MemberMapperDO::getMobile, mobile);
    }

    default MemberMapperDO selectByMpOpenid(String mpOpenid) {
        return selectOne(MemberMapperDO::getMpOpenid, mpOpenid);
    }

    default void unbindMp(Long loginUserId) {
        LambdaUpdateWrapper<MemberMapperDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<MemberMapperDO>()
                .setSql(" mp_openid = null ")
                .eq(MemberMapperDO::getId, loginUserId);
        update(null, lambdaUpdateWrapper);
    }
}