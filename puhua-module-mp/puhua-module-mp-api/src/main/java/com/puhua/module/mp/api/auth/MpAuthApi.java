package com.puhua.module.mp.api.auth;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeReqDto;
import com.puhua.module.mp.api.auth.dto.TempWechatQrcodeRespDto;
import com.puhua.module.mp.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME) // TODO 中航普华：fallbackFactory =
@Tag(name = "RPC 服务 - 关注微信公众号登录鉴权相关")
public interface MpAuthApi {

    String PREFIX = ApiConstants.PREFIX + "/auth";

    @PostMapping(PREFIX + "/getWechatQrcode")
    @Operation(summary = "根据参数键查询参数值")
    CommonResult<TempWechatQrcodeRespDto> getQrCodeCreateTmpTicket(@RequestBody TempWechatQrcodeReqDto reqDto) throws WxErrorException, WxErrorException, WxErrorException;

}
