package com.puhua.module.ai.dal.dataobject.apithirdkey;

import lombok.*;

import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * 三方平台apiKey DO
 *
 * @author 中航普华
 */
@TableName("ai_api_third_key")
@KeySequence("ai_api_third_key_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiThirdKeyDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 资源标识
     */
    private String resourceId;
    /**
     * ak
     */
    private String ak;
    /**
     * sk
     */
    private String sk;
    /**
     * key
     */
    private String apiKey;
    /**
     * account
     */
    private String account;
    /**
     * 状态
     * <p>
     * 枚举 {@link TODO enable_status 对应的类}
     */
    private Integer status;
    /**
     * qps
     */
    private Integer qps;
    /**
     * endpoint
     */
    private String endpoint;

}