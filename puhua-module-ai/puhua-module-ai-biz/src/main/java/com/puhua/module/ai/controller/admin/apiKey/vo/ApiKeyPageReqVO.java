package com.puhua.module.ai.controller.admin.apiKey.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.puhua.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ApiKey管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiKeyPageReqVO extends PageParam {

    @Schema(description = "用户id", example = "1986")
    private Long memberUserId;

    @Schema(description = "key")
    private String apiKey;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "名称", example = "sd1.5")
    private String name;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}