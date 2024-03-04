package org.example.utils;

import java.util.Random;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
public final class StringUtils {
    private static final Random RANDOM = new Random();
    private static final String CHARACTER_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private StringUtils() {
        // 拋出異常是為了防止透過反射呼叫私有建構函數
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTER_SET.charAt(RANDOM.nextInt(CHARACTER_SET.length())));
        }
        return sb.toString();
    }
}
