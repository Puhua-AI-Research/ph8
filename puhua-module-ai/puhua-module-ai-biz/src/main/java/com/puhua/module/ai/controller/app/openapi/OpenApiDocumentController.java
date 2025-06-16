package com.puhua.module.ai.controller.app.openapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;

/**
 * @Author ZhangYi
 * @Date 2025年03月06日 14:24
 * @Description:
 */
@RestController
@RequestMapping("/ai")
public class OpenApiDocumentController {

    @Resource
    private ApiKeyService apiKeyService;
    @Resource
    private MemberApi memberApi;
    @Resource
    private ModelRepositoryService modelRepositoryService;
    @Resource
    private RateLimiterRedisDAO rateLimiterRedisDAO;

    // 公共认证方法
    private MemberUserInfoRespVO authenticate(String apiKeyHeader) {
        if (StrUtil.isBlank(apiKeyHeader)) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Authorization header is required");
        }

        String apiKey = apiKeyHeader.replace("Bearer ", "").trim();
        ApiKeyDO apiKeyDO = apiKeyService.getApiKey(apiKey);

        if (apiKeyDO == null) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid API key");
        }
        if (CommonStatusEnum.isDisable(apiKeyDO.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "API key is disabled");
        }

        MemberUserInfoRespVO user = memberApi.getMemberUser(apiKeyDO.getMemberUserId());
        if (user == null) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "User not found");
        }
        return user;
    }

    // 模型检查与限流
    private ModelRepositoryDO checkModelAndRateLimit(JSONObject request, Long userId) {
        String modelName = request.getString("model");
        ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);

        if (model == null) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Model not available");
        }
        if (CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        // 根据厂商类型构建限流Key
        String rateLimitKey = model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)
                ? "model:rpm-" + model.getName()
                : "model:rpm-" + userId + model.getName();

        if (!rateLimiterRedisDAO.tryAcquire(rateLimitKey, model.getRpm(), 1, TimeUnit.MINUTES)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "Request was rejected due to rate limiting.");
        }
        return model;
    }

    // 统一请求执行方法
    private JSONObject executeModelRequest(ModelRepositoryDO model, JSONObject request) {
        String response = HttpRequest.post(model.getUrl())
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .body(request.toJSONString())
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }

    @PostMapping("/embeddings")
    @PermitAll
    public JSONObject embeddings(@RequestHeader("Authorization") String apiKey,
                                 @RequestBody JSONObject request) {
        MemberUserInfoRespVO user = authenticate(apiKey);
        ModelRepositoryDO model = checkModelAndRateLimit(request, user.getId());
        return executeModelRequest(model, request);
    }

    @PostMapping("/rerank")
    @PermitAll
    public JSONObject rerank(@RequestHeader("Authorization") String apiKey,
                             @RequestBody JSONObject request) {
        MemberUserInfoRespVO user = authenticate(apiKey);
        ModelRepositoryDO model = checkModelAndRateLimit(request, user.getId());
        return executeModelRequest(model, request);
    }
}