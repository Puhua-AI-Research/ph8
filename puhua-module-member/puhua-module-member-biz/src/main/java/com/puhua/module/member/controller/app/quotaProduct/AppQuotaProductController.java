package com.puhua.module.member.controller.app.quotaProduct;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.member.controller.app.quotaProduct.vo.AppQuotaProductRespVO;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.module.member.service.quotaProduct.QuotaProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "C端 - 用户积分配额商品")
@RestController
@RequestMapping("/member/quota-product")
@Validated
public class AppQuotaProductController {

    @Resource
    private QuotaProductService quotaProductService;


    @GetMapping("/list")
    @Operation(summary = "获得用户积分配额商品分页")
    @PermitAll
    public CommonResult<List<AppQuotaProductRespVO>> getQuotaProductList() {
        List<QuotaProductDO> list = quotaProductService.getQuotaProductList();
        return success(BeanUtils.toBean(list, AppQuotaProductRespVO.class));
    }
}