package com.zzx.utils;

import javax.servlet.http.Cookie;

/**
 * Cookie工具类
 * 提供Cookie相关的操作方法
 */
public class CookieUtils {
    /**
 * 根据名称查找Cookie
 * 在Cookie数组中查找指定名称的Cookie
 *
 * @param cookies Cookie数组
 * @param name Cookie名称
 * @return 找到的Cookie对象，未找到返回null
 */
public static Cookie findCookie(Cookie[] cookies, String name) {
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
    }
    return null;
}
}
