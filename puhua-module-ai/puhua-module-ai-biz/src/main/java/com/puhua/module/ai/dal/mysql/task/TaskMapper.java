package com.puhua.module.ai.dal.mysql.task;

import java.util.*;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.ai.dal.dataobject.task.TaskDO;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.ai.controller.admin.task.vo.*;

/**
 * 用户任务计费 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface TaskMapper extends BaseMapperX<TaskDO> {

    default PageResult<TaskDO> selectPage(TaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TaskDO>()
                .eqIfPresent(TaskDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TaskDO::getModelId, reqVO.getModelId())
                .likeIfPresent(TaskDO::getModelName, reqVO.getModelName())
                .eqIfPresent(TaskDO::getModelType, reqVO.getModelType())
                .eqIfPresent(TaskDO::getVersion, reqVO.getVersion())
                .eqIfPresent(TaskDO::getChargeMode, reqVO.getChargeMode())
                .betweenIfPresent(TaskDO::getCallTime, reqVO.getCallTime())
                .eqIfPresent(TaskDO::getTotalFee, reqVO.getTotalFee())
                .betweenIfPresent(TaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TaskDO::getId));
    }

    default List<TaskDO> selectList(TaskPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TaskDO>()
                .eqIfPresent(TaskDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TaskDO::getModelId, reqVO.getModelId())
                .likeIfPresent(TaskDO::getModelName, reqVO.getModelName())
                .eqIfPresent(TaskDO::getModelType, reqVO.getModelType())
                .eqIfPresent(TaskDO::getVersion, reqVO.getVersion())
                .eqIfPresent(TaskDO::getChargeMode, reqVO.getChargeMode())
                .betweenIfPresent(TaskDO::getCallTime, reqVO.getCallTime())
                .eqIfPresent(TaskDO::getTotalFee, reqVO.getTotalFee())
                .betweenIfPresent(TaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TaskDO::getId));
    }

}