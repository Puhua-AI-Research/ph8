package com.puhua.module.member.dal.dataobject.memberBalancelog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.puhua.framework.mybatis.core.dataobject.BaseDO;
import com.puhua.module.member.enums.BusinessModeEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户额度流水 DO
 *
 * @author 中航普华
 */
@TableName("member_balance_log")
@KeySequence("member_balance_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceLogDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 变动时间
     */
    private LocalDateTime logTime;
    /**
     * 交易类型
     * <p>
     * 枚举 {@link TODO change_type 对应的类}
     */
    private String changeType;
    /**
     * 变动金额
     */
    private Long changeBalance;
    /**
     * 业务方式
     * <p>
     * 枚举 {@link BusinessModeEnum }
     */
    private String businessMode;

}