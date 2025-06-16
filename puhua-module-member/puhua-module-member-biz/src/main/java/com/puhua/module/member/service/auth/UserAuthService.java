package com.puhua.module.member.service.auth;


import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginReqVO;
import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginRespVO;
import com.puhua.module.member.controller.app.auth.vo.AppCaptchaAuthLoginReqVO;
import com.puhua.module.member.controller.app.user.vo.AppGzhUserBindMobileVO;
import com.puhua.module.member.controller.app.user.vo.AppGzhUserRegMobileVO;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeRespDto;
import jakarta.validation.Valid;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 管理后台的认证 Service 接口
 * <p>
 * 提供用户的登录、登出的能力
 *
 * @author 中航普华
 */
public interface UserAuthService {


    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO login(@Valid AppAuthLoginReqVO reqVO);

    /**
     * 验证码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO captchaLogin(@Valid AppCaptchaAuthLoginReqVO reqVO);

    /**
     * 生成微信公众号临时二维码
     *
     * @param puhuaApiMpAppId
     * @return
     */
    CommonResult<TempWechatQrcodeRespDto> getGzhQrCode(String puhuaApiMpAppId) throws WxErrorException;

    /**
     * 公众号扫码结果查询
     *
     * @param ticket
     * @return
     */
    AppAuthLoginRespVO mpWeChatScanLogin(String ticket);


//    /**
//     * 公众号-注册 && 手机号注册 绑定后进行登录
//     *
//     * @param reqVO
//     * @return
//     */
//    AppAuthLoginRespVO gzhRegBindMobileLogin(AppGzhUserRegMobileVO reqVO);

    /**
     * 公众号-注册并绑定手机号进行登录
     *
     * @param reqVO
     * @return
     */
    AppAuthLoginRespVO gzhBindMobileLogin(AppGzhUserBindMobileVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    void logout(String token, Integer logType);
}
