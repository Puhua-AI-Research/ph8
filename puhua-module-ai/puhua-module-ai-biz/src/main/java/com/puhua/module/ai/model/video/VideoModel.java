package com.puhua.module.ai.model.video;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public interface VideoModel {

    /**
     * 创建视频生成任务(同步)
     *
     * @param requestParams req
     * @return res
     */
    public JSONObject generateVideoSync(HashMap<String, Object> requestParams);

    /**
     * 创建视频生成任务
     *
     * @param requestParams req
     * @return res
     */
    public JSONObject generateVideo(HashMap<String, Object> requestParams);

    /**
     * 查询视频生成任务
     *
     * @param id 任务id
     * @return res
     */
    public JSONObject checkTaskStatus(String id);
}
