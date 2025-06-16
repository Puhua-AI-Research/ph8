package com.puhua.module.ai.controller.app.modelExperience;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;
import com.puhua.module.ai.service.openapi.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * @Author ZhangYi
 * @Date 2024年12月12日 15:29
 * @Description:
 */
@Tag(name = "APP - Image")
@RestController
@RequestMapping("/ai/image")
@Validated
public class ImageController {

    @Resource
    ImageService imageService;

    @Operation(summary = "体验对话-文生图")
    @PostMapping(value = "generate")
    @PermitAll
    public CommonResult<GenerateImageResponseVo> generate(@RequestBody GenerateImageRequestVo imageRequestVo) throws IOException {
        return CommonResult.success(imageService.generateImage(imageRequestVo));
    }

}
