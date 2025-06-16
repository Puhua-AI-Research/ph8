package com.puhua.module.member.framework.rpc.config;

import com.puhua.module.ai.api.task.TaskApi;
import com.puhua.module.mp.api.auth.MpAuthApi;
import com.puhua.module.pay.api.order.PayOrderApi;
import com.puhua.module.system.api.logger.LoginLogApi;
import com.puhua.module.system.api.mail.MailSendApi;
import com.puhua.module.system.api.sms.SmsCodeApi;
import com.puhua.module.system.api.social.SocialClientApi;
import com.puhua.module.system.api.social.SocialUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {MailSendApi.class, TaskApi.class,SmsCodeApi.class, LoginLogApi.class, SocialUserApi.class, SocialClientApi.class, MpAuthApi.class, PayOrderApi.class})
public class RpcConfiguration {
}
