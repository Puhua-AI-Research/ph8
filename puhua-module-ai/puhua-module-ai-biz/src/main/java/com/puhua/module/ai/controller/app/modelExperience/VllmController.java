package com.puhua.module.ai.controller.app.modelExperience;

import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import com.puhua.module.ai.service.openapi.VllmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * @Author ZhangYi
 * @Date 2024年12月12日 15:29
 * @Description:
 */
@Tag(name = "APP - VLLM")
@RestController
@RequestMapping("/ai/vllm")
@Validated
public class VllmController {

    @Resource
    VllmService vllmService;


    @Operation(summary = "体验对话-vllm")
    @PostMapping(value = "completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PermitAll
    public SseEmitter completion(@RequestBody VllmRequestVo vllmRequestVo) throws IOException {
        vllmRequestVo.setStream(true);
        return vllmService.webStream(getLoginUserId(), vllmRequestVo);
    }

}
