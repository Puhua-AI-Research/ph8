package com.puhua.module.member.controller.admin.memberOrder;

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

import com.puhua.module.member.controller.admin.memberOrder.vo.*;
import com.puhua.module.member.dal.dataobject.memberOrder.MemberOrderDO;
import com.puhua.module.member.service.memberOrder.MemberOrderService;

@Tag(name = "管理后台 - 额度订单信息")
@RestController
@RequestMapping("/member/order")
@Validated
public class MemberOrderController {

    @Resource
    private MemberOrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "创建额度订单信息")
    @PreAuthorize("@ss.hasPermission('member:order:create')")
    public CommonResult<Long> createOrder(@Valid @RequestBody MemberOrderSaveReqVO createReqVO) {
        return success(orderService.createOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新额度订单信息")
    @PreAuthorize("@ss.hasPermission('member:order:update')")
    public CommonResult<Boolean> updateOrder(@Valid @RequestBody MemberOrderSaveReqVO updateReqVO) {
        orderService.updateOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除额度订单信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:order:delete')")
    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
        orderService.deleteOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得额度订单信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:order:query')")
    public CommonResult<MemberOrderRespVO> getOrder(@RequestParam("id") Long id) {
        MemberOrderDO order = orderService.getOrder(id);
        return success(BeanUtils.toBean(order, MemberOrderRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得额度订单信息分页")
    @PreAuthorize("@ss.hasPermission('member:order:query')")
    public CommonResult<PageResult<MemberOrderRespVO>> getOrderPage(@Valid MemberOrderPageReqVO pageReqVO) {
        PageResult<MemberOrderDO> pageResult = orderService.getOrderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MemberOrderRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出额度订单信息 Excel")
    @PreAuthorize("@ss.hasPermission('member:order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderExcel(@Valid MemberOrderPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MemberOrderDO> list = orderService.getOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "额度订单信息.xls", "数据", MemberOrderRespVO.class,
                        BeanUtils.toBean(list, MemberOrderRespVO.class));
    }

}