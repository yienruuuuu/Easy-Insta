package org.example.utils;

import org.example.bean.enumtype.LanguageEnum;

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

    public static LanguageEnum detectLanguage(String input) {
        if (input == null || input.isEmpty()) {
            return LanguageEnum.OTHER;
        }
        // 檢查是否包含日文字符
        if (input.matches(".*[\u3040-\u309F\u30A0-\u30FF].*")) {
            return LanguageEnum.JA;
        }
        // 檢查是否包含中文字符
        if (input.matches(".*[\u4E00-\u9FA5].*")) {
            return LanguageEnum.ZH_TW;
        }
        // 檢查是否包含英文字符
        if (input.matches(".*[a-zA-Z].*")) {
            return LanguageEnum.EN;
        }
        // 檢查是否包含俄文字符
        if (input.matches(".*[\u0400-\u04FF].*")) {
            return LanguageEnum.RU;
        }
        // 預設為其他
        return LanguageEnum.OTHER;
    }

    public static void main(String[] args) {
        String randomString = "張嘉勝";
        System.out.println(detectLanguage(randomString));
    }
}
