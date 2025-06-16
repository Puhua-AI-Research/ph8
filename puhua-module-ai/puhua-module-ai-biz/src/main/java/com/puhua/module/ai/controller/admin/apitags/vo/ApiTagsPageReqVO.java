package com.puhua.module.ai.controller.admin.apitags.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.util.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.puhua.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.puhua.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 模型tag分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiTagsPageReqVO extends PageParam {

    @Schema(description = "名称", example = "中航普华")
    private String name;

    @Schema(description = "类型", example = "1")
    private String type;

    @Schema(description = "分组", example = "1")
    private String groupName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}