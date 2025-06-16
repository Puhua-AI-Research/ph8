package com.puhua.module.ai.controller.admin.modelrepository.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.puhua.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 模型库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelRepositoryPageReqVO extends PageParam {

    @Schema(description = "模型名称", example = "李四")
    private String name;

    @Schema(description = "模型分类", example = "1")
    private String type;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "模型中文名称", example = "赵六")
    private String modelName;

    @Schema(description = "计费方式")
    private String chargeMode;

    @Schema(description = "厂商")
    private String manufacturers;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}