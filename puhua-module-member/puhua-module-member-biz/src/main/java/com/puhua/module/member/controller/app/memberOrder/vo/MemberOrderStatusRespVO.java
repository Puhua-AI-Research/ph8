package com.puhua.module.member.controller.app.memberOrder.vo;

import com.puhua.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 额度订单信息 Response VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberOrderStatusRespVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29717")
    private Long id;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @DictFormat("pay_order_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer payStatus;
}