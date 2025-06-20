package com.puhua.module.pay.controller.admin.app;

import cn.hutool.core.collection.CollUtil;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.module.pay.controller.admin.app.vo.*;
import com.puhua.module.pay.convert.app.PayAppConvert;
import com.puhua.module.pay.dal.dataobject.app.PayAppDO;
import com.puhua.module.pay.dal.dataobject.channel.PayChannelDO;
import com.puhua.module.pay.service.app.PayAppService;
import com.puhua.module.pay.service.channel.PayChannelService;
import com.puhua.module.pay.controller.admin.app.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.common.util.collection.CollectionUtils.convertList;

@Slf4j
@Tag(name = "管理后台 - 支付应用信息")
@RestController
@RequestMapping("/pay/app")
@Validated
public class PayAppController {

    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;

    @PostMapping("/create")
    @Operation(summary = "创建支付应用信息")
    @PreAuthorize("@ss.hasPermission('pay:app:create')")
    public CommonResult<Long> createApp(@Valid @RequestBody PayAppCreateReqVO createReqVO) {
        return success(appService.createApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新支付应用信息")
    @PreAuthorize("@ss.hasPermission('pay:app:update')")
    public CommonResult<Boolean> updateApp(@Valid @RequestBody PayAppUpdateReqVO updateReqVO) {
        appService.updateApp(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新支付应用状态")
    @PreAuthorize("@ss.hasPermission('pay:app:update')")
    public CommonResult<Boolean> updateAppStatus(@Valid @RequestBody PayAppUpdateStatusReqVO updateReqVO) {
        appService.updateAppStatus(updateReqVO.getId(), updateReqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除支付应用信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pay:app:delete')")
    public CommonResult<Boolean> deleteApp(@RequestParam("id") Long id) {
        appService.deleteApp(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得支付应用信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:app:query')")
    public CommonResult<PayAppRespVO> getApp(@RequestParam("id") Long id) {
        PayAppDO app = appService.getApp(id);
        return success(PayAppConvert.INSTANCE.convert(app));
    }

    @GetMapping("/page")
    @Operation(summary = "获得支付应用信息分页")
    @PreAuthorize("@ss.hasPermission('pay:app:query')")
    public CommonResult<PageResult<PayAppPageItemRespVO>> getAppPage(@Valid PayAppPageReqVO pageVO) {
        // 得到应用分页列表
        PageResult<PayAppDO> pageResult = appService.getAppPage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }

        // 得到所有的应用编号，查出所有的渠道，并移除未启用的渠道
        List<PayChannelDO> channels = channelService.getChannelListByAppIds(
                convertList(pageResult.getList(), PayAppDO::getId));
        channels.removeIf(channel -> !CommonStatusEnum.ENABLE.getStatus().equals(channel.getStatus()));

        // 拼接后返回
        return success(PayAppConvert.INSTANCE.convertPage(pageResult, channels));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用列表")
    @PreAuthorize("@ss.hasPermission('pay:merchant:query')")
    public CommonResult<List<PayAppRespVO>> getAppList() {
        List<PayAppDO> appListDO = appService.getAppList();
        return success(PayAppConvert.INSTANCE.convertList(appListDO));
    }

}
