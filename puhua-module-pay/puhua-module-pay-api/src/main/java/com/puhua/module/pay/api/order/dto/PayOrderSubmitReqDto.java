package com.puhua.module.pay.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderSubmitReqDto {

    @Schema(description = "支付单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "支付单编号不能为空")
    private Long id;

    @Schema(description = "支付渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "wx_pub")
    @NotEmpty(message = "支付渠道不能为空")
    private String channelCode;

    @Schema(description = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数")
    private Map<String, String> channelExtras;

    @Schema(description = "展示模式", example = "url") // 参见 {@link PayDisplayModeEnum} 枚举。如果不传递，则每个支付渠道使用默认的方式
    private String displayMode;

    @Schema(description = "回跳地址")
    @URL(message = "回跳地址的格式必须是 URL")
    private String returnUrl;

    @Schema(description = "用户id")
    private String userIp;

}
