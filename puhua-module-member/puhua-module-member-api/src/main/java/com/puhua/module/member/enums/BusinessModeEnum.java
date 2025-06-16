package com.puhua.module.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author ZhangYi
 * @Date 2025年04月13日 12:21
 * @Description:
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BusinessModeEnum {
    TIME_BASED("time_based"),
    RECHARGE("recharge");
    private String value;

}
