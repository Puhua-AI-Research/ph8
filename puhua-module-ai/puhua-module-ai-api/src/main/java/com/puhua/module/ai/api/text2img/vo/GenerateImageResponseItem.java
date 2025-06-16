package com.puhua.module.ai.api.text2img.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateImageResponseItem {
    private int index;
    private String url;
}