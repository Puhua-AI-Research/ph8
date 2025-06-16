package com.puhua.module.ai.service.modelRepository;

import com.puhua.module.ai.controller.app.modelRepository.vo.ModelRepositoryListReqVO;
import com.puhua.module.ai.model.chat.ChatModel;
import com.puhua.module.ai.model.image.ImageModel;
import com.puhua.module.ai.model.video.VideoModel;
import com.puhua.module.ai.model.vllm.VllmModel;
import jakarta.validation.*;
import com.puhua.module.ai.controller.admin.modelrepository.vo.*;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.framework.common.pojo.PageResult;

import java.util.List;

/**
 * 模型库 Service 接口
 *
 * @author 中航普华
 */
public interface ModelRepositoryService {

    /**
     * 创建模型库
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createModelRepository(@Valid ModelRepositorySaveReqVO createReqVO);

    /**
     * 更新模型库
     *
     * @param updateReqVO 更新信息
     */
    void updateModelRepository(@Valid ModelRepositorySaveReqVO updateReqVO);

    /**
     * 删除模型库
     *
     * @param id 编号
     */
    void deleteModelRepository(Long id);

    /**
     * 获得模型库
     *
     * @param id 编号
     * @return 模型库
     */
    ModelRepositoryDO getModelRepository(Long id);

    /**
     * 获得模型库分页
     *
     * @param pageReqVO 分页查询
     * @return 模型库分页
     */
    PageResult<ModelRepositoryDO> getModelRepositoryPage(ModelRepositoryPageReqVO pageReqVO);

    /**
     * 获取chatModel
     *
     * @param name
     * @return
     */
    ChatModel getChatModel(String name);

    /**
     * 获取imageModel
     *
     * @param name
     * @return
     */
    ImageModel getImageModel(String name);

    /**
     * 获取video model
     *
     * @param name
     * @return
     */
    public VideoModel getVideoModel(String name);

    /**
     * 获取vllm model
     *
     * @param name
     * @return
     */
    public VllmModel getVllmModel(String name);

    /**
     * 获取模型列表
     *
     * @param listReqVO
     * @return
     */
    List<ModelRepositoryDO> list(ModelRepositoryListReqVO listReqVO);

    /**
     * 获取模型广场模型列表
     *
     * @param listReqVO
     * @return
     */
    List<ModelRepositoryDO> square(ModelRepositoryListReqVO listReqVO);

    /**
     * 根据名称获取模型
     *
     * @param name
     * @return
     */
    public ModelRepositoryDO getModelRepositoryByName(String name);

    List<ModelRepositoryDO> listByTypes(List<String> types);
}