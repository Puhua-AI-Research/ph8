package com.puhua.module.ai.controller.app.modelRepository;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.ai.controller.app.modelRepository.vo.ModelRepositoryListReqVO;
import com.puhua.module.ai.controller.app.modelRepository.vo.ModelRepositoryListRespVO;
import com.puhua.module.ai.controller.app.modelRepository.vo.ModelRepositoryRespVO;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.service.apitags.ApiTagsService;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "APP - 模型库")
@RestController
@RequestMapping("/ai/modelRepository")
@Validated
public class AppModelRepositoryController {

    @Resource
    private ModelRepositoryService modelRepositoryService;

    @Resource
    private ApiTagsService apiTagsService;

    @PostMapping("/list")
    @Operation(summary = "获得模型列表")
    @PermitAll
    public CommonResult<List<ModelRepositoryListRespVO>> getModelRepositoryPage(@RequestBody ModelRepositoryListReqVO listReqVO) {
        List<ModelRepositoryDO> list = modelRepositoryService.list(listReqVO);
        return success(BeanUtils.toBean(list, ModelRepositoryListRespVO.class));
    }

    @PostMapping("/square")
    @Operation(summary = "获得模型列表")
    @PermitAll
    public CommonResult<List<ModelRepositoryRespVO>> square(@RequestBody ModelRepositoryListReqVO listReqVO) {
        List<ModelRepositoryDO> list = modelRepositoryService.square(listReqVO);
        List<ModelRepositoryRespVO> respVOS = BeanUtils.toBean(list, ModelRepositoryRespVO.class);

        List<ApiTagsDO> chargeLabels = apiTagsService.getChargeLabelPriority();
        for (ModelRepositoryRespVO respVO : respVOS) {
            respVO.setLabelList(JSONArray.parseArray(respVO.getLabels(), String.class));
            respVO.setImageList(JSONArray.parseArray(respVO.getImages(), String.class));
            respVO.setSupportChipList(JSONArray.parseArray(respVO.getSupportChips(), String.class));
            respVO.setTag(getLabel(chargeLabels, respVO.getTags()));
            respVO.setInferencePriceStr(respVO.getInferencePrice() == 0 ? "限免" : respVO.getInferencePrice() + getUnitWord(respVO.getType()));
            respVO.setOriginalPriceStr(respVO.getOriginalPrice() + getUnitWord(respVO.getType()));
        }
        return success(respVOS);
    }

    private String getUnitWord(String modelType) {
        return switch (modelType) {
            case "llm", "vllm", "embedding", "rerank" -> "积分/千tokens";
            case "text2img" -> "积分/次";
            case "text2video" -> "积分/tokens";
            default -> "";
        };
    }

    private String getLabel(List<ApiTagsDO> chargeLabels, String tags) {
        List<String> split = StrUtil.split(tags, "|");
        for (ApiTagsDO chargeLabel : chargeLabels) {
            for (String tag : split) {
                if (chargeLabel.getType().equals(tag)) {
                    return tag;
                }
            }
        }
        return "";
    }

}