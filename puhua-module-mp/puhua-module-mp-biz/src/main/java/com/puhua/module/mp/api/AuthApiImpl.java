package com.puhua.module.mp.api;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.mp.api.auth.MpAuthApi;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeReqDto;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeRespDto;
import com.puhua.module.mp.enums.ApiConstants;
import com.puhua.module.mp.framework.mp.core.MpServiceFactory;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AuthApiImpl implements MpAuthApi {

    @Resource
    @Lazy // 延迟加载，解决循环依赖的问题
    private MpServiceFactory mpServiceFactory;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CommonResult<TempWechatQrcodeRespDto> getQrCodeCreateTmpTicket(TempWechatQrcodeReqDto reqDto) throws WxErrorException {
        WxMpService mpService = mpServiceFactory.getMpService(reqDto.getAppId());
        WxMpQrCodeTicket wxMpQrCodeTicket = mpService.getQrcodeService().qrCodeCreateTmpTicket(reqDto.getTicket(), ApiConstants.GZH_TEMP_TICKET_TIMEOUT);
        stringRedisTemplate.opsForValue().set(ApiConstants.GZH_TICKET + reqDto.getTicket(), reqDto.getTicket(), Duration.ofSeconds(ApiConstants.GZH_TEMP_TICKET_TIMEOUT));
        TempWechatQrcodeRespDto respDto = TempWechatQrcodeRespDto
                .builder()
                .qrcodeUrl(wxMpQrCodeTicket.getUrl())
                .ticket(reqDto.getTicket())
                .build();
        return CommonResult.success(respDto);
    }

}
