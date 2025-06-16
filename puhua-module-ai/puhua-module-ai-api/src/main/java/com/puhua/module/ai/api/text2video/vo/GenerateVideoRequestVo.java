package com.puhua.module.ai.api.text2video.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerateVideoRequestVo {

    private String model;

    private String prompt;

    private int seed;

    private int width;

    private int height;

    @JSONField(name = "num_frames")
    @JsonProperty("num_frames")
    private int numFrames;

}