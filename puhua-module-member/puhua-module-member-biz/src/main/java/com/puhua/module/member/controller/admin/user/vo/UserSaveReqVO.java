package com.puhua.module.member.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - C端用户新增/修改 Request VO")
@Data
public class UserSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "423")
    private Long id;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "头像不能为空")
    private String avatar;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "注册ip", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "注册ip不能为空")
    private String registerIp;

    @Schema(description = "最后登录ip", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "最后登录ip不能为空")
    private String lastLoginIp;

    @Schema(description = "最后登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最后登录时间不能为空")
    private LocalDateTime lastLoginDate;

}