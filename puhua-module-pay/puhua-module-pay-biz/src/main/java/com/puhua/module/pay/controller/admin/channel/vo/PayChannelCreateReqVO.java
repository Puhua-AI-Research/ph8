package com.puhua.module.pay.controller.admin.channel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 支付渠道 创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelCreateReqVO extends PayChannelBaseVO {

    @Schema(description = "渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "alipay_pc")
    @NotNull(message = "渠道编码不能为空")
    private String code;

    @Schema(description = "渠道配置的 json 字符串")
    @NotBlank(message = "渠道配置不能为空")
    private String config;

}
