package com.puhua.module.pay.controller.admin.demo.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 示例订单创建 Request VO")
@Data
public class PayDemoOrderCreateReqVO {

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17682")
    @NotNull(message = "商品编号不能为空")
    private Long spuId;

}
