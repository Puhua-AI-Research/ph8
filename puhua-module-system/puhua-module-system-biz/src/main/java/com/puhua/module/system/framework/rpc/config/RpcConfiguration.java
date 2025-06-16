package com.puhua.module.system.framework.rpc.config;

import com.puhua.module.infra.api.config.ConfigApi;
import com.puhua.module.infra.api.file.FileApi;
import com.puhua.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
