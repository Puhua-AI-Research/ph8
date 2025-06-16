package com.puhua.module.mp.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ZhangYi
 * @Date 2025年02月18日 10:24
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempWechatQrcodeRespDto {
    /**
     * 临时二维码链接（带票据）
     */
    private String qrcodeUrl;
    /**
     * 票据
     */
    private String ticket;
}
