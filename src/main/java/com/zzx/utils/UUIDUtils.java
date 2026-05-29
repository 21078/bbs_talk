package com.zzx.utils;


import java.util.UUID;

/**
 * UUID工具类
 * 提供UUID生成的相关方法
 */
public class UUIDUtils {
    /**
 * 生成UUID
 * 生成一个不带连字符的UUID字符串
 *
 * @return 32位的UUID字符串
 */
public static String generateUUID() {
    return UUID.randomUUID().toString().replaceAll("-", "");
}
}
