package com.puhua.module.member.enums;

import cn.hutool.core.util.ObjUtil;
import com.puhua.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 通用状态枚举
 *
 * @author 中航普华
 */
@Getter
@AllArgsConstructor
public enum ProductStatusEnum {

    ENABLE(1, "上架"),
    DISABLE(0, "下架");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;


}
