package com.puhua.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "发送注册邮件，填邮箱")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRegEmailCodeVO {

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "18988882222@163.com")
    @NotEmpty(message = "邮箱不能为空")
//    @Mobile
    private String email;


}