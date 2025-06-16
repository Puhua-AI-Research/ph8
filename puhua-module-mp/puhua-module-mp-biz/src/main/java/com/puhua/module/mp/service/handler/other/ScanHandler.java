package com.puhua.module.mp.service.handler.other;

import cn.hutool.core.util.ObjUtil;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.puhua.module.mp.enums.ApiConstants.GZH_TICKET;

/**
 * 扫码的事件处理器
 */
@Component
public class ScanHandler implements WxMpMessageHandler {


    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> context,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 如果正在进行关注公众号登录，则记录openid
        String eventKey = GZH_TICKET + wxMpXmlMessage.getEventKey();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(eventKey))) {
            stringRedisTemplate.opsForValue().set(eventKey, wxMpXmlMessage.getFromUser());
        }

//        throw new UnsupportedOperationException("未实现该处理，请自行重写");
        return WxMpXmlOutMessage.TEXT().content("登录成功")
                .fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                .build();
    }

}
