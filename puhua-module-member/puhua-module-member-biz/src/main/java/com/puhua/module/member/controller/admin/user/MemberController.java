package com.puhua.module.member.controller.admin.user;

import com.puhua.framework.apilog.core.annotation.ApiAccessLog;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.excel.core.util.ExcelUtils;
import com.puhua.module.member.controller.admin.user.vo.UserPageReqVO;
import com.puhua.module.member.controller.admin.user.vo.UserRespVO;
import com.puhua.module.member.controller.admin.user.vo.UserSaveReqVO;
import com.puhua.module.member.dal.dataobject.user.MemberMapperDO;
import com.puhua.module.member.service.user.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.puhua.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - C端用户")
@RestController
@RequestMapping("/member/user")
@Validated
public class MemberController {

    @Resource
    private MemberService userService;

    @PostMapping("/create")
    @Operation(summary = "创建C端用户")
    @PreAuthorize("@ss.hasPermission('web:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserSaveReqVO createReqVO) {
        return success(userService.createUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新C端用户")
    @PreAuthorize("@ss.hasPermission('web:user:update')")
    public CommonResult
            <Boolean> updateUser(@Valid @RequestBody UserSaveReqVO updateReqVO) {
        userService.updateUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除C端用户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('web:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得C端用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('web:user:query')")
    public CommonResult
            <UserRespVO> getUser
            (@RequestParam("id") Long id) {
        MemberMapperDO user = userService.getUser(id);
        return success(BeanUtils.toBean(user, UserRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得C端用户分页")
    @PreAuthorize("@ss.hasPermission('web:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        PageResult
                <MemberMapperDO> pageResult = userService.getUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出C端用户 Excel")
    @PreAuthorize("@ss.hasPermission('web:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserExcel(@Valid UserPageReqVO
                                        pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List
                <MemberMapperDO> list = userService.getUserPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "C端用户.xls",
                "数据", UserRespVO.class,
                BeanUtils.toBean(list, UserRespVO.class));
    }

}