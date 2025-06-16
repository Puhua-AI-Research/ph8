package com.puhua.module.member.service.quotaProduct;

import java.util.*;

import jakarta.validation.*;
import com.puhua.module.member.controller.admin.quotaProduct.vo.*;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;

/**
 * 用户积分配额商品 Service 接口
 *
 * @author 中航普华
 */
public interface QuotaProductService {

    /**
     * 创建用户积分配额商品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuotaProduct(@Valid QuotaProductSaveReqVO createReqVO);

    /**
     * 更新用户积分配额商品
     *
     * @param updateReqVO 更新信息
     */
    void updateQuotaProduct(@Valid QuotaProductSaveReqVO updateReqVO);

    /**
     * 删除用户积分配额商品
     *
     * @param id 编号
     */
    void deleteQuotaProduct(Long id);

    /**
     * 获得用户积分配额商品
     *
     * @param id 编号
     * @return 用户积分配额商品
     */
    QuotaProductDO getQuotaProduct(Long id);

    /**
     * 获得用户积分配额商品
     *
     * @param productId 编号
     * @return 用户积分配额商品
     */
    QuotaProductDO getQuotaProductByProductId(Long productId);

    /**
     * 获得用户积分配额商品分页
     *
     * @param pageReqVO 分页查询
     * @return 用户积分配额商品分页
     */
    PageResult<QuotaProductDO> getQuotaProductPage(QuotaProductPageReqVO pageReqVO);

    /**
     * 获得用户积分配额商品列表
     *
     * @return 用户积分配额商品列表
     */
    List<QuotaProductDO> getQuotaProductList();

}