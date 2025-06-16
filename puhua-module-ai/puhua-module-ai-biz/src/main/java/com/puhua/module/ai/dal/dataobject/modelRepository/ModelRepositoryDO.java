package com.puhua.module.ai.dal.dataobject.modelRepository;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 模型库 DO
 *
 * @author 中航普华
 */
@TableName("ai_model_repository")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRepositoryDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 模型名称
     */
    private String name;
    /**
     * 最大token数
     */
    private Integer maxTokens;
    /**
     * api地址
     */
    private String url;
    /**
     * 模型分类
     * <p>
     * 枚举 {@link TODO model_type 对应的类}
     */
    private String type;
    /**
     * 状态
     * <p>
     * 枚举 {@link TODO enable_status 对应的类}
     */
    private Integer status;
    /**
     * 模型中文名称
     */
    private String modelName;
    /**
     * 模型简介
     */
    private String briefIntroduction;
    /**
     * 封面logo
     */
    private String coverLogo;
    /**
     * 封面
     */
    private String coverUrl;
    /**
     * 图片列表
     */
    private String images;
    /**
     * 模型介绍
     */
    private String introduction;
    /**
     * 原价
     */
    private Integer originalPrice;
    /**
     * 推理费用
     */
    private Integer inferencePrice;
    /**
     * 计费方式
     * <p>
     * 枚举 {@link TODO 模型计费方式 对应的类}
     */
    private String chargeMode;
    /**
     * 结算周期
     * <p>
     * 枚举 {@link TODO model_settlement_interval 对应的类}
     */
    private String settlementInterval;
    /**
     * 代码示例
     */
    private String example;
    /**
     * curl示例
     */
    private String curlExample;
    /**
     * 版本号
     */
    private String version;
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
    /**
     * 更新内容
     */
    private String updateContent;
    /**
     * 模型id
     */
    private String modelId;
    /**
     * 上线时间
     */
    private LocalDateTime pubTime;
    /**
     * 支持芯片类型
     * <p>
     * 枚举 {@link TODO model_chips 对应的类}
     */
    private String supportChips;

    private String apiKey;

    /**
     * 标签列表
     */
    private String labels;
    /**
     * tags列表
     */
    private String tags;
    /**
     * 厂商
     */
    private String manufacturers;
    /**
     * 是否支持体验 1-是 0-否
     */
    private Integer experience;
    /**
     * 限流配置
     */
    private Integer rpm;
    /**
     * 限流配置
     */
    private Integer tpm;
    /**
     * qps
     */
    private Integer qps;
    /**
     * 任务模式
     * 枚举 {@link com.puhua.module.ai.enums.TaskMode}
     */
    private String taskMode;
}