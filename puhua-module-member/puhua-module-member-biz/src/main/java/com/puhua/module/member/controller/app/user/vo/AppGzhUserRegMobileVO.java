package com.puhua.module.member.controller.app.user.vo;

import com.puhua.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "手机号注册并绑定公众号")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppGzhUserRegMobileVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18988882222")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "公众号票据", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid")
    @NotEmpty(message = "绑定参数丢失")
    private String ticket;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "验证码不能为空")
    private String code;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "密码不能为空")
    public String password;
}