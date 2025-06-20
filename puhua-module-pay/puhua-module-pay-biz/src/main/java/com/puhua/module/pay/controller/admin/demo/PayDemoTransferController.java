package com.puhua.module.pay.controller.admin.demo;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.pay.api.notify.dto.PayTransferNotifyReqDTO;
import com.puhua.module.pay.controller.admin.demo.vo.transfer.PayDemoTransferCreateReqVO;
import com.puhua.module.pay.controller.admin.demo.vo.transfer.PayDemoTransferRespVO;
import com.puhua.module.pay.convert.demo.PayDemoTransferConvert;
import com.puhua.module.pay.dal.dataobject.demo.PayDemoTransferDO;
import com.puhua.module.pay.service.demo.PayDemoTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 示例转账单")
@RestController
@RequestMapping("/pay/demo-transfer")
@Validated
public class PayDemoTransferController {
    @Resource
    private PayDemoTransferService demoTransferService;

    @PostMapping("/create")
    @Operation(summary = "创建示例转账订单")
    public CommonResult<Long> createDemoTransfer(@Valid @RequestBody PayDemoTransferCreateReqVO createReqVO) {
        return success(demoTransferService.createDemoTransfer(createReqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得示例转账订单分页")
    public CommonResult<PageResult<PayDemoTransferRespVO>> getDemoTransferPage(@Valid PageParam pageVO) {
        PageResult<PayDemoTransferDO> pageResult = demoTransferService.getDemoTransferPage(pageVO);
        return success(PayDemoTransferConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/update-status")
    @Operation(summary = "更新示例转账订单的转账状态") // 由 pay-module 转账服务，进行回调
    @PermitAll // 无需登录，安全由 PayDemoTransferService 内部校验实现
    public CommonResult<Boolean> updateDemoTransferStatus(@RequestBody PayTransferNotifyReqDTO notifyReqDTO) {
        demoTransferService.updateDemoTransferStatus(Long.valueOf(notifyReqDTO.getMerchantTransferId()),
                notifyReqDTO.getPayTransferId());
        return success(true);
    }
}
