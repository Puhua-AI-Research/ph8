package com.puhua.module.member.enums;

import com.puhua.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode USER_NOT_EXISTS = new ErrorCode(2_001_001, "用户不存在");
    ErrorCode CAPTCHA_INCORRECT = new ErrorCode(2_001_003, "请输入正确的验证码");
    ErrorCode QUOTA_PRODUCT_NOT_EXISTS = new ErrorCode(2_002_001, "商品不存在");
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(2_003_001, "订单信息不存在");
    ErrorCode ORDER_UPDATE_PAID_FAIL_PAY_ORDER_STATUS_NOT_SUCCESS = new ErrorCode(2_003_002, "交易订单更新支付状态失败，支付单状态不是【支付成功】状态");
    ErrorCode ORDER_UPDATE_PAID_FAIL_PAY_PRICE_NOT_MATCH = new ErrorCode(2_003_003, "交易订单更新支付状态失败，支付单金额不匹配");
    ErrorCode ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR = new ErrorCode(2_003_004, "交易订单更新支付状态失败，支付单编号不匹配");
    ErrorCode BALANCE_LOG_NOT_EXISTS = new ErrorCode(2_004_001, "用户流水信息不存在");
}
