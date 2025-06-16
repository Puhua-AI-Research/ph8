package com.puhua.module.ai.dal.dataobject.apiKey;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * ApiKey管理 DO
 *
 * @author 中航普华
 */
@TableName("ai_api_key")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户id
     */
    private Long memberUserId;
    /**
     * key
     */
    private String apiKey;
    /**
     * 名称
     */
    private String name;
    /**
     * 最后使用时间
     */
    private String lastUseTime;
    /**
     * 状态
     *
     * 枚举 {@link TODO enable_status 对应的类}
     */
    private Integer status;

}