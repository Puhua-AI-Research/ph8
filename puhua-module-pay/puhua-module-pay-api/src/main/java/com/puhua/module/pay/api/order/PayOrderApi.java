package com.puhua.module.pay.api.order;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.pay.api.order.dto.PayOrderCreateReqDTO;
import com.puhua.module.pay.api.order.dto.PayOrderRespDTO;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitReqDto;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitRespDto;
import com.puhua.module.pay.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@FeignClient(name = ApiConstants.NAME) // TODO 普华：fallbackFactory =
@Tag(name = "RPC 服务 - 支付单")
public interface PayOrderApi {

    String PREFIX = ApiConstants.PREFIX + "/order";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建支付单")
    CommonResult<Long> createOrder(@Valid @RequestBody PayOrderCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得支付单")
    @Parameter(name = "id", description = "支付单编号", example = "1", required = true)
    @PermitAll
    CommonResult<PayOrderRespDTO> getOrder(@RequestParam("id") Long id);

    @PutMapping(PREFIX + "/update-price")
    @Operation(summary = "更新支付订单价格")
    @Parameters({
            @Parameter(name = "id", description = "支付单编号", example = "1", required = true),
            @Parameter(name = "payPrice", description = "支付单价格", example = "100", required = true)
    })
    CommonResult<Boolean> updatePayOrderPrice(@RequestParam("id") Long id,
                                              @RequestParam("payPrice") Integer payPrice);

    @PostMapping("/submit")
    @Operation(summary = "提交支付订单")
    CommonResult<PayOrderSubmitRespDto> submitOrder(@Valid @RequestBody PayOrderSubmitReqDto reqDTO);
}
