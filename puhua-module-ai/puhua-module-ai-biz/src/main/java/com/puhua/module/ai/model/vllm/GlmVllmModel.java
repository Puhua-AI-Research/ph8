package com.puhua.module.ai.model.vllm;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class GlmVllmModel implements VllmModel {

    private String api = "/v1/chat/completions";
    private String baseUrl;

    private String apiKey;

    public GlmVllmModel(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public String completionNoStream(VllmRequestVo vllmRequestVo) {
        vllmRequestVo.setStream(false);
        String body = JSONObject.toJSONString(vllmRequestVo);

        // 执行请求
        String result = HttpRequest.post(this.baseUrl + api)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .body(body)
                .execute()
                .body();
        log.info("result:{}", result);
        return result;
    }


    @Override
    public HttpResponse completion(VllmRequestVo requestVo) throws IOException {
        requestVo.setStream(true);
        String body = JSONObject.toJSONString(requestVo);
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(this.baseUrl + api);
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + this.apiKey);
        // 设置请求体
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        // 执行请求
        return httpClient.execute(httpPost);
    }

}
