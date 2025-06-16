package com.puhua.module.ai.service.apiKey;

import cn.hutool.http.HttpStatus;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import com.puhua.module.ai.controller.admin.apiKey.vo.*;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;

import com.puhua.module.ai.dal.mysql.apiKey.ApiKeyMapper;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.ai.enums.ErrorCodeConstants.*;

/**
 * ApiKey管理 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class ApiKeyServiceImpl implements ApiKeyService {

    @Resource
    private ApiKeyMapper apiKeyMapper;

    @Override
    public Long createApiKey(ApiKeySaveReqVO createReqVO) {
        // 插入
        ApiKeyDO apiKey = BeanUtils.toBean(createReqVO, ApiKeyDO.class);
        apiKeyMapper.insert(apiKey);
        // 返回
        return apiKey.getId();
    }

    @Override
    public void updateApiKey(ApiKeySaveReqVO updateReqVO) {
        // 校验存在
        validateApiKeyExists(updateReqVO.getId());
        // 更新
        ApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, ApiKeyDO.class);
        apiKeyMapper.updateById(updateObj);
    }

    @Override
    public void deleteApiKey(Long id) {
        // 校验存在
        validateApiKeyExists(id);
        // 删除
        apiKeyMapper.deleteById(id);
    }

    private void validateApiKeyExists(Long id) {
        if (apiKeyMapper.selectById(id) == null) {
            throw exception(API_KEY_NOT_EXISTS);
        }
    }

    @Override
    public ApiKeyDO getApiKey(Long id) {
        return apiKeyMapper.selectById(id);
    }

    @Override
    public List<ApiKeyDO> getApiKeyByUserId(Long userId) {
        return apiKeyMapper.selectList(new LambdaQueryWrapperX<ApiKeyDO>()
                .eq(ApiKeyDO::getMemberUserId, userId)
                .orderByDesc(ApiKeyDO::getId));
    }

    @Override
    public PageResult<ApiKeyDO> getApiKeyPage(ApiKeyPageReqVO pageReqVO) {
        return apiKeyMapper.selectPage(pageReqVO);
    }

    @Override
    public ApiKeyDO getApiKey(String apiKey) {
        return apiKeyMapper.selectByApiKey(apiKey);
    }

    @Override
    public void updateAppApiKey(ApiKeyDO apiKey) {
        apiKeyMapper.updateById(apiKey);
    }

}