package com.puhua.module.ai.dal.mysql.apithirdkey;

import java.util.*;

import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.ai.dal.dataobject.apithirdkey.ApiThirdKeyDO;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.ai.controller.admin.apithirdkey.vo.*;

/**
 * 三方平台apiKey Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface ApiThirdKeyMapper extends BaseMapperX<ApiThirdKeyDO> {

    default PageResult<ApiThirdKeyDO> selectPage(ApiThirdKeyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiThirdKeyDO>()
                .eqIfPresent(ApiThirdKeyDO::getResourceId, reqVO.getResourceId())
                .eqIfPresent(ApiThirdKeyDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ApiThirdKeyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ApiThirdKeyDO::getId));
    }

    default ApiThirdKeyDO getApiThirdKeyByEndPoint(String endpoint) {
        return selectOne(new LambdaQueryWrapperX<ApiThirdKeyDO>()
                .eqIfPresent(ApiThirdKeyDO::getEndpoint, endpoint)
                .last("limit 1"));
    }
    
}