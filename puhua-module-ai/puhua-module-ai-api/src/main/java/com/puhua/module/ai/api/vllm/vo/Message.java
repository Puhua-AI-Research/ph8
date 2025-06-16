package com.puhua.module.ai.api.vllm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {


    private String role;

    private List<Content> content;
}
