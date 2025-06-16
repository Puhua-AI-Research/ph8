package com.puhua.module.ai.controller.app.task;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.ai.controller.app.task.vo.AppTaskPageReqVO;
import com.puhua.module.ai.controller.app.task.vo.TaskRespVO;
import com.puhua.module.ai.controller.app.task.vo.TaskStatisticsRespVO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "C端 - 用户任务计费")
@RestController
@RequestMapping("/ai/task")
@Validated
public class AppTaskController {

    @Resource
    private TaskService taskService;


    @GetMapping("/list")
    @Operation(summary = "获得用户任务计费分页")
    public CommonResult<PageResult<TaskRespVO>> getTaskPage(@Valid AppTaskPageReqVO pageReqVO) {
        pageReqVO.setUserId(getLoginUserId());
        PageResult<TaskDO> pageResult = taskService.getTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TaskRespVO.class));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得用户任务统计")
    public CommonResult<TaskStatisticsRespVO> statistics() {
        Long loginUserId = getLoginUserId();
        return success(taskService.getTaskStatisticsByUserId(loginUserId));
    }

}