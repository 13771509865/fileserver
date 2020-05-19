package com.yozosoft.fileserver.common.utils;

import com.yozosoft.util.SecretSignatureUtils;

import java.util.*;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-15 11:04
 **/
public class FileServerVerifyUtil {

    private static final String SIGN = "sign";

    private static String uniqSortParams(Map<String, String[]> params) {
        params.remove(SIGN);
        Map<String, String[]> sortedMap = new TreeMap<String, String[]>();
        sortedMap.putAll(params);
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> ketSet = sortedMap.keySet();
        Iterator<String> iter = ketSet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String[] strs = sortedMap.get(key);
            if (strs != null && strs.length > 0) {
                Arrays.sort(strs);
                for (int i = 0; i < strs.length; i++) {
                    String temp = strs[i];
                    stringBuffer.append(key).append("=").append(temp);
                }
            } else {
                stringBuffer.append(key).append("=");
            }
        }
        return stringBuffer.toString();
    }

    public static String generateSign(Map<String, String[]> params, String secret) throws Exception {
        String str = uniqSortParams(params);
        return SecretSignatureUtils.hmacSHA256(str, secret);
    }

    public static Boolean verifySign(Map<String, String[]> params, String secret, String sign) throws Exception {
        String generateSign = generateSign(params, secret);
        return sign.equals(generateSign);
    }
}
