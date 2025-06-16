package com.puhua.module.member.service.user;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.member.controller.admin.user.vo.UserPageReqVO;
import com.puhua.module.member.controller.admin.user.vo.UserSaveReqVO;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginRespVO;
import com.puhua.module.member.controller.app.user.vo.*;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import jakarta.validation.Valid;

/**
 * C端用户 Service 接口
 *
 * @author ZhangYi
 */
public interface MemberService {

    /**
     * 创建C端用户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUser(@Valid UserSaveReqVO createReqVO);

    /**
     * 更新C端用户
     *
     * @param updateReqVO 更新信息
     */
    void updateUser(@Valid UserSaveReqVO updateReqVO);

    /**
     * 删除C端用户
     *
     * @param id 编号
     */
    void deleteUser(Long id);

    /**
     * 获得C端用户
     *
     * @param id 编号
     * @return C端用户
     */
    MemberMapperDO getUser(Long id);

    /**
     * 获得C端用户分页
     *
     * @param pageReqVO 分页查询
     * @return C端用户分页
     */
    PageResult<MemberMapperDO> getUserPage(UserPageReqVO pageReqVO);

    /**
     * 通过邮箱查询用户
     *
     * @param email 用户名
     * @return 用户对象信息
     */
    MemberMapperDO getUserByEmail(String email);

    /**
     * 通过手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    MemberMapperDO getUserByMobile(String mobile);

    /**
     * 通过公众号openid查询用户
     *
     * @param mpOpenid 公众号openid
     * @return 用户对象信息
     */
    MemberMapperDO getUserByMpOpenid(String mpOpenid);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id      用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 发送注册邮件
     *
     * @param reqVO
     * @return
     */
    void sendRegEmail(AppUserRegEmailCodeVO reqVO);

    /**
     * 发送手机号注册验证码
     *
     * @param reqVO
     * @return
     */
    void sendRegMobileCaptcha(AppUserRegMobileCaptchaVO reqVO);

    /**
     * 通过邮箱注册
     *
     * @param reqVO
     */
    void emailRegister(AppUserEmailRegVO reqVO);

    /**
     * 手机号注册
     *
     * @param reqVO
     */
    MemberMapperDO smsRegister(AppUserSmsRegVO reqVO);

    /**
     * 更新用户信息
     *
     * @param memberMapperDO
     */
    void updateUserByUserId(MemberMapperDO memberMapperDO);

    /**
     * 解绑公众号
     *
     * @param loginUserId
     */
    void unbindMp(Long loginUserId);

    /**
     * 修改用户信息
     *
     * @param reqVO 请求
     * @return 用户信息
     */
    void update(AppUserUpdateReqVO reqVO);

    void changeBalance(Long userId, Long balance);

    /**
     * 用户注销
     *
     * @param loginUserId 用户id
     * @param code        验证码
     */
    void cancel(Long loginUserId, String code);
}