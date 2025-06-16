package com.puhua.module.ai.service.apitags;

import java.util.*;

import jakarta.validation.*;
import com.puhua.module.ai.controller.admin.apitags.vo.*;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;

/**
 * 模型tag Service 接口
 *
 * @author 中航普华
 */
public interface ApiTagsService {

    /**
     * 创建模型tag
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApiTags(@Valid ApiTagsSaveReqVO createReqVO);

    /**
     * 更新模型tag
     *
     * @param updateReqVO 更新信息
     */
    void updateApiTags(@Valid ApiTagsSaveReqVO updateReqVO);

    /**
     * 删除模型tag
     *
     * @param id 编号
     */
    void deleteApiTags(Long id);

    /**
     * 获得模型tag
     *
     * @param id 编号
     * @return 模型tag
     */
    ApiTagsDO getApiTags(Long id);

    /**
     * 获得模型tag分页
     *
     * @param pageReqVO 分页查询
     * @return 模型tag分页
     */
    PageResult<ApiTagsDO> getApiTagsPage(ApiTagsPageReqVO pageReqVO);

    /**
     * 获得模型tag列表
     *
     * @return 模型tag列表
     */
    List<ApiTagsDO> getApiTagsList();

    /**
     * 获取label优先级
     *
     * @return list
     */
    public List<ApiTagsDO> getChargeLabelPriority();
}