package com.puhua.module.ai.service.apithirdkey;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.enums.CommonStatusEnum;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.puhua.module.ai.controller.admin.apithirdkey.vo.*;
import com.puhua.module.ai.dal.dataobject.apithirdkey.ApiThirdKeyDO;
import com.puhua.framework.common.pojo.PageResult;
import com.puhua.framework.common.pojo.PageParam;
import com.puhua.framework.common.util.object.BeanUtils;

import com.puhua.module.ai.dal.mysql.apithirdkey.ApiThirdKeyMapper;

import static com.puhua.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.puhua.module.ai.enums.ErrorCodeConstants.*;

/**
 * 三方平台apiKey Service 实现类
 *
 * @author 中航普华
 */
@Service
@Validated
public class ApiThirdKeyServiceImpl implements ApiThirdKeyService {

    @Resource
    private ApiThirdKeyMapper apiThirdKeyMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        List<ApiThirdKeyDO> apiThirdKeyDOS = apiThirdKeyMapper.selectList(ApiThirdKeyDO::getStatus, CommonStatusEnum.ENABLE);

        Map<String, List<ApiThirdKeyDO>> groupedByResourceId = apiThirdKeyDOS.stream()
                .collect(Collectors.groupingBy(ApiThirdKeyDO::getResourceId));

        groupedByResourceId.forEach((resourceId, list) -> {
            String redisKey = "third_api_key:" + resourceId;
            Boolean exists = stringRedisTemplate.hasKey(redisKey);

            if (exists != null && !exists) {
                // 场景1：Key不存在，初始化列表
                List<String> jsonList = list.stream()
                        .filter(doObj -> doObj.getQps() > 0)
                        .flatMap(doObj ->
                                Collections.nCopies(doObj.getQps(), JSON.toJSONString(doObj)).stream()
                        )
                        .collect(Collectors.toList());
                if (!jsonList.isEmpty()) {
                    stringRedisTemplate.opsForList().rightPushAll(redisKey, jsonList);
                }
            } else {
                // 场景2：Key存在，动态调整
                List<String> existingElements = stringRedisTemplate.opsForList().range(redisKey, 0, -1);

                // 步骤1：统计当前元素频率
                Map<String, Integer> currentFreq = new HashMap<>();
                if (existingElements != null) {
                    existingElements.forEach(json ->
                            currentFreq.put(json, currentFreq.getOrDefault(json, 0) + 1)
                    );
                }

                // 步骤2：构建预期元素及其QPS
                Map<String, Integer> expectedFreq = new HashMap<>();
                list.stream()
                        .filter(doObj -> doObj.getQps() > 0)
                        .forEach(doObj -> {
                            String json = JSON.toJSONString(doObj);
                            expectedFreq.put(json, doObj.getQps());
                        });

                // 步骤3：处理需要删除的元素（包含两类：QPS降为0的记录 & 数据库不存在的记录）
                currentFreq.keySet().stream()
                        .filter(json -> !expectedFreq.containsKey(json)) // 不在预期中的元素
                        .forEach(json ->
                                // 删除所有匹配元素（count=0表示全删）
                                stringRedisTemplate.opsForList().remove(redisKey, 0, json)
                        );

                // 步骤4：调整现有元素数量
                expectedFreq.forEach((json, targetQps) -> {
                    int currentCount = currentFreq.getOrDefault(json, 0);
                    int diff = currentCount - targetQps;

                    if (diff > 0) {
                        // 删除多余元素（从头部开始删）
                        stringRedisTemplate.opsForList().remove(redisKey, diff, json);
                    } else if (diff < 0) {
                        // 补充不足元素
                        List<String> additions =
                                Collections.nCopies(-diff, json);
                        stringRedisTemplate.opsForList().rightPushAll(redisKey, additions);
                    }
                });
            }
        });
    }

    @Override
    public Long createApiThirdKey(ApiThirdKeySaveReqVO createReqVO) {
        // 插入
        ApiThirdKeyDO apiThirdKey = BeanUtils.toBean(createReqVO, ApiThirdKeyDO.class);
        apiThirdKeyMapper.insert(apiThirdKey);
        // 返回
        return apiThirdKey.getId();
    }

    @Override
    public void updateApiThirdKey(ApiThirdKeySaveReqVO updateReqVO) {
        // 校验存在
        validateApiThirdKeyExists(updateReqVO.getId());
        // 更新
        ApiThirdKeyDO updateObj = BeanUtils.toBean(updateReqVO, ApiThirdKeyDO.class);
        apiThirdKeyMapper.updateById(updateObj);
    }

    @Override
    public void deleteApiThirdKey(Long id) {
        // 校验存在
        validateApiThirdKeyExists(id);
        // 删除
        apiThirdKeyMapper.deleteById(id);
    }

    private void validateApiThirdKeyExists(Long id) {
        if (apiThirdKeyMapper.selectById(id) == null) {
            throw exception(API_THIRD_KEY_NOT_EXISTS);
        }
    }

    @Override
    public ApiThirdKeyDO getApiThirdKey(Long id) {
        return apiThirdKeyMapper.selectById(id);
    }

    @Override
    public PageResult<ApiThirdKeyDO> getApiThirdKeyPage(ApiThirdKeyPageReqVO pageReqVO) {
        return apiThirdKeyMapper.selectPage(pageReqVO);
    }

    @Override
    public ApiThirdKeyDO getApiThirdKeyByEndPoint(String endpoint) {
        return apiThirdKeyMapper.getApiThirdKeyByEndPoint(endpoint);
    }

}