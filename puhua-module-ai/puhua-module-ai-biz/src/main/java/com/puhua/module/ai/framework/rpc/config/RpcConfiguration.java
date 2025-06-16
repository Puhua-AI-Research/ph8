package com.puhua.module.ai.framework.rpc.config;

import com.puhua.module.infra.api.file.FileApi;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {MemberApi.class, FileApi.class})
public class RpcConfiguration {
}
