package com.puhua.module.infra.controller.admin.demo.demo01;

import com.puhua.framework.apilog.core.annotation.ApiAccessLog;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.excel.core.util.ExcelUtils;
import com.puhua.module.infra.controller.admin.demo.demo01.vo.Demo01ContactPageReqVO;
import com.puhua.module.infra.controller.admin.demo.demo01.vo.Demo01ContactRespVO;
import com.puhua.module.infra.controller.admin.demo.demo01.vo.Demo01ContactSaveReqVO;
import com.puhua.module.infra.dal.dataobject.demo.demo01.Demo01ContactDO;
import com.puhua.module.infra.service.demo.demo01.Demo01ContactService;
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

@Tag(name = "管理后台 - 示例联系人")
@RestController
@RequestMapping("/infra/demo01-contact")
@Validated
public class Demo01ContactController {

    @Resource
    private Demo01ContactService demo01ContactService;

    @PostMapping("/create")
    @Operation(summary = "创建示例联系人")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:create')")
    public CommonResult<Long> createDemo01Contact(@Valid @RequestBody Demo01ContactSaveReqVO createReqVO) {
        return success(demo01ContactService.createDemo01Contact(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新示例联系人")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:update')")
    public CommonResult<Boolean> updateDemo01Contact(@Valid @RequestBody Demo01ContactSaveReqVO updateReqVO) {
        demo01ContactService.updateDemo01Contact(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除示例联系人")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:delete')")
    public CommonResult<Boolean> deleteDemo01Contact(@RequestParam("id") Long id) {
        demo01ContactService.deleteDemo01Contact(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得示例联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:query')")
    public CommonResult<Demo01ContactRespVO> getDemo01Contact(@RequestParam("id") Long id) {
        Demo01ContactDO demo01Contact = demo01ContactService.getDemo01Contact(id);
        return success(BeanUtils.toBean(demo01Contact, Demo01ContactRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得示例联系人分页")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:query')")
    public CommonResult<PageResult<Demo01ContactRespVO>> getDemo01ContactPage(@Valid Demo01ContactPageReqVO pageReqVO) {
        PageResult<Demo01ContactDO> pageResult = demo01ContactService.getDemo01ContactPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, Demo01ContactRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出示例联系人 Excel")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDemo01ContactExcel(@Valid Demo01ContactPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<Demo01ContactDO> list = demo01ContactService.getDemo01ContactPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "示例联系人.xls", "数据", Demo01ContactRespVO.class,
                        BeanUtils.toBean(list, Demo01ContactRespVO.class));
    }

}