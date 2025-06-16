package com.puhua.module.infra.framework.file.config;

import com.puhua.module.infra.framework.file.core.client.FileClientFactory;
import com.puhua.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author 中航普华
 */
@Configuration(proxyBeanMethods = false)
public class PuhuaFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
