package com.puhua.module.ai.controller.admin.apiKey;

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

import com.puhua.module.ai.controller.admin.apiKey.vo.*;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.service.apiKey.ApiKeyService;

@Tag(name = "管理后台 - ApiKey管理")
@RestController
@RequestMapping("/ai/api-key")
@Validated
public class ApiKeyController {

    @Resource
    private ApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建ApiKey管理")
    @PreAuthorize("@ss.hasPermission('ai:api-key:create')")
    public CommonResult<Long> createApiKey(@Valid @RequestBody ApiKeySaveReqVO createReqVO) {
        return success(apiKeyService.createApiKey(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新ApiKey管理")
    @PreAuthorize("@ss.hasPermission('ai:api-key:update')")
    public CommonResult<Boolean> updateApiKey(@Valid @RequestBody ApiKeySaveReqVO updateReqVO) {
        apiKeyService.updateApiKey(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除ApiKey管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:api-key:delete')")
    public CommonResult<Boolean> deleteApiKey(@RequestParam("id") Long id) {
        apiKeyService.deleteApiKey(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得ApiKey管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:api-key:query')")
    public CommonResult<ApiKeyRespVO> getApiKey(@RequestParam("id") Long id) {
        ApiKeyDO apiKey = apiKeyService.getApiKey(id);
        return success(BeanUtils.toBean(apiKey, ApiKeyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得ApiKey管理分页")
    @PreAuthorize("@ss.hasPermission('ai:api-key:query')")
    public CommonResult<PageResult<ApiKeyRespVO>> getApiKeyPage(@Valid ApiKeyPageReqVO pageReqVO) {
        PageResult<ApiKeyDO> pageResult = apiKeyService.getApiKeyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiKeyRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出ApiKey管理 Excel")
    @PreAuthorize("@ss.hasPermission('ai:api-key:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiKeyExcel(@Valid ApiKeyPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiKeyDO> list = apiKeyService.getApiKeyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "ApiKey管理.xls", "数据", ApiKeyRespVO.class,
                        BeanUtils.toBean(list, ApiKeyRespVO.class));
    }

}