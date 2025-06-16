package com.puhua.module.ai.controller.app.modelExperience;

import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import com.puhua.module.ai.service.openapi.ChatService;
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
@Tag(name = "APP - chat")
@RestController
@RequestMapping("/ai/chat")
@Validated
public class ChatController {

    @Resource
    ChatService chatService;

    @Operation(summary = "体验对话-流式")
    @PostMapping(value = "completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PermitAll
    public SseEmitter chat(@RequestBody ChatCompletionRequest chatCompletionRequest) throws IOException {

        Long loginUserId = getLoginUserId();
        chatCompletionRequest.setStream(true);
        Double frequencyPenalty = chatCompletionRequest.getFrequencyPenalty();
        if (frequencyPenalty != null) {
            if (frequencyPenalty < 0.0 || frequencyPenalty > 2.0) {
                // 给默认值
                chatCompletionRequest.setFrequencyPenalty(1.0);
            }
        }
        Double presencePenalty = chatCompletionRequest.getPresencePenalty();

        if (presencePenalty != null) {
            if (presencePenalty < 0.0 || presencePenalty > 2.0) {
                // 给默认值
                chatCompletionRequest.setPresencePenalty(1.0);
            }
        }
        return chatService.webStream(loginUserId, chatCompletionRequest);
    }

}
