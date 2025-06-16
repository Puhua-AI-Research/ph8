package com.puhua.module.member.controller.app.memberOrder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "C端 - 创建订单 Request VO")
@Data
public class AppMemberOrderCreateReqVO {

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16754")
    @NotNull(message = "商品编号不能为空")
    private Long spuId;

    @Schema(description = "支付渠道")
    private String payChannelCode;

}