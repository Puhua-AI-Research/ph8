package com.puhua.module.member.api.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "发送注册邮件，填邮箱")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUserInfoRespVO {
    /**
     * id
     */
    private Long id;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 余额
     */
    private Long balance;
}