package com.puhua.module.ai.dal.mysql.apitags;

import java.util.*;

import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.ai.controller.admin.apitags.vo.*;

/**
 * 模型tag Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface ApiTagsMapper extends BaseMapperX<ApiTagsDO> {

    default PageResult<ApiTagsDO> selectPage(ApiTagsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiTagsDO>()
                .likeIfPresent(ApiTagsDO::getName, reqVO.getName())
                .eqIfPresent(ApiTagsDO::getType, reqVO.getType())
                .eqIfPresent(ApiTagsDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ApiTagsDO::getSort, reqVO.getSort())
                .betweenIfPresent(ApiTagsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ApiTagsDO::getId));
    }

    default List<ApiTagsDO> getApiTagsList() {
        return selectList(new LambdaQueryWrapperX<ApiTagsDO>()
                .eq(ApiTagsDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(ApiTagsDO::getSort)
                .orderByDesc(ApiTagsDO::getId));
    }
}