package com.puhua.module.member.service.user;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.controller.admin.user.vo.UserPageReqVO;
import com.puhua.module.member.controller.admin.user.vo.UserSaveReqVO;
import com.puhua.module.member.controller.app.user.vo.*;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.dal.mysql.user.MemberMapper;
import com.puhua.module.system.api.mail.MailSendApi;
import com.puhua.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import com.puhua.module.system.api.sms.SmsCodeApi;
import com.puhua.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.puhua.module.system.api.sms.dto.code.SmsTemplateCodeSendReqDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.puhua.module.member.enums.ErrorCodeConstants.CAPTCHA_INCORRECT;
import static com.puhua.module.member.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.puhua.module.system.enums.sms.SmsSceneEnum.*;

/**
 * C端用户 Service 实现类
 *
 * @author ZhangYi
 */
@Service
@Validated
public class MemberServiceImpl implements MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private MailSendApi mailSendApi;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long createUser(UserSaveReqVO createReqVO) {
        // 插入
        MemberMapperDO user = BeanUtils.toBean(createReqVO, MemberMapperDO.class);
        memberMapper.insert(user);
        // 返回
        return user.getId();
    }

    @Override
    public void updateUser(UserSaveReqVO updateReqVO) {
        // 校验存在
        validateUserExists(updateReqVO.getId());
        // 更新
        MemberMapperDO updateObj = BeanUtils.toBean(updateReqVO, MemberMapperDO.class);
        memberMapper.updateById(updateObj);
    }

    @Override
    public void deleteUser(Long id) {
        // 校验存在
        validateUserExists(id);
        // 删除
        memberMapper.deleteById(id);
    }

    private void validateUserExists(Long id) {
        if (memberMapper.selectById(id) == null) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    @Override
    public MemberMapperDO getUser(Long id) {
        return memberMapper.selectById(id);
    }

    @Override
    public PageResult<MemberMapperDO> getUserPage(UserPageReqVO pageReqVO) {
        return memberMapper.selectPage(pageReqVO);
    }

    @Override
    public MemberMapperDO getUserByEmail(String email) {
        return memberMapper.selectByUsername(email);
    }

    @Override
    public MemberMapperDO getUserByMobile(String mobile) {
        return memberMapper.selectByMobile(mobile);
    }

    @Override
    public MemberMapperDO getUserByMpOpenid(String mpOpenid) {
        return memberMapper.selectByMpOpenid(mpOpenid);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        memberMapper.updateById(new MemberMapperDO().setId(id)
                .setLastLoginIp(loginIp).setLastLoginDate(LocalDateTime.now()));
    }

    @Override
    public void sendRegEmail(AppUserRegEmailCodeVO reqVO) {
        String redisKey = "user:reg:email:" + reqVO.getEmail();
        String code = String.valueOf(randomInt(0, 999999));
        // 1. 准备参数
        String templateCode = "1001"; // 邮件模版，记得在【邮箱管理】中配置噢
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("code", code);
        templateParams.put("name", reqVO.getEmail());
        stringRedisTemplate.opsForValue().set(redisKey, code, 300, TimeUnit.SECONDS);

        // 2. 发送邮件
        mailSendApi.sendSingleMailToMember(new MailSendSingleToUserReqDTO().setMail(reqVO.getEmail()).setTemplateCode(templateCode).setTemplateParams(templateParams));
    }

    @Override
    public void sendRegMobileCaptcha(AppUserRegMobileCaptchaVO reqVO) {

        SmsTemplateCodeSendReqDTO reqDTO = SmsTemplateCodeSendReqDTO.builder().mobile(reqVO.getMobile()).scene(reqVO.getScene()).createIp(getClientIP()).build();

        Map<String, Object> params = new HashMap<>();
        params.put("code", randomInt(100000, 999999));
        params.put("min", 5);
        reqDTO.setTemplateParams(params);
        smsCodeApi.sendTemplateSmsCode(reqDTO);
    }

    @Override
    public void emailRegister(AppUserEmailRegVO reqVO) {
        String redisKey = "user:reg:email:" + reqVO.getEmail();

        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (!reqVO.getCode().equals(code)) {
            throw exception(CAPTCHA_INCORRECT);
        }
        // insert
        MemberMapperDO userDO = new MemberMapperDO();
        userDO.setEmail(reqVO.getEmail());
        userDO.setRegisterIp(getClientIP());
        userDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        userDO.setNickname(reqVO.getEmail());
        userDO.setPassword(encodePassword(reqVO.getPassword()));
        memberMapper.insert(userDO);
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public MemberMapperDO smsRegister(AppUserSmsRegVO reqVO) {
        SmsCodeUseReqDTO smsCodeUseReqDTO = new SmsCodeUseReqDTO().setCode(reqVO.getCode()).setMobile(reqVO.getMobile()).setScene(MEMBER_LOGIN.getScene()).setUsedIp(getClientIP());
        smsCodeApi.useSmsCode(smsCodeUseReqDTO).checkError();
        // insert
        MemberMapperDO userDO = new MemberMapperDO();
        userDO.setMobile(reqVO.getMobile());
        userDO.setRegisterIp(getClientIP());
        userDO.setNickname(reqVO.getMobile());
        userDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        userDO.setPassword(encodePassword(reqVO.getPassword()));
        userDO.setMpOpenid(reqVO.getMpOpenid());
        memberMapper.insert(userDO);
        return userDO;
    }

    @Override
    public void updateUserByUserId(MemberMapperDO memberMapperDO) {
        memberMapper.updateById(memberMapperDO);
    }

    @Override
    public void unbindMp(Long loginUserId) {
        memberMapper.unbindMp(loginUserId);
    }

    @Override
    public void update(AppUserUpdateReqVO reqVO) {
        MemberMapperDO user = getUser(reqVO.getId());

        LambdaUpdateWrapper<MemberMapperDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MemberMapperDO::getId, user.getId());
        updateWrapper.set(MemberMapperDO::getNickname, reqVO.getNickname());
        updateWrapper.set(MemberMapperDO::getAvatar, reqVO.getAvatar());

        if (StrUtil.isNotBlank(reqVO.getPassword())) {
            if (StrUtil.isBlank(user.getMobile())) {
                throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "手机号不存在");
            }
            // 验证码
            SmsCodeUseReqDTO smsCodeUseReqDTO = new SmsCodeUseReqDTO()
                    .setCode(reqVO.getCode()).
                    setMobile(user.getMobile())
                    .setScene(MEMBER_UPDATE_PASSWORD.getScene())
                    .setUsedIp(getClientIP());
            smsCodeApi.useSmsCode(smsCodeUseReqDTO).checkError();
            updateWrapper.set(MemberMapperDO::getPassword, passwordEncoder.encode(reqVO.getPassword()));
        }
        memberMapper.update(updateWrapper);
    }

    @Override
    public void changeBalance(Long userId, Long balance) {
        LambdaUpdateWrapper<MemberMapperDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MemberMapperDO::getId, userId);
        updateWrapper.setSql(" balance = balance + " + balance);
        memberMapper.update(updateWrapper);
    }

    @Override
    public void cancel(Long loginUserId, String code) {
        MemberMapperDO memberMapperDO = memberMapper.selectById(loginUserId);
        if (memberMapperDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        // 验证码
        SmsCodeUseReqDTO smsCodeUseReqDTO = new SmsCodeUseReqDTO()
                .setCode(code).
                setMobile(memberMapperDO.getMobile())
                .setScene(MEMBER_CLOSE_ACCOUNT.getScene())
                .setUsedIp(getClientIP());
        smsCodeApi.useSmsCode(smsCodeUseReqDTO).checkError();
        memberMapper.deleteById(loginUserId);
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}