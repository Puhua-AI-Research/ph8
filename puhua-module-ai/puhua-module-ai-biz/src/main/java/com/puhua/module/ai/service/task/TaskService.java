package com.puhua.module.ai.service.task;

import java.util.*;

import com.puhua.module.ai.controller.app.task.vo.TaskStatisticsRespVO;
import jakarta.validation.*;
import com.puhua.module.ai.controller.admin.task.vo.*;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;

/**
 * 用户任务计费 Service 接口
 *
 * @author 中航普华
 */
public interface TaskService {

    /**
     * 创建用户任务计费
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTask(@Valid TaskSaveReqVO createReqVO);

    /**
     * 更新用户任务计费
     *
     * @param updateReqVO 更新信息
     */
    void updateTask(@Valid TaskSaveReqVO updateReqVO);

    /**
     * 删除用户任务计费
     *
     * @param id 编号
     */
    void deleteTask(Long id);

    /**
     * 获得用户任务计费
     *
     * @param id 编号
     * @return 用户任务计费
     */
    TaskDO getTask(Long id);

    /**
     * 获得用户任务计费分页
     *
     * @param pageReqVO 分页查询
     * @return 用户任务计费分页
     */
    PageResult<TaskDO> getTaskPage(TaskPageReqVO pageReqVO);

    /**
     * 用户数据统计
     *
     * @param loginUserId 用户id
     * @return res
     */
    TaskStatisticsRespVO getTaskStatisticsByUserId(Long loginUserId);

    /**
     * 处理轮询任务
     */
    void handleVideoTaskPoolAsyncJob();
}