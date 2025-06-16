package com.puhua.module.ai.controller.app.openapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import com.puhua.module.ai.service.openapi.ImageService;
import com.puhua.module.member.api.member.MemberApi;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author ZhangYi
 * @Date 2025年03月06日 14:24
 * @Description:
 */
@RestController
@RequestMapping("/ai/images")
public class OpenApiImageController {

    @Resource
    private ApiKeyService apiKeyService;

    @Resource
    MemberApi memberApi;

    @Resource
    ImageService imageService;

    /**
     * 生图
     *
     * @param requestVo 请求
     * @return stream
     */
    @PermitAll
    @PostMapping("generations")
    public GenerateImageResponseVo generations(@RequestHeader HashMap<String, String> headers, @RequestBody GenerateImageRequestVo requestVo) throws IOException {
        String apiKey = headers.get("authorization");
        if (StrUtil.isBlank(apiKey)) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid token");
        }

        apiKey = apiKey.replace("Bearer ", "");
        // key是否存在，是否被禁用
        ApiKeyDO apiKeyDO = apiKeyService.getApiKey(apiKey);
        if (apiKeyDO == null) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid API key");
        }
        if (CommonStatusEnum.isDisable(apiKeyDO.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "API key is disabled");
        }
        return imageService.generateImage(requestVo);
    }
}
