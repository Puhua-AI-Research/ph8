package com.puhua.module.ai.controller.admin.task;

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

import com.puhua.module.ai.controller.admin.task.vo.*;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.service.task.TaskService;

@Tag(name = "管理后台 - 用户任务计费")
@RestController
@RequestMapping("/ai/task")
@Validated
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping("/create")
    @Operation(summary = "创建用户任务计费")
    @PreAuthorize("@ss.hasPermission('ai:task:create')")
    public CommonResult<Long> createTask(@Valid @RequestBody TaskSaveReqVO createReqVO) {
        return success(taskService.createTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户任务计费")
    @PreAuthorize("@ss.hasPermission('ai:task:update')")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody TaskSaveReqVO updateReqVO) {
        taskService.updateTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户任务计费")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:task:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        taskService.deleteTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户任务计费")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:task:query')")
    public CommonResult<TaskRespVO> getTask(@RequestParam("id") Long id) {
        TaskDO task = taskService.getTask(id);
        return success(BeanUtils.toBean(task, TaskRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户任务计费分页")
    @PreAuthorize("@ss.hasPermission('ai:task:query')")
    public CommonResult<PageResult<TaskRespVO>> getTaskPage(@Valid TaskPageReqVO pageReqVO) {
        PageResult<TaskDO> pageResult = taskService.getTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TaskRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户任务计费 Excel")
    @PreAuthorize("@ss.hasPermission('ai:task:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTaskExcel(@Valid TaskPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TaskDO> list = taskService.getTaskPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户任务计费.xls", "数据", TaskRespVO.class,
                        BeanUtils.toBean(list, TaskRespVO.class));
    }

}