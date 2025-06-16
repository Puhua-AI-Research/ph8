package com.puhua.module.ai.api.text2img.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateImageRequestVo {
    private String model;

    private Integer seed;

    private Integer steps;

    @JSONField(name = "batch_size")
    @JsonProperty("batch_size")
    private Integer batchSize;


    private Integer width;

    private Integer height;

    private String prompt;


    private String sampler_name;

    private Integer cfg;

    @JSONField(name = "denoising_strength")
    @JsonProperty("denoising_strength")
    private Double denoisingStrength;

    private String scheduler;

    @JSONField(name = "image_url")
    @JsonProperty("image_url")
    private String imageUrl;

    private String negative_prompt;

}