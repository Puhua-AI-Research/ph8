package com.puhua.module.ai.model.video;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.exception.ServiceException;

import java.util.HashMap;

public class CogVideoModel implements VideoModel {


    private String baseUrl;

    public CogVideoModel(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public JSONObject generateVideo(HashMap<String, Object> requestParams) {
        int width = Integer.parseInt(requestParams.get("width").toString());
        int height = Integer.parseInt(requestParams.get("height").toString());
        if (width < 64 || width > 1024 || height < 64 || height > 1024) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Width and height must be between 64 and 1024");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", IdUtil.fastSimpleUUID());
        return jsonObject;
    }

    @Override
    public JSONObject generateVideoSync(HashMap<String, Object> requestParams) {
        String body = JSONObject.toJSONString(requestParams);
        //链式构建请求
        String result = HttpRequest.post(this.baseUrl)
                .body(body)
                .execute()
                .body();
        return JSONObject.parseObject(result);
    }

    @Override
    public JSONObject checkTaskStatus(String id) {
        // todo 暂未实现
        throw new UnsupportedOperationException();
    }
}
