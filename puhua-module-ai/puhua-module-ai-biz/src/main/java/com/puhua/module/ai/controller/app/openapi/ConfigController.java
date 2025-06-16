package com.puhua.module.ai.controller.app.openapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.controller.app.openapi.vo.Model;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.enums.ApiConstants;
import com.puhua.module.ai.enums.ModelType;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.*;

/**
 * @Author ZhangYi
 * @Date 2025年03月28日 16:17
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class ConfigController {

    @Resource
    private ApiKeyService apiKeyService;

    @Resource
    private ModelRepositoryService modelRepositoryService;

    @Value("${puhua.models-host}")
    private String host;

    /**
     * 统一API Key验证方法
     */
    private void validateApiKey(String apiKey) {
        if (StrUtil.isBlank(apiKey)) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "API key cannot be empty");
        }

        String cleanKey = apiKey.replace("Bearer ", "").trim();
        if (cleanKey.isEmpty()) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Invalid API key format");
        }

        ApiKeyDO apiKeyDO = apiKeyService.getApiKey(cleanKey);
        if (apiKeyDO == null) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "Invalid API key");
        }

        if (CommonStatusEnum.isDisable(apiKeyDO.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAUTHORIZED, "API key is disabled");
        }
    }

    @PermitAll
    @GetMapping("get_continue_config")
    public JSONObject getConfig(@RequestHeader("Authorization") String apiKey,
                                @RequestParam String config_name) {
        validateApiKey(apiKey); // API Key验证

        String url = host + ApiConstants.CONFIG_API + "?config_name=" + config_name;
        try {
            String response = HttpUtil.get(url);
            if (StrUtil.isBlank(response)) {
                throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Empty response from configuration service");
            }
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            log.error("Failed to fetch configuration: {}", e.getMessage());
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Configuration service unavailable");
        }
    }

    @PermitAll
    @GetMapping("models")
    public Map<String, Object> getModels(@RequestHeader("Authorization") String apiKey) {
        validateApiKey(apiKey); // API Key验证

        // 获取远程模型配置
        String remoteResponse;
        try {
            remoteResponse = HttpUtil.get(host + ApiConstants.MODELS_API);
        } catch (Exception e) {
            log.error("Failed to fetch models: {}", e.getMessage());
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Model service unavailable");
        }

        if (StrUtil.isBlank(remoteResponse)) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Empty response from model service");
        }

        // 获取本地模型仓库数据
        List<String> modelTypes = Arrays.asList(
                ModelType.LLM.getType(),
                ModelType.TEXT2IMG.getType(),
                ModelType.EMBEDDING.getType(),
                ModelType.VLLM.getType(),
                ModelType.RERANK.getType()
        );

        List<ModelRepositoryDO> modelRepos = modelRepositoryService.listByTypes(modelTypes);
        List<Model> modelList = Lists.newArrayListWithExpectedSize(modelRepos.size());

        for (ModelRepositoryDO repo : modelRepos) {
            long createdSeconds = repo.getCreateTime().atZone(ZoneId.systemDefault()).toEpochSecond();
            modelList.add(Model.builder()
                    .id(repo.getName())
                    .ownedBy("system")
                    .created(createdSeconds)
                    .objectType("model")
                    .build());
        }

        return Map.of(
                "object", "list",
                "data", modelList
        );
    }

    @PermitAll
    @GetMapping("CodeConfig")
    public JSONObject getCodeConfig(@RequestHeader("Authorization") String apiKey) {
        validateApiKey(apiKey); // API Key验证

        try {
            String response = HttpUtil.get(host + ApiConstants.CODE_CONFIG_API);
            if (StrUtil.isBlank(response)) {
                throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Empty response from code configuration service");
            }
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            log.error("Failed to fetch code configuration: {}", e.getMessage());
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Code configuration service unavailable");
        }
    }
}