package com.puhua.module.member.controller.app.user;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.controller.app.user.vo.*;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.service.user.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.puhua.framework.common.pojo.CommonResult.error;
import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.module.system.enums.ErrorCodeConstants.USER_EMAIL_EXISTS;

/**
 * @Author ZhangYi
 * @Date 2024年11月26日 17:14
 * @Description:
 */
@Tag(name = "web端 - 用户")
@RestController
@RequestMapping("/member/user")
@Validated
@Slf4j
public class AppUserController {

    @Resource
    private MemberService appUserService;

    @PermitAll
    @PostMapping("/sendMobileSms")
    @Operation(summary = "发送手机号注册验证码")
    public CommonResult<Boolean> sendRegSms(@RequestBody @Valid AppUserRegMobileCaptchaVO reqVO) {
//        MemberMapperDO user = appUserService.getUserByMobile(reqVO.getMobile());
//        if (user != null) {
//            return error(USER_MOBILE_EXISTS);
//        }
        appUserService.sendRegMobileCaptcha(reqVO);
        return success(true);
    }


    @PermitAll
    @PostMapping("/smsRegister")
    @Operation(summary = "手机号注册")
    public CommonResult<Boolean> smsRegister(@RequestBody @Valid AppUserSmsRegVO reqVO) {
        MemberMapperDO user = appUserService.getUserByMobile(reqVO.getMobile());
        if (user != null) {
            return error(USER_EMAIL_EXISTS);
        }
        appUserService.smsRegister(reqVO);
        return success(true);
    }

    @PermitAll
    @PostMapping("/sendRegEmail")
    @Operation(summary = "发送注册邮件")
    public CommonResult<Boolean> sendRegEmail(@RequestBody @Valid AppUserRegEmailCodeVO reqVO) {
        MemberMapperDO user = appUserService.getUserByEmail(reqVO.getEmail());
        if (user != null) {
            return error(USER_EMAIL_EXISTS);
        }
        appUserService.sendRegEmail(reqVO);
        return success(true);
    }

    @PermitAll
    @PostMapping("/emailRegister")
    @Operation(summary = "邮箱注册")
    public CommonResult<Boolean> emailRegister(@RequestBody @Valid AppUserEmailRegVO reqVO) {
        MemberMapperDO user = appUserService.getUserByEmail(reqVO.getEmail());
        if (user != null) {
            return error(USER_EMAIL_EXISTS);
        }
        appUserService.emailRegister(reqVO);
        return success(true);
    }

    @GetMapping("/userInfo")
    @Operation(summary = "用户信息")
    public CommonResult<AppUserInfoRespVO> userInfo() {
        Long loginUserId = getLoginUserId();
        MemberMapperDO user = appUserService.getUser(loginUserId);
        return success(BeanUtils.toBean(user, AppUserInfoRespVO.class));
    }

    @GetMapping("/unbindMp")
    @Operation(summary = "解绑微信绑定")
    public CommonResult<Boolean> unbindMp() {
        Long loginUserId = getLoginUserId();
        appUserService.unbindMp(loginUserId);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户信息")
    public CommonResult<Boolean> update(@RequestBody AppUserUpdateReqVO reqVO) {
        Long loginUserId = getLoginUserId();
        reqVO.setId(loginUserId);
        appUserService.update(reqVO);
        return success(true);
    }

    @PostMapping("/cancel")
    @Operation(summary = "注销")
    public CommonResult<Boolean> cancel(@RequestBody AppUserCancelReqVO cancelReqVO) {
        Long loginUserId = getLoginUserId();
        appUserService.cancel(loginUserId,cancelReqVO.getCode());
        return success(true);
    }

}
