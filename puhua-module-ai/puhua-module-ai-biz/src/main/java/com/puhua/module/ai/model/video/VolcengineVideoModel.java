package com.puhua.module.ai.model.video;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class VolcengineVideoModel implements VideoModel {


    private String baseUrl;
    private String apiKey;
    private String endpoint;

    private final String createApi = "/api/v3/contents/generations/tasks";
    private final String queryApi = "/api/v3/contents/generations/tasks/";

    public VolcengineVideoModel(String baseUrl, String apiKey, String endpoint) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }

    @Override
    public JSONObject generateVideoSync(HashMap<String, Object> requestParams) {
        throw new RuntimeException("该模型不支持同步生成的方式");
    }

    @Override
    public JSONObject generateVideo(HashMap<String, Object> requestParams) {
        String body = JSONObject.toJSONString(requestParams);
        //链式构建请求
        String result = HttpRequest.post(this.baseUrl + createApi)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .body(body)
                .execute()
                .body();
        return JSONObject.parseObject(result);
    }

    @Override
    public JSONObject checkTaskStatus(String id) {
        //链式构建请求
        String result = HttpRequest.get(this.baseUrl + queryApi + id)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .execute()
                .body();
        return JSONObject.parseObject(result);
    }
}
