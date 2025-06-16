package com.puhua.module.member.convert.auth;


import com.puhua.module.member.controller.app.auth.vo.AppAuthLoginRespVO;
import com.puhua.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);


    AppAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean, String openid);


}
