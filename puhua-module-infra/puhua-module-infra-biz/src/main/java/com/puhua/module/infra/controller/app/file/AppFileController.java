package com.puhua.module.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.puhua.framework.common.pojo.CommonResult;
import com.puhua.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import com.puhua.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import com.puhua.module.infra.controller.app.file.vo.AppFileUploadReqVO;
import com.puhua.module.infra.controller.app.file.vo.FilePresigneReqVo;
import com.puhua.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.puhua.framework.common.pojo.CommonResult.success;
import static com.puhua.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @PermitAll
    public CommonResult<String> uploadFile(AppFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        String path = uploadReqVO.getPath();
        return success(fileService.createFile(file.getOriginalFilename(), path, IoUtil.readBytes(file.getInputStream())));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @PermitAll
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestParam("path") String path) throws Exception {
        return success(fileService.getFilePresignedUrl(path));
    }

    @PostMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestBody FilePresigneReqVo reqVo) throws Exception {
        String path = "";
        Long loginUserId = getLoginUserId();
        path = switch (reqVo.getScene()) {
            case "avatar" -> "user/" + loginUserId + "/avatar/";
            case "experience" -> "/model/" + loginUserId + "/experience/";
            default -> "/user/" + loginUserId + "/files/";
        };
        String filePath = path + IdUtil.getSnowflakeNextIdStr() + getFileExtension(reqVo.getFileName());
        return success(fileService.getFilePresignedUrl(filePath));
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return ""; // No extension or extension is just a dot
        }
        return fileName.substring(dotIndex);
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

}
