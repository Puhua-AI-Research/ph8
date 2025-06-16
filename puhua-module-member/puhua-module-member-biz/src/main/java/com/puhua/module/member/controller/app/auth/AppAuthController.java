package com.puhua.module.member.controller.app.auth;

import cn.hutool.core.util.StrUtil;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.security.config.SecurityProperties;
import com.puhua.framework.security.core.util.SecurityFrameworkUtils;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginReqVO;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginRespVO;
import com.puhua.module.member.controller.app.auth.vo.AppCaptchaAuthLoginReqVO;
import com.puhua.module.member.controller.app.user.vo.AppGzhUserBindMobileVO;
import com.puhua.module.member.controller.app.user.vo.AppGzhUserRegMobileVO;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.service.auth.UserAuthService;
import com.puhua.module.member.enums.ApiConstants;
import com.puhua.module.member.service.user.MemberService;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeRespDto;
import com.puhua.module.system.enums.logger.LoginLogTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.puhua.framework.common.pojo.CommonResult.error;
import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.module.system.enums.ErrorCodeConstants.*;

/**
 * @Author ZhangYi
 * @Date 2024年11月26日 17:14
 * @Description:
 */
@Tag(name = "web端 - 认证")
@RestController
@RequestMapping("/member/auth")
@Validated
@Slf4j
public class AppAuthController {

    @Resource
    private UserAuthService authService;

    @Resource
    private MemberService memberService;

    @Resource
    private SecurityProperties securityProperties;

    @PermitAll
    @PostMapping("/login")
    @Operation(summary = "使用用户名 + 密码登录")
    public CommonResult<AppAuthLoginRespVO> login(@RequestBody @Valid AppAuthLoginReqVO reqVO) {
        if (StrUtil.isBlank(reqVO.getEmail()) && StrUtil.isBlank(reqVO.getMobile())) {
            return error(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        return success(authService.login(reqVO));
    }

    @PermitAll
    @PostMapping("/captchaLogin")
    @Operation(summary = "使用用户名 + 验证码登录")
    public CommonResult<AppAuthLoginRespVO> captchaLogin(@RequestBody @Valid AppCaptchaAuthLoginReqVO reqVO) {
        return success(authService.captchaLogin(reqVO));
    }

    @PermitAll
    @GetMapping("/getGzhQrCode")
    @Operation(summary = "生成微信公众号临时二维码")
    public CommonResult<TempWechatQrcodeRespDto> getGzhQrCode() throws WxErrorException {
        return authService.getGzhQrCode(ApiConstants.PUHUA_API_MP_APP_ID);
    }

    @PermitAll
    @GetMapping("/checkGzhLogin/{ticket}")
    @Operation(summary = "检查公众号扫码登录结果")
    public CommonResult<AppAuthLoginRespVO> checkGzhLogin(@PathVariable String ticket) {
        return success(authService.mpWeChatScanLogin(ticket));
    }


//    @PermitAll
//    @PostMapping("/gzhRegBindMobileLogin")
//    @Operation(summary = "公众号-注册 && 手机号注册，绑定后进行登录")
//    public CommonResult<AppAuthLoginRespVO> gzhRegBindMobileLogin(@RequestBody @Valid AppGzhUserRegMobileVO reqVO) {
//        MemberMapperDO user = memberService.getUserByMobile(reqVO.getMobile());
//        if (user != null) {
//            return error(USER_MOBILE_EXISTS);
//        }
//        return success(authService.gzhRegBindMobileLogin(reqVO));
//    }

    @PermitAll
    @PostMapping("/gzhBindMobileLogin")
    @Operation(summary = "公众号-注册并绑定手机号进行登录")
    public CommonResult<AppAuthLoginRespVO> gzhBindMobileLogin(@RequestBody @Valid AppGzhUserBindMobileVO reqVO) {
        return success(authService.gzhBindMobileLogin(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

}
