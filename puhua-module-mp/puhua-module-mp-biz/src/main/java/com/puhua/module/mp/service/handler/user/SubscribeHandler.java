package com.puhua.module.mp.service.handler.user;

import cn.hutool.core.util.StrUtil;
import com.puhua.module.mp.framework.mp.core.context.MpContextHolder;
import com.puhua.module.mp.service.message.MpAutoReplyService;
import com.puhua.module.mp.service.user.MpUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.Map;

import static com.puhua.module.mp.enums.ApiConstants.GZH_TICKET;

/**
 * 关注的事件处理器
 *
 * @author 中航普华
 */
@Component
@Slf4j
public class SubscribeHandler implements WxMpMessageHandler {

    @Resource
    private MpUserService mpUserService;
    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final String QR_SCENE_PREFIX = "qrscene_";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
                                    WxMpService weixinService, WxSessionManager sessionManager) throws WxErrorException {
        // 第一步，从公众号平台，获取粉丝信息
        log.info("[handle][粉丝({}) 关注]", wxMessage.getFromUser());
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = weixinService.getUserService().userInfo(wxMessage.getFromUser());
        } catch (WxErrorException e) {
            log.error("[handle][粉丝({})] 获取粉丝信息失败！", wxMessage.getFromUser(), e);
        }

        // 第二步，保存粉丝信息
        mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);

        // 登录事件处理
        String wxMessageEventKey = wxMessage.getEventKey();
        if (StrUtil.isNotBlank(wxMessageEventKey) && wxMessageEventKey.startsWith("qrscene_")) {
            wxMessageEventKey = StrUtil.removeAllPrefix(wxMessageEventKey, QR_SCENE_PREFIX);
        }

        // 第三步获取openid，做登录处理
        String eventKey = GZH_TICKET + wxMessageEventKey;


        // 如果正在进行关注公众号登录，则记录openid
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(eventKey))) {
            stringRedisTemplate.opsForValue().set(eventKey, wxMessage.getFromUser());
        }

        // 第四步，回复关注的欢迎语
        return mpAutoReplyService.replyForSubscribe(MpContextHolder.getAppId(), wxMessage);
    }

}
