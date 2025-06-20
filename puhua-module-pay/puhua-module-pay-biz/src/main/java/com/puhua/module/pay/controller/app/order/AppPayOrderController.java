package com.puhua.module.pay.controller.app.order;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.pay.core.enums.channel.PayChannelEnum;
import com.puhua.module.pay.controller.admin.order.vo.PayOrderRespVO;
import com.puhua.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import com.puhua.module.pay.controller.app.order.vo.AppPayOrderSubmitReqVO;
import com.puhua.module.pay.controller.app.order.vo.AppPayOrderSubmitRespVO;
import com.puhua.module.pay.convert.order.PayOrderConvert;
import com.puhua.module.pay.dal.dataobject.order.PayOrderDO;
import com.puhua.module.pay.enums.order.PayOrderStatusEnum;
import com.puhua.module.pay.framework.pay.core.WalletPayClient;
import com.puhua.module.pay.service.order.PayOrderService;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserType;

@Tag(name = "用户 APP - 支付订单")
@RestController
@RequestMapping("/pay/order")
@Validated
@Slf4j
public class AppPayOrderController {

    @Resource
    private PayOrderService payOrderService;

    @GetMapping("/get")
    @Operation(summary = "获得支付订单")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true, example = "1024"),
            @Parameter(name = "sync", description = "是否同步", example = "true")
    })
    public CommonResult<PayOrderRespVO> getOrder(@RequestParam("id") Long id,
                                                 @RequestParam(value = "sync", required = false) Boolean sync) {
        PayOrderDO order = payOrderService.getOrder(id);
        // sync 仅在等待支付
        if (Boolean.TRUE.equals(sync) && PayOrderStatusEnum.isWaiting(order.getStatus())) {
            payOrderService.syncOrderQuietly(order.getId());
            // 重新查询，因为同步后，可能会有变化
            order = payOrderService.getOrder(id);
        }
        return success(BeanUtils.toBean(order, PayOrderRespVO.class));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交支付订单")
    public CommonResult<AppPayOrderSubmitRespVO> submitPayOrder(@RequestBody AppPayOrderSubmitReqVO reqVO) {
        // 1. 钱包支付事，需要额外传 user_id 和 user_type
        if (Objects.equals(reqVO.getChannelCode(), PayChannelEnum.WALLET.getCode())) {
            Map<String, String> channelExtras = reqVO.getChannelExtras() == null ?
                    Maps.newHashMapWithExpectedSize(2) : reqVO.getChannelExtras();
            channelExtras.put(WalletPayClient.USER_ID_KEY, String.valueOf(getLoginUserId()));
            channelExtras.put(WalletPayClient.USER_TYPE_KEY, String.valueOf(getLoginUserType()));
            reqVO.setChannelExtras(channelExtras);
        }

        // 2. 提交支付
        PayOrderSubmitRespVO respVO = payOrderService.submitOrder(reqVO, getClientIP());
        return success(PayOrderConvert.INSTANCE.convert3(respVO));
    }

}
