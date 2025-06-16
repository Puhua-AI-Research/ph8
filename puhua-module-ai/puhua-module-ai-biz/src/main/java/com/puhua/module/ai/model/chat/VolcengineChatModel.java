package com.puhua.module.ai.model.chat;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.puhua.module.ai.api.llm.vo.completion.chat.ChatCompletionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author ZhangYi
 * @Date 2024年12月12日 11:58
 * @Description: 通用chat模型
 */
@Slf4j
public class VolcengineChatModel implements ChatModel {


    private String baseUrl;

    private String apiKey;

    public VolcengineChatModel(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public VolcengineChatModel(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public String chat(ChatCompletionRequest chatCompletionRequest,String action) {
        chatCompletionRequest.setStream(false);
        String body = JSONObject.toJSONString(chatCompletionRequest);

        // 执行请求
        String result = HttpRequest.post(this.baseUrl)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .body(body)
                .execute()
                .body();
        log.info("result:{}", result);
        return result;
    }

    @Override
    public HttpResponse stream(ChatCompletionRequest chatCompletionRequest,String action) throws IOException {
        chatCompletionRequest.setStream(true);
        String body = JSONObject.toJSONString(chatCompletionRequest);
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(this.baseUrl);
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + this.apiKey);

        // 设置请求体
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        long traceId = IdUtil.getSnowflakeNextId();
        if (!Objects.equals(statusCode, 200)) {
            // 转换响应实体为字符串
            String responseBody = EntityUtils.toString(response.getEntity());
            log.error("traceId:{},[chat-api]responseBody: {}", traceId, responseBody);
            log.error("traceId:{},[chat-api]statusCode: {}", traceId, statusCode);
        }
        log.info("traceId:{},[chat-api]statusCode: {}", traceId, statusCode);
        return response;
    }
}
