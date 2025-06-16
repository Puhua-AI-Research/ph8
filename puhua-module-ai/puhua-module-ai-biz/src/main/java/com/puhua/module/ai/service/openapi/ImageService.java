package com.puhua.module.ai.service.openapi;

import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;

public interface ImageService {
    /**
     * 生图
     *
     * @param userId 用户id
     * @param vo     请求
     * @return 响应
     */
    GenerateImageResponseVo generateImage( GenerateImageRequestVo vo);

}
