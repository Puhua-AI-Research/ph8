package com.puhua.module.ai.api.task.dto;

import com.puhua.framework.common.pojo.PageParam;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPageReqDto extends PageParam {
    /**
     * 时间范围
     */
    private LocalDateTime[] callTime;
}