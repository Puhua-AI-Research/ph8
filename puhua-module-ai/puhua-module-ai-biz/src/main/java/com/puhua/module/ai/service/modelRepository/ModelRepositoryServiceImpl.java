package com.puhua.module.ai.service.modelRepository;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.util.object.BeanUtils;
import com.puhua.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.puhua.module.ai.controller.admin.modelrepository.vo.ModelRepositoryPageReqVO;
import com.puhua.module.ai.controller.admin.modelrepository.vo.ModelRepositorySaveReqVO;
import com.puhua.module.ai.controller.app.modelRepository.vo.ModelRepositoryListReqVO;
import com.puhua.module.ai.dal.dataobject.apithirdkey.ApiThirdKeyDO;
import com.puhua.module.ai.dal.dataobject.modelRepository.ModelRepositoryDO;
import com.puhua.module.ai.dal.mysql.modelRepository.ModelRepositoryMapper;
import com.puhua.module.ai.enums.ModelApiConstants;
import com.puhua.module.ai.enums.ModelType;
import com.puhua.module.ai.model.chat.ChatModel;
import com.puhua.module.ai.model.chat.OpenAIChatModel;
import com.puhua.module.ai.model.chat.VolcengineChatModel;
import com.puhua.module.ai.model.image.ImageModel;
import com.puhua.module.ai.model.image.SDImageModel;
import com.puhua.module.ai.model.image.VolcengineImageModel;
import com.puhua.module.ai.model.video.CogVideoModel;
import com.puhua.module.ai.model.video.VideoModel;
import com.puhua.module.ai.model.video.VolcengineVideoModel;
import com.puhua.module.ai.model.vllm.GlmVllmModel;
import com.puhua.module.ai.model.vllm.VllmModel;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.ai.enums.ErrorCodeConstants.MODEL_REPOSITORY_NOT_EXISTS;
import static com.puhua.module.ai.enums.ModelApiConstants.*;

/**
 * 模型库 Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class ModelRepositoryServiceImpl implements ModelRepositoryService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ModelRepositoryMapper modelRepositoryMapper;

    @Override
    public Long createModelRepository(ModelRepositorySaveReqVO createReqVO) {
        // 插入
        ModelRepositoryDO modelRepository = BeanUtils.toBean(createReqVO, ModelRepositoryDO.class);
        modelRepositoryMapper.insert(modelRepository);
        // 返回
        return modelRepository.getId();
    }

    @Override
    public void updateModelRepository(ModelRepositorySaveReqVO updateReqVO) {
        // 校验存在
        validateModelRepositoryExists(updateReqVO.getId());
        // 更新
        ModelRepositoryDO updateObj = BeanUtils.toBean(updateReqVO, ModelRepositoryDO.class);
        modelRepositoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteModelRepository(Long id) {
        // 校验存在
        validateModelRepositoryExists(id);
        // 删除
        modelRepositoryMapper.deleteById(id);
    }

    private void validateModelRepositoryExists(Long id) {
        if (modelRepositoryMapper.selectById(id) == null) {
            throw exception(MODEL_REPOSITORY_NOT_EXISTS);
        }
    }

    @Override
    public ModelRepositoryDO getModelRepository(Long id) {
        return modelRepositoryMapper.selectById(id);
    }

    @Override
    public PageResult<ModelRepositoryDO> getModelRepositoryPage(ModelRepositoryPageReqVO pageReqVO) {
        return modelRepositoryMapper.selectPage(pageReqVO);
    }

    @Override
    public ChatModel getChatModel(String name) {
        ModelRepositoryDO modelRepositoryDO = modelRepositoryMapper.selectOne(
                new LambdaQueryWrapperX<ModelRepositoryDO>()
                        .like(ModelRepositoryDO::getType, ModelType.LLM.getType())
                        .eq(ModelRepositoryDO::getName, name).last("limit 1"));
        if (modelRepositoryDO == null) {
            throw exception(MODEL_REPOSITORY_NOT_EXISTS);
        }
        return switch (modelRepositoryDO.getManufacturers()) {
            case ModelApiConstants.VE_MODEL_MANUFACTURERS_NAME -> {
                String result = stringRedisTemplate.opsForList().rightPopAndLeftPush(
                        THIRD_API_KEY_VOLCENGINE_LLM,
                        THIRD_API_KEY_VOLCENGINE_LLM,
                        Duration.ofSeconds(5)
                );
                System.err.println("使用了：" + result);
                ApiThirdKeyDO apiThirdKeyDO = JSONObject.parseObject(result, ApiThirdKeyDO.class);
                if (apiThirdKeyDO == null) {
                    throw exception(MODEL_REPOSITORY_NOT_EXISTS);
                }
                yield new VolcengineChatModel(modelRepositoryDO.getUrl(), apiThirdKeyDO.getApiKey());
            }
            default -> new OpenAIChatModel(modelRepositoryDO.getUrl());
        };
    }

    @Override
    public ImageModel getImageModel(String name) {
        ModelRepositoryDO modelRepositoryDO = modelRepositoryMapper.selectOne(
                new LambdaQueryWrapperX<ModelRepositoryDO>()
                        .eq(ModelRepositoryDO::getType, ModelType.TEXT2IMG.getType())
                        .eq(ModelRepositoryDO::getName, name).last("limit 1"));
        if (modelRepositoryDO == null) {
            throw exception(MODEL_REPOSITORY_NOT_EXISTS);
        }
        return switch (modelRepositoryDO.getManufacturers()) {
            case ModelApiConstants.VE_MODEL_MANUFACTURERS_NAME -> {
                String result = stringRedisTemplate.opsForList().rightPopAndLeftPush(
                        THIRD_API_KEY_VOLCENGINE_IMAGE,
                        THIRD_API_KEY_VOLCENGINE_IMAGE,
                        Duration.ofSeconds(5)
                );
                System.err.println("使用了：" + result);
                ApiThirdKeyDO apiThirdKeyDO = JSONObject.parseObject(result, ApiThirdKeyDO.class);
                if (apiThirdKeyDO == null) {
                    throw exception(MODEL_REPOSITORY_NOT_EXISTS);
                }
                yield new VolcengineImageModel(apiThirdKeyDO.getAk(), apiThirdKeyDO.getSk());
            }
            default -> new SDImageModel(modelRepositoryDO.getUrl());
        };
    }

    @Override
    public VideoModel getVideoModel(String name) {
        ModelRepositoryDO modelRepositoryDO = modelRepositoryMapper.selectOne(
                new LambdaQueryWrapperX<ModelRepositoryDO>()
                        .eq(ModelRepositoryDO::getType, ModelType.TEXT2VIDEO.getType())
                        .eq(ModelRepositoryDO::getName, name).last("limit 1"));
        if (modelRepositoryDO == null) {
            throw exception(MODEL_REPOSITORY_NOT_EXISTS);
        }
        return switch (modelRepositoryDO.getManufacturers()) {
            case ModelApiConstants.VE_MODEL_MANUFACTURERS_NAME -> {
                String result = stringRedisTemplate.opsForList().rightPopAndLeftPush(
                        THIRD_API_KEY_VOLCENGINE_SEAWEEDE,
                        THIRD_API_KEY_VOLCENGINE_SEAWEEDE,
                        Duration.ofSeconds(5)
                );
                System.err.println("使用了：" + result);
                ApiThirdKeyDO apiThirdKeyDO = JSONObject.parseObject(result, ApiThirdKeyDO.class);
                if (apiThirdKeyDO == null) {
                    throw exception(MODEL_REPOSITORY_NOT_EXISTS);
                }
                yield new VolcengineVideoModel(modelRepositoryDO.getUrl(), apiThirdKeyDO.getApiKey(), apiThirdKeyDO.getEndpoint());
            }
            default -> new CogVideoModel(modelRepositoryDO.getUrl());
        };
    }

    @Override
    public VllmModel getVllmModel(String name) {
        ModelRepositoryDO modelRepositoryDO = modelRepositoryMapper.selectOne(
                new LambdaQueryWrapperX<ModelRepositoryDO>()
                        .eq(ModelRepositoryDO::getType, ModelType.VLLM.getType())
                        .eq(ModelRepositoryDO::getName, name).last("limit 1"));
        // 后续优化
        return new GlmVllmModel(modelRepositoryDO.getUrl(), modelRepositoryDO.getApiKey());
    }

    @Override
    public List<ModelRepositoryDO> list(ModelRepositoryListReqVO listReqVO) {
        LambdaQueryWrapperX<ModelRepositoryDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        lambdaQueryWrapperX.eqIfPresent(ModelRepositoryDO::getType, listReqVO.getType())
                .eqIfPresent(ModelRepositoryDO::getExperience, CommonStatusEnum.ENABLE.getStatus())
                .eqIfPresent(ModelRepositoryDO::getStatus, CommonStatusEnum.ENABLE.getStatus());

        return modelRepositoryMapper.selectList(lambdaQueryWrapperX);
    }

    @Override
    public List<ModelRepositoryDO> square(ModelRepositoryListReqVO listReqVO) {
        LambdaQueryWrapperX<ModelRepositoryDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        lambdaQueryWrapperX.eqIfPresent(ModelRepositoryDO::getType, listReqVO.getType())
                .eqIfPresent(ModelRepositoryDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
        // 拆分成数组
        List<String> tagList = StrUtil.split(listReqVO.getTags(), ",");
        if (!tagList.isEmpty() && StrUtil.isNotBlank(tagList.get(0))) {
            // 动态添加 LIKE 条件
            lambdaQueryWrapperX.and(wrapper -> {
                for (String tag : tagList) {
                    wrapper.or().like(ModelRepositoryDO::getTags, "|" + tag + "|");
                }
            });
        }

        return modelRepositoryMapper.selectList(lambdaQueryWrapperX);
    }

    @Override
    public ModelRepositoryDO getModelRepositoryByName(String name) {
        return modelRepositoryMapper.getModelRepositoryByName(name);
    }

    @Override
    public List<ModelRepositoryDO> listByTypes(List<String> types) {
        return modelRepositoryMapper.listByTypes(types);
    }
}