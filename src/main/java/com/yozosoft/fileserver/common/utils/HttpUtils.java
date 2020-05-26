package com.yozosoft.fileserver.common.utils;

import com.yozosoft.fileserver.common.constants.SysConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-25 17:35
 **/
public class HttpUtils {

    public static String urlEncode(String value) {
        return urlEncode(value, SysConstant.CHARSET);
    }

    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        } else {
            try {
                String encoded = URLEncoder.encode(value, encoding);
                return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
            } catch (UnsupportedEncodingException var3) {
                return "";
            }
        }
    }
}
