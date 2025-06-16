package com.puhua.module.mp.enums;

import com.puhua.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * @author 中航普华
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "mp-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/mp";

    public static final String VERSION = "1.0.0";

    /**
     * ticket
     */
    public final static String GZH_TICKET = "gzh_ticket:";

    /**
     * 公众号临时二维码到期时间
     */
    public final static int GZH_TEMP_TICKET_TIMEOUT = 300;

}
