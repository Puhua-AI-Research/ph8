package com.puhua.module.mp.service.handler.menu;

import com.puhua.module.mp.framework.mp.core.context.MpContextHolder;
import com.puhua.module.mp.service.menu.MpMenuService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 自定义菜单的事件处理器
 *
 * 逻辑：粉丝点击菜单时，触发对应的回复
 *
 * @author 中航普华
 */
@Component
public class MenuHandler implements WxMpMessageHandler {

    @Resource
    private MpMenuService mpMenuService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
                                    WxMpService weixinService, WxSessionManager sessionManager) {
        return mpMenuService.reply(MpContextHolder.getAppId(), wxMessage.getEventKey(), wxMessage.getFromUser());
    }

}
