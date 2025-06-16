package com.puhua.module.member.controller.app.auth.vo;

import com.puhua.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 手机号 + 验证码登录 Request VO,如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppCaptchaAuthLoginReqVO {


    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18988882222")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "332211")
    @NotEmpty(message = "验证码不能为空")
    private String code;


}