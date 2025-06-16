package com.puhua.module.member.controller.app.memberOrder;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.controller.app.memberOrder.vo.AppMemberOrderCreateReqVO;
import com.puhua.module.member.controller.app.memberOrder.vo.MemberOrderStatusRespVO;
import com.puhua.module.member.dal.dataobject.memberOrder.MemberOrderDO;
import com.puhua.module.member.service.memberOrder.MemberOrderService;
import com.puhua.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitRespDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "C端 - 额度订单信息")
@RestController
@RequestMapping("/member/order")
@Validated
public class AppMemberOrderController {

    @Resource
    private MemberOrderService memberOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public CommonResult<PayOrderSubmitRespDto> createDemoOrder(@Valid @RequestBody AppMemberOrderCreateReqVO createReqVO) {
        return memberOrderService.createAppMemberOrder(getLoginUserId(), createReqVO);
    }

    @GetMapping("/check")
    @Operation(summary = "查询订单状态")
    public CommonResult<MemberOrderStatusRespVO> check(@RequestParam Long id) {
//        MemberOrderDO memberOrderDO = memberOrderService.getOrder(id);
        // 对接好回调后删除以下代码
        MemberOrderDO order = memberOrderService.getOrder(id);
        memberOrderService.checkOrder(id, order.getPayOrderId());
        order = memberOrderService.getOrder(id);
        return success(BeanUtils.toBean(order, MemberOrderStatusRespVO.class));
    }


    @PostMapping("/update-paid")
    @Operation(summary = "更新示例订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll
    public CommonResult<Boolean> updateDemoOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        memberOrderService.checkOrder(Long.valueOf(notifyReqDTO.getMerchantOrderId()), notifyReqDTO.getPayOrderId());
        return success(true);
    }

}