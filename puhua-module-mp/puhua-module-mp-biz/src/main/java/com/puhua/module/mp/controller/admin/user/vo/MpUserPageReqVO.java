package com.puhua.module.mp.controller.admin.user.vo;

import com.puhua.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 公众号粉丝分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MpUserPageReqVO extends PageParam {

    @Schema(description = "公众号账号的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "公众号账号的编号不能为空")
    private Long accountId;

    @Schema(description = "公众号粉丝标识，模糊匹配", example = "o6_bmjrPTlm6_2sgVt7hMZOPfL2M")
    private String openid;

    @Schema(description = "微信生态唯一标识，模糊匹配", example = "o6_bmjrPTlm6_2sgVt7hMZOPfL2M")
    private String unionId;

    @Schema(description = "公众号粉丝昵称，模糊匹配", example = "中航普华")
    private String nickname;

}
