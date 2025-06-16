package com.puhua.module.ai.dal.dataobject.apitags;

import lombok.*;

import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * 模型tag DO
 *
 * @author 中航普华
 */
@TableName("ai_api_tags")
@KeySequence("ai_api_tags_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTagsDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 展示优先级（模型图片标签）
     */
    private Integer priority;
    /**
     * 分组
     */
    private String groupName;
    /**
     * 状态
     * <p>
     * 枚举 {@link TODO enable_status 对应的类}
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}