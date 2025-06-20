package com.puhua.module.system.api.mail;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import com.puhua.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME) // TODO 中航普华：fallbackFactory =
@Tag(name = "RPC 服务 - 邮件发送")
public interface MailSendApi {

    String PREFIX = ApiConstants.PREFIX + "/mail/send";

    @PostMapping(PREFIX + "/send-single-admin")
    @Operation(summary = "发送单条邮件给 Admin 用户", description = "在 mail 为空时，使用 userId 加载对应 Admin 的邮箱")
    CommonResult<Long> sendSingleMailToAdmin(@Valid @RequestBody MailSendSingleToUserReqDTO reqDTO);

    @PostMapping(PREFIX + "/send-single-member")
    @Operation(summary = "发送单条邮件给 Member 用户", description = "在 mail 为空时，使用 userId 加载对应 Member 的邮箱")
    CommonResult<Long> sendSingleMailToMember(@Valid @RequestBody MailSendSingleToUserReqDTO reqDTO);

}
