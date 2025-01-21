package com.itguigu.util;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2025/1/20 20:42
 **/
import org.json.JSONObject;
import org.json.JSONArray;

public class JsonUtils {

    /**
     * 判断字符串是否为合法的JSON对象
     * @param str 待检查字符串
     * @return true: 是有效的JSON对象，false: 不是有效的JSON对象
     */
    public static boolean isValidJsonObject(String str) {
        try {
            new JSONObject(str);  // 尝试解析为JSONObject
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为合法的JSON数组
     * @param str 待检查字符串
     * @return true: 是有效的JSON数组，false: 不是有效的JSON数组
     */
    public static boolean isValidJsonArray(String str) {
        try {
            new JSONArray(str);  // 尝试解析为JSONArray
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为合法的JSON格式（无论是JSON对象还是JSON数组）
     * @param str 待检查字符串
     * @return true: 是有效的JSON格式，false: 不是有效的JSON格式
     */
    public static boolean isValidJson(String str) {
        return isValidJsonObject(str) || isValidJsonArray(str);
    }

}

