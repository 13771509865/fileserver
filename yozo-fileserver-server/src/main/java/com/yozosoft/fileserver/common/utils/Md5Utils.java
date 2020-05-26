package com.yozosoft.fileserver.common.utils;

import com.twmacinta.util.MD5;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhoufeng
 * @description md5工具类
 * @create 2020-05-12 15:01
 **/
public class Md5Utils {

    public static String getMD5(String message) {
        return getMD5(message, false);
    }

    public static String getMD5(InputStream is) throws IOException {
        return getMD5(is, false);
    }

    public static String getMD5(File file) throws IOException {
        return getMD5(file, false);
    }

    public static String getMD5(String message, Boolean upperCase) {
        String md5 = DigestUtils.md5Hex(message);
        return upperCase ? md5.toUpperCase() : md5;
    }

    public static String getMD5(InputStream is, Boolean upperCase) throws IOException {
        String md5 = DigestUtils.md5Hex(is);
        return upperCase ? md5.toUpperCase() : md5;
    }

    public static String getMD5(File file, Boolean upperCase) throws IOException {
        String md5 = MD5.asHex(MD5.getHash(file));
        return upperCase ? md5.toUpperCase() : md5;
    }

}
