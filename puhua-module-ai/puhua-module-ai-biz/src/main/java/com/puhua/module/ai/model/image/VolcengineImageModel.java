package com.puhua.module.ai.model.image;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.puhua.framework.common.exception.ServiceException;
import com.puhua.module.ai.api.text2img.vo.*;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolcengineImageModel implements ImageModel {

    private static final Logger log = LoggerFactory.getLogger(VolcengineImageModel.class);
    private String ak;
    private String sk;

    private final int TASK_SUCCESS_CODE = 10000;


    @Override
    public GenerateImageResponseVo generateImage(GenerateImageRequestVo vo) {

        if (vo.getWidth() < 256 || vo.getWidth() > 768) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Width must be between 256 and 768");
        }

        if (vo.getHeight() < 256 || vo.getHeight() > 768) {
            throw new ServiceException(HttpStatus.HTTP_BAD_REQUEST, "Height must be between 256 and 768");
        }

        IVisualService visualService = VisualServiceImpl.getInstance();
        // 密钥策略

        visualService.setAccessKey(this.ak);
        visualService.setSecretKey(this.sk);

        VEGenerateImageReqVo veGenerateImageReqVo = VEGenerateImageReqVo
                .builder()
                .reqKey("high_aes_general_v21_L")
                .prompt(vo.getPrompt())
                .modelVersion("general_v2.1_L")
                .seed(vo.getSeed())
                .ddimSteps(vo.getSteps())
                .width(vo.getWidth())
                .height(vo.getHeight())
                .returnUrl(true)
                .useSr(false)
                .build();
        JSONObject reqBody = (JSONObject) JSONObject.toJSON(veGenerateImageReqVo);
        GenerateImageResponseVo resp = new GenerateImageResponseVo();
        try {
            Object response = visualService.cvProcess(reqBody);
            System.out.println(JSONObject.toJSONString(response));
            VEGenerateImageResponseVo veGenerateImageResponseVo = JSONObject.parseObject(JSONObject.toJSONString(response), VEGenerateImageResponseVo.class);
            if (!veGenerateImageResponseVo.getCode().equals(TASK_SUCCESS_CODE)) {
                throw new ServiceException(veGenerateImageResponseVo.getCode(), veGenerateImageResponseVo.getMessage());
            }
            List<GenerateImageResponseItem> respItems = Lists.newArrayList();
            List<String> imageUrls = veGenerateImageResponseVo.getData().getImageUrls();
            for (int i = 0; i < imageUrls.size(); i++) {
                respItems.add(new GenerateImageResponseItem(i, imageUrls.get(i)));
            }
            resp.setData(respItems);
        } catch (Exception e) {
            log.error("图像生成任务发生异常,req:{}，err:{}", JSONObject.toJSONString(vo), e.getMessage());
        }
        return resp;
    }
}
