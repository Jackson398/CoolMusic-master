package com.cool.music.utils;

import android.text.TextUtils;

import java.lang.reflect.Method;

public class SystemUtils {

    //使用反射获取系统属性
    private static String getSystemProperty(String key) {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String) getMethod.invoke(classType, key);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }


    //通过系统属性获取手机版本号，判断是否为魅族手机
    public static boolean isFlyme() {
        String flymeFlag = getSystemProperty("ro.build.display.id");
        return !TextUtils.isEmpty(flymeFlag) && flymeFlag.toLowerCase().contains("flyme");
    }
}
