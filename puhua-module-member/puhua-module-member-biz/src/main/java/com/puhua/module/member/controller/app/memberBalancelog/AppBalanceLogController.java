package com.puhua.module.member.controller.app.memberBalancelog;

import com.puhua.framework.apilog.core.annotation.ApiAccessLog;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.excel.core.util.ExcelUtils;
import com.puhua.module.member.controller.admin.memberBalancelog.vo.BalanceLogPageReqVO;
import com.puhua.module.member.controller.admin.memberBalancelog.vo.BalanceLogRespVO;
import com.puhua.module.member.controller.admin.memberBalancelog.vo.BalanceLogSaveReqVO;
import com.puhua.module.member.controller.app.memberBalancelog.vo.AppBalanceLogRespVO;
import com.puhua.module.member.dal.dataobject.memberBalancelog.BalanceLogDO;
import com.puhua.module.member.service.memberBalancelog.BalanceLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.puhua.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "C端 - 用户额度流水")
@RestController
@RequestMapping("/member/balance-log")
@Validated
public class AppBalanceLogController {

    @Resource
    private BalanceLogService balanceLogService;


    @GetMapping("/page")
    @Operation(summary = "获得用户额度流水分页")
    public CommonResult<PageResult<AppBalanceLogRespVO>> getBalanceLogPage(@Valid BalanceLogPageReqVO pageReqVO) {
        pageReqVO.setUserId(getLoginUserId());
        PageResult<BalanceLogDO> pageResult = balanceLogService.getBalanceLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppBalanceLogRespVO.class));
    }

}