package com.puhua.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "发送手机号注册验证码，填手机号")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserCancelReqVO {

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "2323")
    @NotEmpty(message = "验证码不能为空")
    private String code;
}