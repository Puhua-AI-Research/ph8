package com.puhua.module.ai.controller.app.apitags;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.ai.controller.app.apitags.vo.AppApiTagsRespVO;
import com.puhua.module.ai.dal.dataobject.apitags.ApiTagsDO;
import com.puhua.module.ai.service.apitags.ApiTagsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.compress.utils.Lists;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.puhua.framework.common.pojo.CommonResult.success;

@Tag(name = "C端 - 模型tag")
@RestController
@RequestMapping("/ai/api-tags")
@Validated
public class AppApiTagsController {

    @Resource
    private ApiTagsService apiTagsService;

    @GetMapping("/list")
    @Operation(summary = "获得模型tag列表")
    @PermitAll
    public CommonResult<List<AppApiTagsRespVO>> getApiTagsList() {
        List<ApiTagsDO> list = apiTagsService.getApiTagsList();
        return success(BeanUtils.toBean(list, AppApiTagsRespVO.class));
    }

    @GetMapping("/newList")
    @Operation(summary = "获得模型tag列表")
    @PermitAll
    public CommonResult<List<Map<String, Object>>> newList() {
        List<ApiTagsDO> list = apiTagsService.getApiTagsList();
        // 使用 Streams API 按 group 分组
        Map<String, List<ApiTagsDO>> group = list.stream()
                .collect(Collectors.groupingBy(ApiTagsDO::getGroupName));

        List<Map<String, Object>> res = Lists.newArrayList();
        for (String k : group.keySet()) {
            List<ApiTagsDO> apiTagsDOS = group.get(k);
            List<AppApiTagsRespVO> bean = BeanUtils.toBean(apiTagsDOS, AppApiTagsRespVO.class);
            Map<String, Object> item = new HashMap<>();

            item.put("name", k);
            item.put("list", bean);
            res.add(item);
        }
        return success(res);
    }

}