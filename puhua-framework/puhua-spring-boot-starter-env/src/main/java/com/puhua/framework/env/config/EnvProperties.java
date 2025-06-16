package com.puhua.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 * @author 中航普华
 */
@ConfigurationProperties(prefix = "puhua.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "puhua.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
