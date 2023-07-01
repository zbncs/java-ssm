package com.itheima.reggie.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentUserId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentUserId() {
        return threadLocal.get();
    }
}
