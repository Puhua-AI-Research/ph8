package com.puhua.module.ai.dal.dataobject.task;

import lombok.*;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户任务计费 DO
 *
 * @author 中航普华
 */
@TableName("ai_task")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 模型id
     */
    private String modelId;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 模型类型
     * <p>
     * 枚举 {@link TODO model_type 对应的类}
     */
    private String modelType;
    /**
     * 版本号
     */
    private String version;
    /**
     * 请求内容
     */
    private String requestBody;
    /**
     * 返回内容
     */
    private String responseBody;
    /**
     * 计费方式
     * <p>
     * 枚举 {@link TODO model_charge_mode 对应的类}
     */
    private String chargeMode;
    /**
     * 调用单价
     */
    private BigDecimal unitPrice;
    /**
     * 调用时间
     */
    private LocalDateTime callTime;
    /**
     * 金额
     */
    private BigDecimal totalFee;
    /**
     * 使用的token数
     */
    private Long tokens;
    /**
     * endpoint
     */
    private String endpoint;
    /**
     * 任务状态
     */
    private String status;
    /**
     * 任务模式
     */
    private String taskMode;
}