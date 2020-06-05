package com.yozosoft.fileserver.utils;

import com.alibaba.fastjson.JSON;
import com.yozosoft.fileserver.constants.EnumResultCode;

import java.util.HashMap;
import java.util.Map;

public class JsonResultUtils {

    public static String success() {
        return success(null, null);
    }

    public static String success(Object data) {
        return success(data, null);
    }

    public static String success(Object data, String message) {
        return buildJsonResult(EnumResultCode.E_SUCCESS.getValue(), data,
                message == null ? EnumResultCode.E_SUCCESS.getInfo() : message);
    }

    public static String fail() {
        return fail(null);
    }

    public static String fail(String message) {
        return buildJsonResult(EnumResultCode.E_FAIL.getValue(), null,
                message == null ? EnumResultCode.E_FAIL.getInfo() : message);
    }

    public static String buildJsonResult(Integer result, Object data, String message) {
        Map<String, Object> params = buildMapResult(result, data, message);
        return JSON.toJSONString(params);
    }

    public static String buildJsonResult(Map<String, Object> map) {
        return JSON.toJSONString(map);
    }

    public static String buildSuccessJsonResult(Map<String, Object> map) {
        map.put("errorCode", EnumResultCode.E_SUCCESS.getValue());
        map.put("message", EnumResultCode.E_SUCCESS.getInfo());
        return JSON.toJSONString(map);
    }

    public static String buildFailJsonResult(Map<String, Object> map) {
        map.put("errorCode", EnumResultCode.E_FAIL.getValue());
        map.put("message", EnumResultCode.E_FAIL.getInfo());
        return JSON.toJSONString(map);
    }

    public static String buildFailJsonResultByResultCode(EnumResultCode code) {
        return buildJsonResult(code.getValue(), null, code.getInfo());
    }

    public static Map<String, Object> successMapResult() {
        return successMapResult(null, EnumResultCode.E_SUCCESS.getInfo());
    }

    public static Map<String, Object> successMapResult(Object data) {
        return successMapResult(data, EnumResultCode.E_SUCCESS.getInfo());
    }

    public static Map<String, Object> successMapResult(Object data, String message) {
        return buildMapResult(EnumResultCode.E_SUCCESS.getValue(), data, message);
    }

    public static Map<String, Object> successAppendMapResult(Map<String, Object> resultMap) {
        resultMap.put("errorCode", EnumResultCode.E_SUCCESS.getValue());
        resultMap.put("message", EnumResultCode.E_SUCCESS.getInfo());
        return resultMap;
    }

    public static Map<String, Object> failMapResult() {
        return buildMapResult(EnumResultCode.E_FAIL.getValue(), null, EnumResultCode.E_FAIL.getInfo());
    }

    public static Map<String, Object> failMapResult(String messgae) {
        return buildMapResult(EnumResultCode.E_FAIL.getValue(), null, messgae);
    }

    public static Map<String, Object> failMapResult(Object data, String message) {
        return buildMapResult(EnumResultCode.E_FAIL.getValue(), data, message);
    }

    public static Map<String, Object> buildMapResultByResultCode(EnumResultCode resultCode) {
        return buildMapResult(resultCode.getValue(), null, resultCode.getInfo());
    }

    public static Map<String, Object> buildMapResultByResultCode(EnumResultCode resultCode, Object data) {
        return buildMapResult(resultCode.getValue(), data, resultCode.getInfo());
    }

    public static Map<String, Object> buildMapResult(Integer result, Object data, String message) {
        Map<String, Object> params = new HashMap<>();
        // 本次请求是否成功
        params.put("errorCode", result);
        // 用户封装信息，典型的是检验出错信息
        params.put("message", message);
        // 本次请求需要返回的数据
        params.put("data", data == null ? "" : data);
        return params;
    }

}
