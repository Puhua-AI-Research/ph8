package com.puhua.module.member.controller.app.memberBalancelog.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.puhua.framework.excel.core.annotations.DictFormat;
import com.puhua.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "C端 - 用户额度流水 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppBalanceLogRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5391")
    @ExcelProperty("id")
    private Long id;


    @Schema(description = "变动时间")
    @ExcelProperty("变动时间")
    private LocalDateTime logTime;

    @Schema(description = "交易类型", example = "1")
    @ExcelProperty(value = "交易类型", converter = DictConvert.class)
    @DictFormat("change_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String changeType;

    @Schema(description = "变动金额")
    @ExcelProperty("变动金额")
    private Long changeBalance;

    @Schema(description = "业务方式")
    @ExcelProperty(value = "业务方式", converter = DictConvert.class)
    @DictFormat("business_mode") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String businessMode;

}