package com.puhua.module.ai.service.apiKey;

import java.util.*;

import jakarta.validation.*;
import com.puhua.module.ai.controller.admin.apiKey.vo.*;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.framework.common.pojo.PageResult;

/**
 * ApiKey管理 Service 接口
 *
 * @author 中航普华
 */
public interface ApiKeyService {

    /**
     * 创建ApiKey管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApiKey(@Valid ApiKeySaveReqVO createReqVO);

    /**
     * 更新ApiKey管理
     *
     * @param updateReqVO 更新信息
     */
    void updateApiKey(@Valid ApiKeySaveReqVO updateReqVO);

    /**
     * 删除ApiKey管理
     *
     * @param id 编号
     */
    void deleteApiKey(Long id);

    /**
     * 获得ApiKey管理
     *
     * @param id 编号
     * @return ApiKey管理
     */
    ApiKeyDO getApiKey(Long id);

    /**
     * 根据用户id获得ApiKey管理
     *
     * @param userId 编号
     * @return ApiKey管理
     */
    List<ApiKeyDO> getApiKeyByUserId(Long userId);

    /**
     * 获得ApiKey管理分页
     *
     * @param pageReqVO 分页查询
     * @return ApiKey管理分页
     */
    PageResult<ApiKeyDO> getApiKeyPage(ApiKeyPageReqVO pageReqVO);


    /**
     * 获得ApiKey管理
     *
     * @param apiKey 编号
     * @return ApiKey管理
     */
    ApiKeyDO getApiKey(String apiKey);

    /**
     * 更新ApiKey
     *
     * @param apiKey 更新信息
     */
    void updateAppApiKey(ApiKeyDO apiKey);
}