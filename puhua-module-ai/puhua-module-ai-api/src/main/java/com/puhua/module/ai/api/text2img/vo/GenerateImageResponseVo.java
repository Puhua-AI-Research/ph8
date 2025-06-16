package com.puhua.module.ai.api.text2img.vo;

import lombok.Data;

import java.util.List;

@Data
public class GenerateImageResponseVo {
    private List<GenerateImageResponseItem> data;
}