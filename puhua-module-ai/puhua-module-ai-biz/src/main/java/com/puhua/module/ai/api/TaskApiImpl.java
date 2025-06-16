package com.puhua.module.ai.api;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.ai.api.task.TaskApi;
import com.puhua.module.ai.api.task.dto.TaskPageReqDto;
import com.puhua.module.ai.api.task.dto.TaskRespDto;
import com.puhua.module.ai.controller.admin.task.vo.TaskPageReqVO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author ZhangYi
 * @Date 2025年03月14日 16:05
 * @Description:
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class TaskApiImpl implements TaskApi {

    @Resource
    TaskMapper taskMapper;

    @Override
    public List<TaskRespDto> taskList(@RequestBody TaskPageReqDto reqDto) {

        TaskPageReqVO reqVO = BeanUtils.toBean(reqDto, TaskPageReqVO.class);
        return BeanUtils.toBean(taskMapper.selectList(reqVO), TaskRespDto.class);
    }
}
