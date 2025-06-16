package com.puhua.module.mp.dal.dataobject.account;

import com.puhua.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 公众号账号 DO
 *
 * @author 中航普华
 */
@TableName("mp_account")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpAccountDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 公众号名称
     */
    private String name;
    /**
     * 公众号账号
     */
    private String account;
    /**
     * 公众号 appid
     */
    private String appId;
    /**
     * 公众号密钥
     */
    private String appSecret;
    /**
     * 公众号token
     */
    private String token;
    /**
     * 消息加解密密钥
     */
    private String aesKey;
    /**
     * 二维码图片 URL
     */
    private String qrCodeUrl;
    /**
     * 备注
     */
    private String remark;

}
