package com.puhua.module.ai.service.openapi;

import com.alibaba.fastjson.JSONObject;
import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;
import com.puhua.module.ai.api.text2video.vo.GenerateVideoRequestVo;
import com.puhua.module.ai.api.text2video.vo.GenerateVideoResponseVo;

import java.util.HashMap;

public interface VideoService {
    /**
     * 生图
     *
     * @param userId 用户id
     * @param vo     请求
     * @return 响应
     */
    JSONObject generate(Long userId, HashMap<String, Object> vo);

    /**
     * 任务查询
     */
    JSONObject get(String id);

}
