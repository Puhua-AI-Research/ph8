package com.puhua.module.member.controller.admin.memberBalancelog;

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

import com.puhua.module.member.controller.admin.memberBalancelog.vo.*;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import com.puhua.module.member.service.memberBalancelog.BalanceLogService;

@Tag(name = "管理后台 - 用户额度流水")
@RestController
@RequestMapping("/member/balance-log")
@Validated
public class BalanceLogController {

    @Resource
    private BalanceLogService balanceLogService;

    @PostMapping("/create")
    @Operation(summary = "创建用户额度流水")
    @PreAuthorize("@ss.hasPermission('member:balance-log:create')")
    public CommonResult<Long> createBalanceLog(@Valid @RequestBody BalanceLogSaveReqVO createReqVO) {
        return success(balanceLogService.createBalanceLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户额度流水")
    @PreAuthorize("@ss.hasPermission('member:balance-log:update')")
    public CommonResult<Boolean> updateBalanceLog(@Valid @RequestBody BalanceLogSaveReqVO updateReqVO) {
        balanceLogService.updateBalanceLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户额度流水")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:balance-log:delete')")
    public CommonResult<Boolean> deleteBalanceLog(@RequestParam("id") Long id) {
        balanceLogService.deleteBalanceLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户额度流水")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:balance-log:query')")
    public CommonResult<BalanceLogRespVO> getBalanceLog(@RequestParam("id") Long id) {
        BalanceLogDO balanceLog = balanceLogService.getBalanceLog(id);
        return success(BeanUtils.toBean(balanceLog, BalanceLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户额度流水分页")
    @PreAuthorize("@ss.hasPermission('member:balance-log:query')")
    public CommonResult<PageResult<BalanceLogRespVO>> getBalanceLogPage(@Valid BalanceLogPageReqVO pageReqVO) {
        PageResult<BalanceLogDO> pageResult = balanceLogService.getBalanceLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BalanceLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户额度流水 Excel")
    @PreAuthorize("@ss.hasPermission('member:balance-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportBalanceLogExcel(@Valid BalanceLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BalanceLogDO> list = balanceLogService.getBalanceLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户额度流水.xls", "数据", BalanceLogRespVO.class,
                        BeanUtils.toBean(list, BalanceLogRespVO.class));
    }

}