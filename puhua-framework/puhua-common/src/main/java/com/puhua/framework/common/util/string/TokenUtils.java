package com.puhua.framework.common.util.string;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author ZhangYi
 * @Date 2024年07月23日 21:26
 * @Description:
 */
public class TokenUtils {
    public static int calculateTokens(String text) {
        double tokens = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            // 检查是否为中文字符
            if (ch >= '\u4e00' && ch <= '\u9fa5') {
                // 中文字符，每个字符约等于 0.6 个 token 按 0.65计算
                tokens += 0.8;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                // 英文字符（包括大小写），每个字符约等于 0.3 个 token
                tokens += 0.4;
            }
        }
        return (int) tokens;
    }

    /**
     * 计算每秒输出的字符数
     *
     * @param begin  开始时间（毫秒）
     * @param end    结束时间（毫秒）
     * @param answer 输出字符串
     * @return 每秒输出的字符数，保留两位小数
     */
    public static BigDecimal calculateCharsPerSecond(long begin, long end, String answer) {
        if (begin >= end) {
            throw new IllegalArgumentException("结束时间不能早于或等于开始时间！");
        }

        long durationMillis = end - begin; // 时间差（毫秒）
        if (durationMillis == 0) {
            return new BigDecimal("0");
        }

        int charCount = answer.length();
        double seconds = durationMillis / 1000.0;

        return new BigDecimal(String.valueOf(charCount / seconds)).setScale(2, RoundingMode.HALF_UP);

    }

}
