package com.puhua.module.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskMode {
    SYNC("sync", "同步"),
    ASYNC("async", "异步"),
    ASYNC_POLLING("async_polling", "异步轮询");

    private final String type;
    private final String name;

}