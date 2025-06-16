package com.puhua.module.member.service.quotaProduct;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.puhua.module.member.controller.admin.quotaProduct.vo.*;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.util.object.BeanUtils;

import com.puhua.module.member.dal.mysql.quotaProduct.QuotaProductMapper;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.member.enums.ErrorCodeConstants.*;

/**
 * 用户积分配额商品 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class QuotaProductServiceImpl implements QuotaProductService {

    @Resource
    private QuotaProductMapper quotaProductMapper;

    @Override
    public Long createQuotaProduct(QuotaProductSaveReqVO createReqVO) {
        // 插入
        QuotaProductDO quotaProduct = BeanUtils.toBean(createReqVO, QuotaProductDO.class);
        quotaProductMapper.insert(quotaProduct);
        // 返回
        return quotaProduct.getId();
    }

    @Override
    public void updateQuotaProduct(QuotaProductSaveReqVO updateReqVO) {
        // 校验存在
        validateQuotaProductExists(updateReqVO.getId());
        // 更新
        QuotaProductDO updateObj = BeanUtils.toBean(updateReqVO, QuotaProductDO.class);
        quotaProductMapper.updateById(updateObj);
    }

    @Override
    public void deleteQuotaProduct(Long id) {
        // 校验存在
        validateQuotaProductExists(id);
        // 删除
        quotaProductMapper.deleteById(id);
    }

    private void validateQuotaProductExists(Long id) {
        if (quotaProductMapper.selectById(id) == null) {
            throw exception(QUOTA_PRODUCT_NOT_EXISTS);
        }
    }

    @Override
    public QuotaProductDO getQuotaProduct(Long id) {
        return quotaProductMapper.selectById(id);
    }

    @Override
    public QuotaProductDO getQuotaProductByProductId(Long productId) {
        return quotaProductMapper.selectByProductId(productId);
    }

    @Override
    public PageResult<QuotaProductDO> getQuotaProductPage(QuotaProductPageReqVO pageReqVO) {
        return quotaProductMapper.selectPage(pageReqVO);
    }

    @Override
    public List<QuotaProductDO> getQuotaProductList() {
        return quotaProductMapper.list();
    }

}