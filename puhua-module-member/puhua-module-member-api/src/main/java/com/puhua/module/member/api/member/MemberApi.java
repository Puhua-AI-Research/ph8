package com.puhua.module.member.api.member;

import com.puhua.module.member.api.member.vo.MemberUserInfoRespVO;
import com.puhua.module.member.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME) // TODO ZhangYi：fallbackFactory =
@Tag(name = "RPC 服务 - 用户")
public interface MemberApi {

    String PREFIX = ApiConstants.PREFIX + "/member";

    /**
     * 获取用户
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping(PREFIX + "get")
    @Operation(summary = "获取用户信息")
    MemberUserInfoRespVO getMemberUser(@RequestParam Long userId);

    /**
     * 增减额度
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping(PREFIX + "reducedBalance")
    @Operation(summary = "增减额度")
    MemberUserInfoRespVO changeBalance(@RequestParam Long userId, @RequestParam Long balance);
}
