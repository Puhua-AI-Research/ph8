package com.puhua.module.pay.convert.transfer;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.pay.core.client.dto.transfer.PayTransferUnifiedReqDTO;
import com.puhua.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import com.puhua.module.pay.controller.admin.demo.vo.transfer.PayDemoTransferCreateReqVO;
import com.puhua.module.pay.controller.admin.transfer.vo.PayTransferCreateReqVO;
import com.puhua.module.pay.controller.admin.transfer.vo.PayTransferPageItemRespVO;
import com.puhua.module.pay.controller.admin.transfer.vo.PayTransferRespVO;
import com.puhua.module.pay.dal.dataobject.transfer.PayTransferDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayTransferConvert {

    PayTransferConvert INSTANCE = Mappers.getMapper(PayTransferConvert.class);

    PayTransferDO convert(PayTransferCreateReqDTO dto);

    PayTransferUnifiedReqDTO convert2(PayTransferDO dto);

    PayTransferCreateReqDTO convert(PayTransferCreateReqVO vo);

    PayTransferCreateReqDTO convert(PayDemoTransferCreateReqVO vo);

    PayTransferRespVO convert(PayTransferDO bean);

    PageResult<PayTransferPageItemRespVO> convertPage(PageResult<PayTransferDO> pageResult);

}
