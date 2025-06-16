package com.puhua.module.member.controller.app.user.vo;

import com.puhua.framework.common.validation.InEnum;
import com.puhua.framework.common.validation.Mobile;
import com.puhua.module.system.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "发送手机号注册验证码，填手机号")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRegMobileCaptchaVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18988882222")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;


    @Schema(description = "发送场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
}