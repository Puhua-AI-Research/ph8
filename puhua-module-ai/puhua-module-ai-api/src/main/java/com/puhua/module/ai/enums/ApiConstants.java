package com.puhua.module.ai.enums;

import com.puhua.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * @author 中航普华
 */
public class ApiConstants {

    /**
     * 服务名
     * <p>
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "ai-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/ai";

    public static final String VERSION = "1.0.0";
    /**
     * config api
     */
    public static final String CONFIG_API = "/openai/v1/get_continue_config";

    /**
     * models api
     */
    public static final String MODELS_API = "/openai/v1/models";

    /**
     * models api
     */
    public static final String CODE_CONFIG_API = "/openai/v1/CodeConfig";

}
