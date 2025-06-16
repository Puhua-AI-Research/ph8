package com.puhua.module.ai.controller.app.apiKey;

import cn.hutool.core.util.IdUtil;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.security.core.util.SecurityFrameworkUtils;
import com.puhua.module.ai.controller.admin.apiKey.vo.ApiKeySaveReqVO;
import com.puhua.module.ai.controller.app.apiKey.vo.AppApiKeyCreateReqVO;
import com.puhua.module.ai.controller.app.apiKey.vo.AppApiKeyCreateRespVO;
import com.puhua.module.ai.controller.app.apiKey.vo.AppApiKeyRespVO;
import com.puhua.module.ai.controller.app.apiKey.vo.AppApiKeySaveReqVO;
import com.puhua.module.ai.dal.dataobject.apiKey.ApiKeyDO;
import com.puhua.module.ai.enums.ErrorCodeConstants;
import com.puhua.module.ai.service.apiKey.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.puhua.framework.common.pojo.CommonResult.*;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "APP - ApiKey管理")
@RestController
@RequestMapping("/ai/apiKey")
@Validated
public class AppApiKeyController {

    @Resource
    private ApiKeyService apiKeyService;

    @GetMapping("/list")
    @Operation(summary = "获取用户的apiKey")
    public CommonResult<HashMap<String, Object>> getApiKeyPage() {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        List<ApiKeyDO> list = apiKeyService.getApiKeyByUserId(loginUserId);
        return successMap("list", BeanUtils.toBean(list, AppApiKeyRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建ApiKey")
    public CommonResult<AppApiKeyCreateRespVO> createApiKey(@Valid @RequestBody AppApiKeyCreateReqVO createReqVO) {
        ApiKeySaveReqVO saveReqVO = ApiKeySaveReqVO
                .builder()
                .name(createReqVO.getName())
                .apiKey("sk-" + IdUtil.fastSimpleUUID())
                .memberUserId(getLoginUserId())
                .status(CommonStatusEnum.ENABLE.getStatus())
                .build();
        apiKeyService.createApiKey(saveReqVO);
        AppApiKeyCreateRespVO createRespVO = AppApiKeyCreateRespVO
                .builder()
                .apiKey(saveReqVO.getApiKey())
                .build();
        return success(createRespVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新ApiKey信息")
    public CommonResult<Boolean> updateApiKey(@Valid @RequestBody AppApiKeySaveReqVO updateReqVO) {
        ApiKeyDO apiKey = apiKeyService.getApiKey(updateReqVO.getId());
        if (apiKey == null) {
            return error(ErrorCodeConstants.API_KEY_NOT_EXISTS);
        }

        if (apiKey.getMemberUserId() == null || !apiKey.getMemberUserId().equals(getLoginUserId())) {
            return error(ErrorCodeConstants.API_KEY_NOT_EXISTS);
        }

        apiKey.setName(updateReqVO.getName());
        apiKey.setStatus(updateReqVO.getStatus());
        apiKeyService.updateAppApiKey(apiKey);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除ApiKey")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteApiKey(@RequestParam("id") Long id) {
        ApiKeyDO apiKey = apiKeyService.getApiKey(id);
        if (apiKey == null) {
            return error(ErrorCodeConstants.API_KEY_NOT_EXISTS);
        }

        if (apiKey.getMemberUserId() == null || !apiKey.getMemberUserId().equals(getLoginUserId())) {
            return error(ErrorCodeConstants.API_KEY_NOT_EXISTS);
        }
        apiKeyService.deleteApiKey(id);
        return success(true);
    }

}