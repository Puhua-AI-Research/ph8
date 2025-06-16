package com.puhua.module.ai.controller.app.openapi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor  // 添加全参构造函数
public class Model {
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String objectType;

    @JsonProperty("created")
    private Long created;

    @JsonProperty("owned_by")
    private String ownedBy;

}