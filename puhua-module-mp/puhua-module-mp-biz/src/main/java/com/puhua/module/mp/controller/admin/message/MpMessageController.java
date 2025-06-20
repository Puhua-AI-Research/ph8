package com.puhua.module.mp.controller.admin.message;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.mp.controller.admin.message.vo.message.MpMessagePageReqVO;
import com.puhua.module.mp.controller.admin.message.vo.message.MpMessageRespVO;
import com.puhua.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import com.puhua.module.mp.convert.message.MpMessageConvert;
import com.puhua.module.mp.dal.dataobject.message.MpMessageDO;
import com.puhua.module.mp.service.message.MpMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 公众号消息")
@RestController
@RequestMapping("/mp/message")
@Validated
public class MpMessageController {

    @Resource
    private MpMessageService mpMessageService;

    @GetMapping("/page")
    @Operation(summary = "获得公众号消息分页")
    @PreAuthorize("@ss.hasPermission('mp:message:query')")
    public CommonResult<PageResult<MpMessageRespVO>> getMessagePage(@Valid MpMessagePageReqVO pageVO) {
        PageResult<MpMessageDO> pageResult = mpMessageService.getMessagePage(pageVO);
        return success(MpMessageConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/send")
    @Operation(summary = "给粉丝发送消息")
    @PreAuthorize("@ss.hasPermission('mp:message:send')")
    public CommonResult<MpMessageRespVO> sendMessage(@Valid @RequestBody MpMessageSendReqVO reqVO) {
        MpMessageDO message = mpMessageService.sendKefuMessage(reqVO);
        return success(MpMessageConvert.INSTANCE.convert(message));
    }

}
