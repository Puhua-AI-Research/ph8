package com.puhua.framework.pay.config;

import com.puhua.framework.pay.core.client.PayClientFactory;
import com.puhua.framework.pay.core.client.impl.PayClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 支付配置类
 *
 * @author 中航普华
 */
@AutoConfiguration
public class PuhuaPayAutoConfiguration {

    @Bean
    public PayClientFactory payClientFactory() {
        return new PayClientFactoryImpl();
    }

}
