package com.puhua.module.pay.api.order;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.pay.api.order.dto.PayOrderCreateReqDTO;
import com.puhua.module.pay.api.order.dto.PayOrderRespDTO;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitReqDto;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitRespDto;
import com.puhua.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import com.puhua.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import com.puhua.module.pay.convert.order.PayOrderConvert;
import com.puhua.module.pay.dal.dataobject.order.PayOrderDO;
import com.puhua.module.pay.service.order.PayOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static com.puhua.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PayOrderApiImpl implements PayOrderApi {

    @Resource
    private PayOrderService payOrderService;

    @Override
    public CommonResult<Long> createOrder(PayOrderCreateReqDTO reqDTO) {
        return success(payOrderService.createOrder(reqDTO));
    }

    @Override
    public CommonResult<PayOrderRespDTO> getOrder(Long id) {
        PayOrderDO order = payOrderService.getOrder(id);
        return success(PayOrderConvert.INSTANCE.convert2(order));
    }

    @Override
    public CommonResult<Boolean> updatePayOrderPrice(Long id, Integer payPrice) {
        payOrderService.updatePayOrderPrice(id, payPrice);
        return success(true);
    }

    @Override
    public CommonResult<PayOrderSubmitRespDto> submitOrder(PayOrderSubmitReqDto reqDTO) {
        PayOrderSubmitRespVO payOrderSubmitRespVO = payOrderService.submitOrder(BeanUtils.toBean(reqDTO, PayOrderSubmitReqVO.class), reqDTO.getUserIp());
        return success(BeanUtils.toBean(payOrderSubmitRespVO, PayOrderSubmitRespDto.class));
    }

}
