package com.puhua.module.member.controller.admin.quotaProduct;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import static com.puhua.framework.common.pojo.CommonResult.success;

import com.puhua.framework.excel.core.util.ExcelUtils;

import com.puhua.framework.apilog.core.annotation.ApiAccessLog;
import static com.puhua.framework.apilog.core.enums.OperateTypeEnum.*;

import com.puhua.module.member.controller.admin.quotaProduct.vo.*;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.module.member.service.quotaProduct.QuotaProductService;

@Tag(name = "管理后台 - 用户积分配额商品")
@RestController
@RequestMapping("/member/quota-product")
@Validated
public class QuotaProductController {

    @Resource
    private QuotaProductService quotaProductService;

    @PostMapping("/create")
    @Operation(summary = "创建用户积分配额商品")
    @PreAuthorize("@ss.hasPermission('member:quota-product:create')")
    public CommonResult<Long> createQuotaProduct(@Valid @RequestBody QuotaProductSaveReqVO createReqVO) {
        return success(quotaProductService.createQuotaProduct(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户积分配额商品")
    @PreAuthorize("@ss.hasPermission('member:quota-product:update')")
    public CommonResult<Boolean> updateQuotaProduct(@Valid @RequestBody QuotaProductSaveReqVO updateReqVO) {
        quotaProductService.updateQuotaProduct(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户积分配额商品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:quota-product:delete')")
    public CommonResult<Boolean> deleteQuotaProduct(@RequestParam("id") Long id) {
        quotaProductService.deleteQuotaProduct(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户积分配额商品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:quota-product:query')")
    public CommonResult<QuotaProductRespVO> getQuotaProduct(@RequestParam("id") Long id) {
        QuotaProductDO quotaProduct = quotaProductService.getQuotaProduct(id);
        return success(BeanUtils.toBean(quotaProduct, QuotaProductRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户积分配额商品分页")
    @PreAuthorize("@ss.hasPermission('member:quota-product:query')")
    public CommonResult<PageResult<QuotaProductRespVO>> getQuotaProductPage(@Valid QuotaProductPageReqVO pageReqVO) {
        PageResult<QuotaProductDO> pageResult = quotaProductService.getQuotaProductPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, QuotaProductRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户积分配额商品 Excel")
    @PreAuthorize("@ss.hasPermission('member:quota-product:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportQuotaProductExcel(@Valid QuotaProductPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<QuotaProductDO> list = quotaProductService.getQuotaProductPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户积分配额商品.xls", "数据", QuotaProductRespVO.class,
                        BeanUtils.toBean(list, QuotaProductRespVO.class));
    }

}