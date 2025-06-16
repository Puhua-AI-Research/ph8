package com.puhua.module.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ZhangYi
 * @Date 2025年06月07日 15:27
 * @Description:
 */

@Getter
@AllArgsConstructor
public enum ModelType {
    LLM("llm", "chat"),
    VLLM("vllm", "多模态"),
    TEXT2VIDEO("text2video", "text2video"),
    RERANK("rerank", "rerank"),
    EMBEDDING("embedding", "embedding"),
    TEXT2IMG("text2img", "text2img");

    private final String type;
    private final String name;

}
