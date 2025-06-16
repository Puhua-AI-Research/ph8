package com.puhua.module.ai.api.llm.vo.completion.chat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
public class ChatFunctionProperty {
    @NonNull
    @JsonIgnore
    private String name;
    @NonNull
    private String type;
    @JsonIgnore
    private Boolean required;
    private String description;
    private ChatFunctionProperty items;
    @JSONField(name = "enum")
    @JsonProperty("enum")
    private Set<?> enumValues;
}