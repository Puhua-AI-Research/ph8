package com.puhua.module.ai.model.image;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;

public class SDImageModel implements ImageModel {


    private String baseUrl;

    public SDImageModel(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public GenerateImageResponseVo generateImage(GenerateImageRequestVo vo) {
        if (vo.getWidth() < 32 || vo.getWidth() > 2048) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Width must be between 32 and 2048");
        }

        if (vo.getHeight() < 32 || vo.getHeight() > 2048) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Height must be between 32 and 2048");
        }
        String body = JSONObject.toJSONString(vo);
        //链式构建请求
        String result = HttpRequest.post(this.baseUrl)
                .body(body)
                .execute()
                .body();
        System.err.println("image result:" + result);
        return JSONObject.parseObject(result, GenerateImageResponseVo.class);
    }
}
