package com.puhua.module.ai.service.apithirdkey;

import java.util.*;

import jakarta.validation.*;
import com.puhua.module.ai.controller.admin.apithirdkey.vo.*;
import com.puhua.module.ai.dal.dataobject.apithirdkey.ApiThirdKeyDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;

/**
 * 三方平台apiKey Service 接口
 *
 * @author 中航普华
 */
public interface ApiThirdKeyService {

    /**
     * 创建三方平台apiKey
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApiThirdKey(@Valid ApiThirdKeySaveReqVO createReqVO);

    /**
     * 更新三方平台apiKey
     *
     * @param updateReqVO 更新信息
     */
    void updateApiThirdKey(@Valid ApiThirdKeySaveReqVO updateReqVO);

    /**
     * 删除三方平台apiKey
     *
     * @param id 编号
     */
    void deleteApiThirdKey(Long id);

    /**
     * 获得三方平台apiKey
     *
     * @param id 编号
     * @return 三方平台apiKey
     */
    ApiThirdKeyDO getApiThirdKey(Long id);

    /**
     * 获得三方平台apiKey分页
     *
     * @param pageReqVO 分页查询
     * @return 三方平台apiKey分页
     */
    PageResult<ApiThirdKeyDO> getApiThirdKeyPage(ApiThirdKeyPageReqVO pageReqVO);

    /**
     * 根据endpoint获取配置
     *
     * @param endpoint 端点
     * @return 配置
     */
    ApiThirdKeyDO getApiThirdKeyByEndPoint(String endpoint);
}