package com.puhua.module.infra.api.file;

import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.infra.api.file.dto.FileCreateReqDTO;
import com.puhua.module.infra.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@FeignClient(name = ApiConstants.NAME) // TODO 中航普华：fallbackFactory =
@Tag(name = "RPC 服务 - 文件")
public interface FileApi {

    String PREFIX = ApiConstants.PREFIX + "/file";

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(byte[] content) {
        return createFile(null, null, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param path 文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(String path, byte[] content) {
        return createFile(null, path, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param name 原文件名称
     * @param path 文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(@RequestParam("name") String name,
                              @RequestParam("path") String path,
                              @RequestParam("content") byte[] content) {
        return createFile(new FileCreateReqDTO().setName(name).setPath(path).setContent(content)).getCheckedData();
    }

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "保存文件，并返回文件的访问路径")
    CommonResult<String> createFile(@Valid @RequestBody FileCreateReqDTO createReqDTO);

}
