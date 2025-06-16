package com.puhua.module.ai.controller.app.task.vo;

import com.puhua.module.ai.controller.admin.task.vo.TaskPageReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 用户任务计费分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppTaskPageReqVO extends TaskPageReqVO {

}