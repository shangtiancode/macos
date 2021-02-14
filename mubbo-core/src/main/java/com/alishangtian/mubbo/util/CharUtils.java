package com.alishangtian.mubbo.util;

/**
 * @Description StringUtils
 * @ClassName StringUtils
 * @Author alishangtian
 * @Date 2021/1/15 19:11
 */
public class CharUtils {
    /**
     * 首字母转小写
     *
     * @param str
     * @return
     */
    public static String lowerFirstChar(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }
}
