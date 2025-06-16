package com.puhua.module.ai.model.image;

import com.puhua.module.ai.api.text2img.vo.GenerateImageRequestVo;
import com.puhua.module.ai.api.text2img.vo.GenerateImageResponseVo;

public interface ImageModel {
    /**
     * 生图
     *
     * @param vo
     * @return
     */
    public GenerateImageResponseVo generateImage(GenerateImageRequestVo vo);
}
