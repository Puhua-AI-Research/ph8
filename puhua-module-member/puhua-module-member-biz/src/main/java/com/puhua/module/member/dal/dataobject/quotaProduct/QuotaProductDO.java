package com.puhua.module.member.dal.dataobject.quotaProduct;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户积分配额商品 DO
 *
 * @author 中航普华
 */
@TableName("member_quota_product")
@KeySequence("member_quota_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaProductDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 商品id
     */
    private Long productId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 配额
     */
    private Integer quota;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link TODO member_product_status 对应的类}
     */
    private Integer status;
    /**
     * 描述
     */
    private String description;

}