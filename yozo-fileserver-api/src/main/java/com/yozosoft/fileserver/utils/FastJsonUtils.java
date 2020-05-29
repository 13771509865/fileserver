package com.yozosoft.fileserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @author near
 */
public class FastJsonUtils {

    /**
     * json转map
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> targetOb) {
        return JSON.parseArray(jsonStr, targetOb);
    }

    public static Map<String, Object> parseJSON2Map(Object obj) {
        return parseJSON2Map(JSON.toJSONString(obj));
    }

    /**
     * json转map
     */
    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        Map<String, Object> map = new HashMap<>();
        // 最外层解析
        JSONObject json = JSONObject.parseObject(jsonStr);
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            if (v instanceof JSONArray) {
                try {
                    Iterator<Object> it = ((JSONArray) v).iterator();
                    List<Map<String, Object>> list = new ArrayList<>();
                    while (it.hasNext()) {
                        JSONObject jsonObj = (JSONObject) it.next();
                        list.add(parseJSON2Map(jsonObj.toString()));
                    }
                    map.put(k.toString(), list);
                } catch (Exception e) {
                    Iterator<Object> it = ((JSONArray) v).iterator();
                    List<String> list = new ArrayList<>();
                    while (it.hasNext()) {
                        String jsonObj = (String) it.next();
                        list.add(jsonObj);
                    }
                    map.put(k.toString(), list);
                }
            } else if (v instanceof JSONObject) {
                map.put(k.toString(), parseJSON2Map(v.toString()));
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    // jsonArray转list<Map<String,String>>
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseJSON2List(String jsonStr) {
        JSONArray jsonArr = JSONArray.parseArray(jsonStr);
        List<Map<String, Object>> list = new ArrayList<>();
        Iterator<Object> it = jsonArr.iterator();
        while (it.hasNext()) {
            JSONObject json2 = (JSONObject) it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }

    public static <T> T map2obj(Map<?, ?> map, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    public static <T> T json2obj(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T json2obj(Object obj, Class<T> clazz) {
        return json2obj(JSON.toJSONString(obj), clazz);
    }

    public static String serializeData(Object obj) {
        return JSON.toJSONString(obj);
    }
}
