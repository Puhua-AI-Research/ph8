package com.puhua.module.ai.controller.app.modelRepository.vo;

import com.puhua.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "APP - 模型库列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelRepositoryListReqVO extends PageParam {

    @Schema(description = "模型分类", example = "llm")
    private String type;

    @Schema(description = "模型tags", example = "free")
    private String tags;

}