package com.yozosoft.fileserver.common.utils;

import java.util.UUID;

/**
 * @author zhoufeng
 * @description uuid工具类
 * @create 2020-05-12 15:01
 **/
public final class UUIDHelper {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String generateUUID(boolean upperCase) {
        String uuid = generateUUID();
        return upperCase == true ? uuid.toUpperCase() : uuid;
    }
}
