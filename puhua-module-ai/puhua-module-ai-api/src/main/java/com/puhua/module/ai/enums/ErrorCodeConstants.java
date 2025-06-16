package com.puhua.module.ai.enums;


import com.puhua.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode API_KEY_NOT_EXISTS = new ErrorCode(3_001_001, "apiKey not exists");
    ErrorCode MODEL_REPOSITORY_NOT_EXISTS = new ErrorCode(3_002_001, "模型不存在或暂不可用");
    ErrorCode TASK_NOT_EXISTS = new ErrorCode(3_003_001, "用户计费任务不存在");
    ErrorCode API_TAGS_NOT_EXISTS = new ErrorCode(3_004_001, "模型tag不存在");
    ErrorCode API_THIRD_KEY_NOT_EXISTS = new ErrorCode(3_005_001, "apiKey不存在");
    ErrorCode MODEL_RESPONSE_PARSE_FAILED = new ErrorCode(3_006_002, "系统错误，解析模型响应失败");
    ErrorCode RPM_LIMIT_ERROR = new ErrorCode(3_006_003, "请求数量达到上限");
    ErrorCode TPM_LIMIT_ERROR = new ErrorCode(3_006_004, "token数量达到上限");
}
