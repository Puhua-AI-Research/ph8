package com.puhua.module.ai.dal.mysql.apiKey;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.ai.controller.admin.apiKey.vo.ApiKeyPageReqVO;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ApiKey管理 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface ApiKeyMapper extends BaseMapperX<ApiKeyDO> {

    default PageResult<ApiKeyDO> selectPage(ApiKeyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiKeyDO>()
                .eqIfPresent(ApiKeyDO::getMemberUserId, reqVO.getMemberUserId())
                .likeIfPresent(ApiKeyDO::getApiKey, reqVO.getApiKey())
                .likeIfPresent(ApiKeyDO::getName, reqVO.getName())
                .eqIfPresent(ApiKeyDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ApiKeyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ApiKeyDO::getId));
    }

    default ApiKeyDO selectByApiKey(String apiKey){
        return selectOne(ApiKeyDO::getApiKey, apiKey);
    }
}