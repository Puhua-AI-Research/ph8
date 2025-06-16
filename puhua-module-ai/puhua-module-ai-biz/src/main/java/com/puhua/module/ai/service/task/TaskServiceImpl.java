package com.puhua.module.ai.service.task;

import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.ai.controller.admin.task.vo.TaskPageReqVO;
import com.puhua.module.ai.controller.admin.task.vo.TaskSaveReqVO;
import com.puhua.module.ai.controller.app.task.vo.TaskStatisticsRespVO;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.module.ai.dal.mysql.task.TaskMapper;
import com.puhua.module.ai.enums.TaskMode;
import com.puhua.module.ai.model.video.VideoModel;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.ai.enums.ErrorCodeConstants.TASK_NOT_EXISTS;

/**
 * 用户任务计费 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    ModelRepositoryService modelRepositoryService;

    @Override
    public Long createTask(TaskSaveReqVO createReqVO) {
        // 插入
        TaskDO task = BeanUtils.toBean(createReqVO, TaskDO.class);
        taskMapper.insert(task);
        // 返回
        return task.getId();
    }

    @Override
    public void updateTask(TaskSaveReqVO updateReqVO) {
        // 校验存在
        validateTaskExists(updateReqVO.getId());
        // 更新
        TaskDO updateObj = BeanUtils.toBean(updateReqVO, TaskDO.class);
        taskMapper.updateById(updateObj);
    }

    @Override
    public void deleteTask(Long id) {
        // 校验存在
        validateTaskExists(id);
        // 删除
        taskMapper.deleteById(id);
    }

    private void validateTaskExists(Long id) {
        if (taskMapper.selectById(id) == null) {
            throw exception(TASK_NOT_EXISTS);
        }
    }

    @Override
    public TaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public PageResult<TaskDO> getTaskPage(TaskPageReqVO pageReqVO) {
        return taskMapper.selectPage(pageReqVO);
    }

    @Override
    public TaskStatisticsRespVO getTaskStatisticsByUserId(Long loginUserId) {
        List<TaskDO> taskDOS = taskMapper.selectList(TaskPageReqVO.builder().userId(loginUserId).build());
        // 总费用
        BigDecimal totalFee = taskDOS.stream().map(TaskDO::getTotalFee).reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTokens = taskDOS.stream().mapToLong(TaskDO::getTokens) // 提取 tokens 并转换为 LongStream
                .filter(Objects::nonNull) // 过滤掉 null 值，防止 NullPointerException
                .sum();// 求和


        long modelNums = taskDOS.stream().map(TaskDO::getModelId).distinct().count();

        return TaskStatisticsRespVO.builder().totalApiTimes(taskDOS.size()).totalFee(totalFee).totalModels(modelNums).totalTokens(totalTokens).build();
    }

    @Override
    public void handleVideoTaskPoolAsyncJob() {
        LambdaQueryWrapperX<TaskDO> taskDOLambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        taskDOLambdaQueryWrapperX.eq(TaskDO::getTaskMode, TaskMode.ASYNC_POLLING.getType())
                .eq(TaskDO::getStatus, "running")
                .last("limit 1");
        TaskDO taskDO = taskMapper.selectOne(taskDOLambdaQueryWrapperX);
        if (Objects.nonNull(taskDO)) {
            VideoModel videoModel = modelRepositoryService.getVideoModel(taskDO.getModelName());
            HashMap requestBody = JSONObject.parseObject(taskDO.getRequestBody(), HashMap.class);
            JSONObject jsonObject = videoModel.generateVideoSync(requestBody);
            if (jsonObject.containsKey("url")) {
                taskDO.setStatus("succeeded");
                taskDO.setResponseBody(jsonObject.toJSONString());
                taskMapper.updateById(taskDO);
            }else {
                taskDO.setStatus("failed");
                taskDO.setResponseBody(jsonObject.toJSONString());
                taskMapper.updateById(taskDO);
            }
        }
    }


}