package com.puhua.module.ai.model.vllm;

import com.puhua.module.ai.api.vllm.vo.VllmRequestVo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface VllmModel {

    /**
     * completionNoStream 非流式
     *
     * @param vllmRequestVo vo
     * @return
     */
    public String completionNoStream(VllmRequestVo vllmRequestVo);

    /**
     * completion 流式
     *
     * @param vo req
     * @return httpResponse
     */
    public HttpResponse completion(VllmRequestVo vo) throws IOException;
}
