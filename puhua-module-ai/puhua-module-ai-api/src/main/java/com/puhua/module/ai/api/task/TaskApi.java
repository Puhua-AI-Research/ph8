package com.puhua.module.ai.api.task;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.ai.api.task.dto.TaskPageReqDto;
import com.puhua.module.ai.api.task.dto.TaskRespDto;
import com.puhua.module.ai.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Author ZhangYi
 * @Date 2025年03月14日 15:59
 * @Description:
 */

@FeignClient(name = ApiConstants.NAME) // TODO 中航普华：fallbackFactory =
@Tag(name = "RPC 服务 - 任务查询")
public interface TaskApi {

    String PREFIX = ApiConstants.PREFIX + "/auth";

    @PostMapping(PREFIX + "/getTasks")
    @Operation(summary = "任务查询")
    List<TaskRespDto> taskList(TaskPageReqDto reqDto);

}