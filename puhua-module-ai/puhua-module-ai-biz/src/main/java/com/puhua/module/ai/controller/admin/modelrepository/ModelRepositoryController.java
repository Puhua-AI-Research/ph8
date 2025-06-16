package com.puhua.module.ai.controller.admin.modelrepository;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import com.puhua.module.ai.controller.admin.modelrepository.vo.*;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;

@Tag(name = "管理后台 - 模型库")
@RestController
@RequestMapping("/ai/model-repository")
@Validated
public class ModelRepositoryController {

    @Resource
    private ModelRepositoryService modelRepositoryService;

    @PostMapping("/create")
    @Operation(summary = "创建模型库")
    @PreAuthorize("@ss.hasPermission('ai:model-repository:create')")
    public CommonResult<Long> createModelRepository(@Valid @RequestBody ModelRepositorySaveReqVO createReqVO) {
        return success(modelRepositoryService.createModelRepository(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型库")
    @PreAuthorize("@ss.hasPermission('ai:model-repository:update')")
    public CommonResult<Boolean> updateModelRepository(@Valid @RequestBody ModelRepositorySaveReqVO updateReqVO) {
        modelRepositoryService.updateModelRepository(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型库")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:model-repository:delete')")
    public CommonResult<Boolean> deleteModelRepository(@RequestParam("id") Long id) {
        modelRepositoryService.deleteModelRepository(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:model-repository:query')")
    public CommonResult<ModelRepositoryRespVO> getModelRepository(@RequestParam("id") Long id) {
        ModelRepositoryDO modelRepository = modelRepositoryService.getModelRepository(id);
        return success(BeanUtils.toBean(modelRepository, ModelRepositoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型库分页")
    @PreAuthorize("@ss.hasPermission('ai:model-repository:query')")
    public CommonResult<PageResult<ModelRepositoryRespVO>> getModelRepositoryPage(@Valid ModelRepositoryPageReqVO pageReqVO) {
        PageResult<ModelRepositoryDO> pageResult = modelRepositoryService.getModelRepositoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ModelRepositoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出模型库 Excel")
    @PreAuthorize("@ss.hasPermission('ai:model-repository:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportModelRepositoryExcel(@Valid ModelRepositoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ModelRepositoryDO> list = modelRepositoryService.getModelRepositoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "模型库.xls", "数据", ModelRepositoryRespVO.class,
                        BeanUtils.toBean(list, ModelRepositoryRespVO.class));
    }

}