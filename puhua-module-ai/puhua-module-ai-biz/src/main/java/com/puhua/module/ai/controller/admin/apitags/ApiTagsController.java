package com.puhua.module.ai.controller.admin.apitags;

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

import com.puhua.module.ai.controller.admin.apitags.vo.*;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import com.puhua.module.ai.service.apitags.ApiTagsService;

@Tag(name = "管理后台 - 模型tag")
@RestController
@RequestMapping("/ai/api-tags")
@Validated
public class ApiTagsController {

    @Resource
    private ApiTagsService apiTagsService;

    @PostMapping("/create")
    @Operation(summary = "创建模型tag")
    @PreAuthorize("@ss.hasPermission('ai:api-tags:create')")
    public CommonResult<Long> createApiTags(@Valid @RequestBody ApiTagsSaveReqVO createReqVO) {
        return success(apiTagsService.createApiTags(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型tag")
    @PreAuthorize("@ss.hasPermission('ai:api-tags:update')")
    public CommonResult<Boolean> updateApiTags(@Valid @RequestBody ApiTagsSaveReqVO updateReqVO) {
        apiTagsService.updateApiTags(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型tag")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:api-tags:delete')")
    public CommonResult<Boolean> deleteApiTags(@RequestParam("id") Long id) {
        apiTagsService.deleteApiTags(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型tag")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:api-tags:query')")
    public CommonResult<ApiTagsRespVO> getApiTags(@RequestParam("id") Long id) {
        ApiTagsDO apiTags = apiTagsService.getApiTags(id);
        return success(BeanUtils.toBean(apiTags, ApiTagsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型tag分页")
    @PreAuthorize("@ss.hasPermission('ai:api-tags:query')")
    public CommonResult<PageResult<ApiTagsRespVO>> getApiTagsPage(@Valid ApiTagsPageReqVO pageReqVO) {
        PageResult<ApiTagsDO> pageResult = apiTagsService.getApiTagsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiTagsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出模型tag Excel")
    @PreAuthorize("@ss.hasPermission('ai:api-tags:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiTagsExcel(@Valid ApiTagsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiTagsDO> list = apiTagsService.getApiTagsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "模型tag.xls", "数据", ApiTagsRespVO.class,
                        BeanUtils.toBean(list, ApiTagsRespVO.class));
    }

}