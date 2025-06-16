package com.puhua.module.member.api.member;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.service.user.MemberService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ZhangYi
 * @Date 2025年03月06日 14:40
 * @Description:
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class MemberApiImpl implements MemberApi {

    @Resource
    private MemberService memberService;

    @Override
    public MemberUserInfoRespVO getMemberUser(Long userId) {
        MemberMapperDO user = memberService.getUser(userId);
        return BeanUtils.toBean(user, MemberUserInfoRespVO.class);
    }

    @Override
    public MemberUserInfoRespVO changeBalance(Long userId, Long balance) {
        memberService.changeBalance(userId, balance);
        return null;
    }

}
