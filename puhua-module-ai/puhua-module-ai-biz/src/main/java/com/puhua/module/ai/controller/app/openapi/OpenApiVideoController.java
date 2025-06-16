package com.puhua.module.ai.controller.app.openapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import com.puhua.module.ai.service.openapi.VideoService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
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
@RequestMapping("/ai/videos")
public class OpenApiVideoController {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_MSG = "Invalid token";
    private static final String API_KEY_DISABLED_MSG = "API key disabled";
    private static final String USER_NOT_FOUND_MSG = "User not found";

    @Resource
    private ApiKeyService apiKeyService;
    @Resource
    private MemberApi memberApi;
    @Resource
    private VideoService videoService;

    /**
     * 创建视频生成任务
     *
     * @param authHeader 请求头authKey
     * @param requestVo  请求体
     * @return 任务结果
     */
    @PermitAll
    @PostMapping("/generations")
    public JSONObject createGenerationTask(@RequestHeader("Authorization") String authHeader, @RequestBody HashMap<String, Object> requestVo) throws IOException {

        MemberUserInfoRespVO user = validateAndGetUser(authHeader);
        return videoService.generate(user.getId(), requestVo);
    }

    /**
     * 查询视频生成任务
     *
     * @param apiKey 请求头
     * @param taskId 任务ID
     * @return 任务信息
     */
    @PermitAll
    @GetMapping("/get")
    public JSONObject getGenerationTask(@RequestHeader("Authorization") String apiKey, @RequestParam String taskId) throws IOException {

        validateAndGetUser(apiKey);
        return videoService.get(taskId);
    }

    /**
     * 统一验证逻辑：
     * 1. 检查API Key有效性
     * 2. 获取用户信息
     * 3. 校验用户余额
     */
    private MemberUserInfoRespVO validateAndGetUser(String authHeader) {
        if (StrUtil.isBlank(authHeader)) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, INVALID_TOKEN_MSG);
        }

        // 提取并清理API Key
        String apiKey = authHeader.startsWith(BEARER_PREFIX) ? authHeader.substring(BEARER_PREFIX.length()) : authHeader;

        ApiKeyDO apiKeyDO = apiKeyService.getApiKey(apiKey);
        if (apiKeyDO == null) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid API key");
        }
        if (CommonStatusEnum.isDisable(apiKeyDO.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, API_KEY_DISABLED_MSG);
        }

        MemberUserInfoRespVO user = memberApi.getMemberUser(apiKeyDO.getMemberUserId());
        if (user == null) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, USER_NOT_FOUND_MSG);
        }
        return user;
    }
}