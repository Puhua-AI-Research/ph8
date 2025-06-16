package com.puhua.module.ai.controller.admin.apithirdkey;

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

import com.puhua.module.ai.controller.admin.apithirdkey.vo.*;
import com.puhua.module.ai.dal.dataobject.apithirdkey.ApiThirdKeyDO;
import com.puhua.module.ai.service.apithirdkey.ApiThirdKeyService;

@Tag(name = "管理后台 - 三方平台apiKey")
@RestController
@RequestMapping("/ai/api-third-key")
@Validated
public class ApiThirdKeyController {

    @Resource
    private ApiThirdKeyService apiThirdKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建三方平台apiKey")
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:create')")
    public CommonResult<Long> createApiThirdKey(@Valid @RequestBody ApiThirdKeySaveReqVO createReqVO) {
        return success(apiThirdKeyService.createApiThirdKey(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新三方平台apiKey")
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:update')")
    public CommonResult<Boolean> updateApiThirdKey(@Valid @RequestBody ApiThirdKeySaveReqVO updateReqVO) {
        apiThirdKeyService.updateApiThirdKey(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除三方平台apiKey")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:delete')")
    public CommonResult<Boolean> deleteApiThirdKey(@RequestParam("id") Long id) {
        apiThirdKeyService.deleteApiThirdKey(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得三方平台apiKey")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:query')")
    public CommonResult<ApiThirdKeyRespVO> getApiThirdKey(@RequestParam("id") Long id) {
        ApiThirdKeyDO apiThirdKey = apiThirdKeyService.getApiThirdKey(id);
        return success(BeanUtils.toBean(apiThirdKey, ApiThirdKeyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得三方平台apiKey分页")
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:query')")
    public CommonResult<PageResult<ApiThirdKeyRespVO>> getApiThirdKeyPage(@Valid ApiThirdKeyPageReqVO pageReqVO) {
        PageResult<ApiThirdKeyDO> pageResult = apiThirdKeyService.getApiThirdKeyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiThirdKeyRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出三方平台apiKey Excel")
    @PreAuthorize("@ss.hasPermission('ai:api-third-key:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiThirdKeyExcel(@Valid ApiThirdKeyPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiThirdKeyDO> list = apiThirdKeyService.getApiThirdKeyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "三方平台apiKey.xls", "数据", ApiThirdKeyRespVO.class,
                        BeanUtils.toBean(list, ApiThirdKeyRespVO.class));
    }

}