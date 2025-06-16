package com.puhua.module.ai.controller.app.apiKey.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "C端 - ApiKey管理新增/修改 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppApiKeyCreateRespVO {
    private String apiKey;
}