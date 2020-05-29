package com.yozosoft.fileserver.utils;

import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-29 09:27
 **/
public class ResultAnalysisUtils {

    private static final String errorCode = "errorCode";

    private static final String data = "data";

    private static final String message = "message";


    public static String getDownloadUrlBody(Map<String, Object> responseBody) {
        if (responseBody == null) {
            return "";
        }
        return responseBody.get(data).toString();
    }

    public static Boolean isResponseSuccess(Map<String, Object> responseBody) {
        if (responseBody == null) {
            return false;
        }
        Object errorCodeObj = responseBody.get(errorCode);
        if (errorCodeObj instanceof Integer) {
            Integer code = Integer.valueOf(errorCodeObj.toString());
            if (code == 0) {
                return true;
            }
        }
        return false;
    }

    public static String getErrorMessage(Map<String, Object> responseBody) {
        if (responseBody == null) {
            return "";
        }
        return responseBody.get(message).toString();
    }
}
