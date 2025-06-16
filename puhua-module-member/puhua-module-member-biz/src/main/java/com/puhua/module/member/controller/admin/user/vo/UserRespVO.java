package com.puhua.module.member.controller.admin.user.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - C端用户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "423")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("密码")
    private String password;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("昵称")
    private String nickname;

    @Schema(description = "头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("头像")
    private String avatar;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "注册ip", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("注册ip")
    private String registerIp;

    @Schema(description = "最后登录ip", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最后登录ip")
    private String lastLoginIp;

    @Schema(description = "最后登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最后登录时间")
    private LocalDateTime lastLoginDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}