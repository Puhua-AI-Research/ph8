package com.puhua.module.ai.dal.mysql.modelRepository;

import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.ai.controller.admin.modelrepository.vo.ModelRepositoryPageReqVO;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 模型库 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface ModelRepositoryMapper extends BaseMapperX<ModelRepositoryDO> {

    default PageResult<ModelRepositoryDO> selectPage(ModelRepositoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ModelRepositoryDO>()
                .likeIfPresent(ModelRepositoryDO::getName, reqVO.getName())
                .eqIfPresent(ModelRepositoryDO::getType, reqVO.getType())
                .eqIfPresent(ModelRepositoryDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ModelRepositoryDO::getModelName, reqVO.getModelName())
                .betweenIfPresent(ModelRepositoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ModelRepositoryDO::getId));
    }

    default ModelRepositoryDO getModelRepositoryByName(String name) {
        return selectOne(ModelRepositoryDO::getName, name);
    }

    default List<ModelRepositoryDO> listByTypes(List<String> types) {
        return selectList(new LambdaQueryWrapperX<ModelRepositoryDO>()
                .in(ModelRepositoryDO::getType, types)
                .eq(ModelRepositoryDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
    }
}