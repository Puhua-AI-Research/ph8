package com.puhua.module.mp.controller.admin.menu;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.mp.controller.admin.menu.vo.MpMenuRespVO;
import com.puhua.module.mp.controller.admin.menu.vo.MpMenuSaveReqVO;
import com.puhua.module.mp.convert.menu.MpMenuConvert;
import com.puhua.module.mp.dal.dataobject.menu.MpMenuDO;
import com.puhua.module.mp.service.menu.MpMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 公众号菜单")
@RestController
@RequestMapping("/mp/menu")
@Validated
public class MpMenuController {

    @Resource
    private MpMenuService mpMenuService;

    @PostMapping("/save")
    @Operation(summary = "保存公众号菜单")
    @PreAuthorize("@ss.hasPermission('mp:menu:save')")
    public CommonResult<Boolean> saveMenu(@Valid @RequestBody MpMenuSaveReqVO createReqVO) {
        mpMenuService.saveMenu(createReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公众号菜单")
    @Parameter(name = "accountId", description = "公众号账号的编号", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('mp:menu:delete')")
    public CommonResult<Boolean> deleteMenu(@RequestParam("accountId") Long accountId) {
        mpMenuService.deleteMenuByAccountId(accountId);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得公众号菜单列表")
    @Parameter(name = "accountId", description = "公众号账号的编号", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('mp:menu:query')")
    public CommonResult<List<MpMenuRespVO>> getMenuList(@RequestParam("accountId") Long accountId) {
        List<MpMenuDO> list = mpMenuService.getMenuListByAccountId(accountId);
        return success(MpMenuConvert.INSTANCE.convertList(list));
    }

}
