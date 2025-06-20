package com.puhua.module.pay.api.refund;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.pay.api.refund.dto.PayRefundCreateReqDTO;
import com.puhua.module.pay.api.refund.dto.PayRefundRespDTO;
import com.puhua.module.pay.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@FeignClient(name = ApiConstants.NAME) // TODO 普华：fallbackFactory =
@Tag(name = "RPC 服务 - 退款单")
public interface PayRefundApi {

    String PREFIX = ApiConstants.PREFIX + "/refund";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建退款单")
    CommonResult<Long> createRefund(@Valid @RequestBody PayRefundCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得退款单")
    @Parameter(name = "id", description = "退款单编号", example = "1", required = true)
    CommonResult<PayRefundRespDTO> getRefund(@RequestParam("id") Long id);

}
