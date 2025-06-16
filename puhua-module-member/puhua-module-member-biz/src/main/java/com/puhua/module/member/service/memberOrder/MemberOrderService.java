package com.puhua.module.member.service.memberOrder;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.member.controller.app.memberOrder.vo.AppMemberOrderCreateReqVO;
import com.puhua.module.pay.api.order.dto.PayOrderSubmitRespDto;
import jakarta.validation.*;
import com.puhua.module.member.controller.admin.memberOrder.vo.*;
import com.puhua.module.member.dal.dataobject.memberOrder.MemberOrderDO;
import com.puhua.framework.common.pojo.PageResult;

/**
 * 额度订单信息 Service 接口
 *
 * @author 中航普华
 */
public interface MemberOrderService {

    /**
     * 创建额度订单信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrder(@Valid MemberOrderSaveReqVO createReqVO);

    /**
     * 更新额度订单信息
     *
     * @param updateReqVO 更新信息
     */
    void updateOrder(@Valid MemberOrderSaveReqVO updateReqVO);

    /**
     * 删除额度订单信息
     *
     * @param id 编号
     */
    void deleteOrder(Long id);

    /**
     * 获得额度订单信息
     *
     * @param id 编号
     * @return 额度订单信息
     */
    MemberOrderDO getOrder(Long id);

    /**
     * 获得额度订单信息分页
     *
     * @param pageReqVO 分页查询
     * @return 额度订单信息分页
     */
    PageResult<MemberOrderDO> getOrderPage(MemberOrderPageReqVO pageReqVO);

    /**
     * 创建订单
     *
     * @param loginUserId 用户id
     * @param createReqVO 请求
     * @return 订单编号
     */
    CommonResult<PayOrderSubmitRespDto> createAppMemberOrder(Long loginUserId, AppMemberOrderCreateReqVO createReqVO);

    /**
     * 获得订单信息ByPayOrderId
     *
     * @param payOrderId 编号
     * @return 额度订单信息
     */
    MemberOrderDO getOrderByPayOrderId(Long payOrderId);

    /**
     * 更新订单状态
     *
     * @param id         订单id
     * @param payOrderId 支付单id
     */
    void checkOrder(Long id, Long payOrderId);
}