package com.puhua.module.infra.service.file;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.io.FileUtils;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import com.puhua.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import com.puhua.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import com.puhua.module.infra.dal.dataobject.file.FileDO;
import com.puhua.module.infra.dal.mysql.file.FileMapper;
import com.puhua.module.infra.framework.file.core.client.FileClient;
import com.puhua.module.infra.framework.file.core.client.s3.FilePresignedUrlRespDTO;
import com.puhua.module.infra.framework.file.core.utils.FileTypeUtils;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;

/**
 * 文件 Service 实现类
 *
 * @author 中航普华
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private FileMapper fileMapper;

    @Override
    public PageResult<FileDO> getFilePage(FilePageReqVO pageReqVO) {
        return fileMapper.selectPage(pageReqVO);
    }

    @Override
    @SneakyThrows
    public String createFile(String name, String path, byte[] content) {
        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtils.generatePath(content, name);
        }
        // 如果 name 为空，则使用 path 填充
        if (StrUtil.isEmpty(name)) {
            name = path;
        }

        // 上传到文件存储器
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url = client.upload(content, path, type);

        // 保存到数据库
        FileDO file = new FileDO();
        file.setConfigId(client.getId());
        file.setName(name);
        file.setPath(path);
        file.setUrl(url);
        file.setType(type);
        file.setSize(content.length);
        fileMapper.insert(file);
        return url;
    }

    @Override
    public Long createFile(FileCreateReqVO createReqVO) {
        FileDO file = BeanUtils.toBean(createReqVO, FileDO.class);
        fileMapper.insert(file);
        return file.getId();
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // 校验存在
        FileDO file = validateFileExists(id);

        // 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        client.delete(file.getPath());

        // 删除记录
        fileMapper.deleteById(id);
    }

    private FileDO validateFileExists(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (fileDO == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        return client.getContent(path);
    }

    @Override
    public FilePresignedUrlRespVO getFilePresignedUrl(String path) throws Exception {
        FileClient fileClient = fileConfigService.getMasterFileClient();
        FilePresignedUrlRespDTO presignedObjectUrl = fileClient.getPresignedObjectUrl(path);
        return BeanUtils.toBean(presignedObjectUrl, FilePresignedUrlRespVO.class,
                object -> object.setConfigId(fileClient.getId()));
    }

}
