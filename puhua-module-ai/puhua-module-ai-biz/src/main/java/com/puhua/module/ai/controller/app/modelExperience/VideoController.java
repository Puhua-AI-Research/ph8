package com.puhua.module.ai.controller.app.modelExperience;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import com.puhua.module.ai.api.text2video.vo.GenerateVideoResponseVo;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.model.video.VideoModel;
import com.puhua.module.ai.service.modelRepository.ModelRepositoryService;
import com.puhua.module.ai.service.openapi.VideoService;
import com.puhua.module.member.api.member.MemberApi;
import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.puhua.module.ai.enums.ErrorCodeConstants.MODEL_REPOSITORY_NOT_EXISTS;
import static com.puhua.module.ai.enums.ModelApiConstants.PH_MODEL_MANUFACTURERS_NAME;

/**
 * @Author ZhangYi
 * @Date 2024年12月12日 15:29
 * @Description:
 */
@Tag(name = "APP - Video")
@RestController
@RequestMapping("/ai/video")
@Validated
public class VideoController {

    @Resource
    ModelRepositoryService modelRepositoryService;

    @Resource
    RateLimiterRedisDAO rateLimiterRedisDAO;


    @Resource
    VideoService videoService;

    @Resource
    MemberApi memberApi;

    @Operation(summary = "体验对话-文生视频")
    @PostMapping(value = "generate")
    @PermitAll
    public CommonResult<GenerateVideoResponseVo> generate(@RequestBody HashMap<String, Object> requestParams) throws IOException {
        if (!requestParams.containsKey("model")) {
            throw exception(MODEL_REPOSITORY_NOT_EXISTS);
        }
        String modelName = requestParams.get("model").toString();
        ModelRepositoryDO model = modelRepositoryService.getModelRepositoryByName(modelName);
        if (model == null || CommonStatusEnum.isDisable(model.getStatus())) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        if (model.getManufacturers().equals(PH_MODEL_MANUFACTURERS_NAME)) {
            throw new ServiceException(HttpStatus.HTTP_UNAVAILABLE, "Overloaded Model service overloaded. Please try again later.");
        }

        if (!rateLimiterRedisDAO.tryAcquire("model:rpm-" + modelName, model.getRpm(), 1, TimeUnit.MINUTES)) {
            throw new ServiceException(HttpStatus.HTTP_TOO_MANY_REQUESTS, "RateLimit Request was rejected due to rate limiting. ");
        }

        VideoModel videoModel = modelRepositoryService.getVideoModel(modelName);
        JSONObject jsonObject = videoModel.generateVideoSync(requestParams);
        return CommonResult.success(JSONObject.parseObject(jsonObject.toJSONString(), GenerateVideoResponseVo.class));
    }


    /**
     * 创建视频生成任务
     *
     * @param requestVo 请求
     * @return stream
     */
    @PermitAll
    @PostMapping("generations")
    public JSONObject generations(@RequestBody HashMap<String, Object> requestVo) throws IOException {
        // 获取用户
        MemberUserInfoRespVO memberUser = memberApi.getMemberUser(getLoginUserId());
        if (memberUser == null) {
            throw new ServiceException(HttpStatus.HTTP_NOT_FOUND, "User not found");
        }
        // 校验余额
        if (memberUser.getBalance() <= 0) {
            throw new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, "Insufficient balance");
        }

        return videoService.generate(memberUser.getId(), requestVo);
    }

    /**
     * 查询视频生成任务
     *
     * @param id 任务id
     * @return stream
     */
    @PermitAll
    @GetMapping("get")
    public JSONObject get(@RequestParam String id) throws IOException {
        return videoService.get(id);
    }


}
