package com.puhua.module.system.enums.sms;

import cn.hutool.core.util.ArrayUtil;
import com.puhua.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户短信验证码发送场景的枚举
 *
 * @author 中航普华
 */
@Getter
@AllArgsConstructor
public enum SmsSceneEnum implements IntArrayValuable {

    MEMBER_LOGIN(1, "user-sms-login", "会员用户 - 手机号登陆"),
    MEMBER_UPDATE_MOBILE(2, "user-update-mobile", "会员用户 - 修改手机"),
    MEMBER_UPDATE_PASSWORD(3, "user-sms-login", "会员用户 - 修改密码"),
    MEMBER_RESET_PASSWORD(4, "user-reset-password", "会员用户 - 忘记密码"),
    MEMBER_REGISTER(5, "user-sms-login", "会员用户 - 手机号登陆"),
    MEMBER_CLOSE_ACCOUNT(6, "user-sms-login", "会员用户 - 注销账户"),

    ADMIN_MEMBER_LOGIN(21, "admin-sms-login", "后台用户 - 手机号登录");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(SmsSceneEnum::getScene).toArray();

    /**
     * 验证场景的编号
     */
    private final Integer scene;
    /**
     * 模版编码
     */
    private final String templateCode;
    /**
     * 描述
     */
    private final String description;

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static SmsSceneEnum getCodeByScene(Integer scene) {
        return ArrayUtil.firstMatch(sceneEnum -> sceneEnum.getScene().equals(scene),
                values());
    }

}
