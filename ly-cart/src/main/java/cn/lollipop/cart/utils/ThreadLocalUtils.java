package cn.lollipop.cart.utils;

import cn.lollipop.auth.pojo.UserInfo;

public class ThreadLocalUtils {
    private static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    private ThreadLocalUtils() {
    }

    public static void set(UserInfo user) {
        threadLocal.set(user);
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static UserInfo get() {
        return threadLocal.get();
    }
}
