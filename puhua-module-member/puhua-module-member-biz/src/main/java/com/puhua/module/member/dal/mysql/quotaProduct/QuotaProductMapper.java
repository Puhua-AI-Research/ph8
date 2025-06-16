package com.puhua.module.member.dal.mysql.quotaProduct;

import java.io.Serializable;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.member.dal.dataobject.quotaProduct.QuotaProductDO;
import com.puhua.module.member.enums.ProductStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import com.puhua.module.member.controller.admin.quotaProduct.vo.*;

/**
 * 用户积分配额商品 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface QuotaProductMapper extends BaseMapperX<QuotaProductDO> {

    default PageResult<QuotaProductDO> selectPage(QuotaProductPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuotaProductDO>()
                .eqIfPresent(QuotaProductDO::getProductId, reqVO.getProductId())
                .likeIfPresent(QuotaProductDO::getName, reqVO.getName())
                .eqIfPresent(QuotaProductDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(QuotaProductDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(QuotaProductDO::getId));
    }

    default List<QuotaProductDO> list() {
        return selectList(new LambdaQueryWrapperX<QuotaProductDO>()
                .eqIfPresent(QuotaProductDO::getStatus, ProductStatusEnum.ENABLE.getStatus())
                .orderByAsc(QuotaProductDO::getSort));
    }

    default QuotaProductDO selectByProductId(Serializable productId) {
        return selectOne(new LambdaQueryWrapperX<QuotaProductDO>().eq(QuotaProductDO::getProductId, productId).eq(QuotaProductDO::getStatus, ProductStatusEnum.ENABLE.getStatus()).last("limit 1"));
    }
}