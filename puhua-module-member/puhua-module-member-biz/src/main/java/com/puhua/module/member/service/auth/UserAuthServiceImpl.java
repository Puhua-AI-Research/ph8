package com.puhua.module.member.service.auth;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.enums.UserTypeEnum;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.monitor.TracerUtils;
import com.puhua.framework.common.util.servlet.ServletUtils;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginReqVO;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginRespVO;
import com.puhua.module.member.controller.app.auth.vo.AppCaptchaAuthLoginReqVO;
import com.puhua.module.member.controller.app.user.vo.AppGzhUserBindMobileVO;
import com.puhua.module.member.convert.auth.AuthConvert;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.dal.mysql.user.MemberMapper;
import com.puhua.module.member.service.user.MemberService;
import com.puhua.module.mp.api.auth.MpAuthApi;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeReqDto;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeRespDto;
import com.puhua.module.mp.enums.ApiConstants;
import com.puhua.module.system.api.logger.LoginLogApi;
import com.puhua.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.puhua.module.system.api.oauth2.OAuth2TokenApi;
import com.puhua.module.system.api.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.puhua.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.puhua.module.system.api.sms.SmsCodeApi;
import com.puhua.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.puhua.module.system.enums.logger.LoginLogTypeEnum;
import com.puhua.module.system.enums.logger.LoginResultEnum;
import com.puhua.module.system.enums.oauth2.OAuth2ClientConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.puhua.module.mp.enums.ApiConstants.GZH_TICKET;
import static com.puhua.module.system.enums.ErrorCodeConstants.*;
import static com.puhua.module.system.enums.sms.SmsSceneEnum.MEMBER_LOGIN;

/**
 * Auth Service 实现类
 *
 * @author 中航普华
 */
@Service
@Slf4j
public class UserAuthServiceImpl implements UserAuthService {

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    MemberMapper memberMapper;

    /**
     * 验证码的开关，默认为 false
     */
    @Value("${puhua.captcha.enable:false}")
    private Boolean captchaEnable;

    @Resource
    private MemberService memberService;

    @Resource
    private OAuth2TokenApi oauth2TokenApi;

    @Resource
    private LoginLogApi loginLogApi;

    @Resource
    private MpAuthApi mpAuthApi;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public AppAuthLoginRespVO login(AppAuthLoginReqVO reqVO) {
        // 使用手机 + 密码，进行登录。
        MemberMapperDO user = null;
        String username = null;
        if (StrUtil.isNotBlank(reqVO.getEmail())) {
            username = reqVO.getEmail();
            user = loginByEmail(reqVO.getEmail(), reqVO.getPassword());
        }

        if (StrUtil.isNotBlank(reqVO.getMobile())) {
            username = reqVO.getMobile();
            user = loginByMobile(reqVO.getMobile(), reqVO.getPassword());
        }

        // 如果 socialType 非空，说明需要绑定社交用户
        String openid = null;
//        if (reqVO.getSocialType() != null) {
//            openid = socialUserApi.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
//                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState())).getCheckedData();
//        }

        // 创建 Token 令牌，记录登录日志
        assert user != null;
        return createTokenAfterLoginSuccess(user, username, LoginLogTypeEnum.LOGIN_MOBILE, openid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAuthLoginRespVO captchaLogin(AppCaptchaAuthLoginReqVO reqVO) {
        MemberMapperDO user = loginByCaptcha(reqVO.getMobile(), reqVO.getCode());
        return createTokenAfterLoginSuccess(user, user.getNickname(), LoginLogTypeEnum.LOGIN_MOBILE, user.getMpOpenid());

    }

    private MemberMapperDO loginByCaptcha(String mobile, String code) {
        SmsCodeUseReqDTO smsCodeUseReqDTO = new SmsCodeUseReqDTO().setCode(code).setMobile(mobile).setScene(MEMBER_LOGIN.getScene()).setUsedIp(getClientIP());

        smsCodeApi.useSmsCode(smsCodeUseReqDTO).checkError();
        MemberMapperDO user = memberService.getUserByMobile(mobile);
        // 创建 Token 令牌，记录登录日志
        if (user == null) {
            // insert
            user = new MemberMapperDO();
            user.setMobile(mobile);
            user.setRegisterIp(ServletUtils.getClientIP());
            user.setStatus(CommonStatusEnum.ENABLE.getStatus());
            user.setNickname(mobile);
            user.setPassword(IdUtil.fastSimpleUUID());
            memberMapper.insert(user);
        }
        return user;
    }

    @Override
    public CommonResult<TempWechatQrcodeRespDto> getGzhQrCode(String puhuaApiMpAppId) throws WxErrorException {
        TempWechatQrcodeReqDto reqDto = TempWechatQrcodeReqDto
                .builder()
                .appId(puhuaApiMpAppId)
                .ticket(IdUtil.fastSimpleUUID())
                .build();
        return mpAuthApi.getQrCodeCreateTmpTicket(reqDto);
    }

    @Override
    public AppAuthLoginRespVO mpWeChatScanLogin(String ticket) {
        String openid = stringRedisTemplate.opsForValue().get(ApiConstants.GZH_TICKET + ticket);
        if (StrUtil.isBlank(openid)) {
            // 二维码到期前，没有成功获取openid
            return AppAuthLoginRespVO.builder().loginOk(false).expired(true).build();
        }

        // 用户未扫码
        if (ticket.equals(openid)) {
            return AppAuthLoginRespVO.builder().loginOk(false).expired(false).build();
        }

        // 查找该openid是否注册
        MemberMapperDO user = memberService.getUserByMpOpenid(openid);
        // 未注册返回 AUTH_THIRD_LOGIN_NOT_BIND
        if (Objects.isNull(user)) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 已注册则返回token
        stringRedisTemplate.delete(ApiConstants.GZH_TICKET + ticket);
        return createTokenAfterLoginSuccess(user, user.getNickname(), LoginLogTypeEnum.LOGIN_MOBILE, openid);
    }


    private MemberMapperDO loginByEmail(String email, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_EMAIL;
        // 校验账号是否存在
        MemberMapperDO user = memberService.getUserByEmail(email);
        if (user == null) {
            createLoginLog(null, email, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!memberService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), email, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (ObjectUtil.notEqual(user.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            createLoginLog(user.getId(), email, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    private MemberMapperDO loginByMobile(String mobile, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_MOBILE;
        // 校验账号是否存在
        MemberMapperDO user = memberService.getUserByMobile(mobile);
        if (user == null) {
            createLoginLog(null, mobile, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!memberService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), mobile, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (ObjectUtil.notEqual(user.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            createLoginLog(user.getId(), mobile, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    private AppAuthLoginRespVO createTokenAfterLoginSuccess(MemberMapperDO user, String mobile, LoginLogTypeEnum logType, String openid) {
        // 插入登陆日志
        createLoginLog(user.getId(), mobile, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO().setUserId(user.getId()).setUserType(getUserType().getValue()).setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT)).getCheckedData();
        // 构建返回结果
        AppAuthLoginRespVO convert = AuthConvert.INSTANCE.convert(accessTokenRespDTO, openid);
        convert.setExpired(true);
        convert.setLoginOk(true);
        return convert;
    }

    private void createLoginLog(Long userId, String username, LoginLogTypeEnum logType, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogApi.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            memberService.updateUserLogin(userId, getClientIP());
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public AppAuthLoginRespVO gzhRegBindMobileLogin(AppGzhUserRegMobileVO reqVO) {
//        String mpOpenid = stringRedisTemplate.opsForValue().get(GZH_TICKET + reqVO.getTicket());
//        if (StrUtil.isBlank(mpOpenid)) {
//            throw exception(AUTH_GZH_TICKET_EXPIRED);
//        }
//        // 注册
//        AppUserSmsRegVO smsRegVO = AppUserSmsRegVO
//                .builder()
//                .mobile(reqVO.getMobile())
//                .password(IdUtil.fastSimpleUUID())
//                .code(reqVO.getCode())
//                .mpOpenid(mpOpenid)
//                .build();
//        MemberMapperDO user = memberService.smsRegister(smsRegVO);
//        return createTokenAfterLoginSuccess(user, user.getNickname(), LoginLogTypeEnum.LOGIN_SOCIAL, user.getMpOpenid());
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAuthLoginRespVO gzhBindMobileLogin(AppGzhUserBindMobileVO reqVO) {
        String ticketKey = GZH_TICKET + reqVO.getTicket();

        String mpOpenid = stringRedisTemplate.opsForValue().get(ticketKey);
        if (StrUtil.isBlank(mpOpenid)) {
            throw exception(AUTH_GZH_TICKET_EXPIRED);
        }
        // 登录校验
        MemberMapperDO user = loginByCaptcha(reqVO.getMobile(), reqVO.getCode());

        user.setMpOpenid(mpOpenid);
        memberService.updateUserByUserId(user);

        stringRedisTemplate.delete(ticketKey);
        return createTokenAfterLoginSuccess(user, user.getNickname(), LoginLogTypeEnum.LOGIN_SOCIAL, user.getMpOpenid());
    }


    private UserTypeEnum getUserType() {
        return UserTypeEnum.MEMBER;
    }

    @Override
    public void logout(String token, Integer logType) {
        oauth2TokenApi.removeAccessToken(token);
    }

}
