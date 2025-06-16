package com.puhua.module.ai.task;

import com.puhua.module.ai.service.task.TaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author ZhangYi
 * @Date 2025年03月14日 15:35
 * @Description: 后台轮询异步视频任务
 */
@Component
@Slf4j
public class UserTaskJob {
    @Resource
    TaskService taskService;

    @XxlJob("VideoTaskPoolAsyncJob")
    public String execute() {
        taskService.handleVideoTaskPoolAsyncJob();
        return "";
    }

}
