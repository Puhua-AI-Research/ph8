package com.puhua.module.pay.convert.demo;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.pay.controller.admin.demo.vo.order.PayDemoOrderCreateReqVO;
import com.puhua.module.pay.controller.admin.demo.vo.order.PayDemoOrderRespVO;
import com.puhua.module.pay.dal.dataobject.demo.PayDemoOrderDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 示例订单 Convert
 *
 * @author 中航普华
 */
@Mapper
public interface PayDemoOrderConvert {

    PayDemoOrderConvert INSTANCE = Mappers.getMapper(PayDemoOrderConvert.class);

    PayDemoOrderDO convert(PayDemoOrderCreateReqVO bean);

    PayDemoOrderRespVO convert(PayDemoOrderDO bean);

    PageResult<PayDemoOrderRespVO> convertPage(PageResult<PayDemoOrderDO> page);

}
