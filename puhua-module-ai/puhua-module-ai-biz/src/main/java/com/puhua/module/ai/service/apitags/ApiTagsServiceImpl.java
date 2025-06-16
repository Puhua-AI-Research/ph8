package com.puhua.module.ai.service.apitags;

import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.puhua.module.ai.controller.admin.apitags.vo.*;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.util.object.BeanUtils;

import com.puhua.module.ai.dal.mysql.apitags.ApiTagsMapper;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.ai.enums.ErrorCodeConstants.*;

/**
 * 模型tag Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class ApiTagsServiceImpl implements ApiTagsService {

    @Resource
    private ApiTagsMapper apiTagsMapper;

    @Override
    public Long createApiTags(ApiTagsSaveReqVO createReqVO) {
        // 插入
        ApiTagsDO apiTags = BeanUtils.toBean(createReqVO, ApiTagsDO.class);
        apiTagsMapper.insert(apiTags);
        // 返回
        return apiTags.getId();
    }

    @Override
    public void updateApiTags(ApiTagsSaveReqVO updateReqVO) {
        // 校验存在
        validateApiTagsExists(updateReqVO.getId());
        // 更新
        ApiTagsDO updateObj = BeanUtils.toBean(updateReqVO, ApiTagsDO.class);
        apiTagsMapper.updateById(updateObj);
    }

    @Override
    public void deleteApiTags(Long id) {
        // 校验存在
        validateApiTagsExists(id);
        // 删除
        apiTagsMapper.deleteById(id);
    }

    private void validateApiTagsExists(Long id) {
        if (apiTagsMapper.selectById(id) == null) {
            throw exception(API_TAGS_NOT_EXISTS);
        }
    }

    @Override
    public ApiTagsDO getApiTags(Long id) {
        return apiTagsMapper.selectById(id);
    }

    @Override
    public PageResult<ApiTagsDO> getApiTagsPage(ApiTagsPageReqVO pageReqVO) {
        return apiTagsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ApiTagsDO> getApiTagsList() {
        return apiTagsMapper.getApiTagsList();
    }

    @Override
    public List<ApiTagsDO> getChargeLabelPriority() {
        LambdaQueryWrapperX<ApiTagsDO> queryWrapperX = new LambdaQueryWrapperX<>();
        return apiTagsMapper.selectList(queryWrapperX.eq(ApiTagsDO::getGroupName, "charge")
                .orderByAsc(ApiTagsDO::getPriority));
    }

}