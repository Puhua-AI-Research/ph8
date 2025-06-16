package com.puhua.module.member.controller.admin.memberBalancelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户额度流水新增/修改 Request VO")
@Data
public class BalanceLogSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5391")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "22437")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @Schema(description = "变动时间")
    private LocalDateTime logTime;

    @Schema(description = "交易类型", example = "1")
    private String changeType;

    @Schema(description = "变动金额")
    private Long changeBalance;

    @Schema(description = "业务方式")
    private String businessMode;

}