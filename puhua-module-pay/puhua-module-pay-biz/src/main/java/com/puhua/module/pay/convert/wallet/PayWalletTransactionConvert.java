package com.puhua.module.pay.convert.wallet;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.pay.controller.admin.wallet.vo.transaction.PayWalletTransactionRespVO;
import com.puhua.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import com.puhua.module.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletTransactionConvert {

    PayWalletTransactionConvert INSTANCE = Mappers.getMapper(PayWalletTransactionConvert.class);

    PageResult<PayWalletTransactionRespVO> convertPage2(PageResult<PayWalletTransactionDO> page);

    PayWalletTransactionDO convert(WalletTransactionCreateReqBO bean);

}
