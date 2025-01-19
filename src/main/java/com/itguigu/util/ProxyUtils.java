package com.itguigu.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Scanner;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/12/17 19:53
 **/
public class ProxyUtils {

    /**
     * 获取代理类对象
     *
     * @param url 请求的URL
     * @return 代理类对象，若请求失败则返回 null
     */
    public static Proxy getProxy(String url) {
        try {
            URL url1 = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) url1.openConnection();
            httpConn.setRequestMethod("GET");
            int responseCode = httpConn.getResponseCode();

            // 只在响应码为 200 且返回的内容包含 ":" 时处理
            if (responseCode == 200) {
                try (InputStream responseStream = httpConn.getInputStream();
                     Scanner scanner = new Scanner(responseStream)) {

                    String response = scanner.hasNext() ? scanner.next() : "";
                    if (response.contains(":")) {
                        String[] split = response.split(":");
                        if (split.length == 2) {
                             LogUtil.println("提取代理ip成功:" + response);
                            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(split[0], Integer.parseInt(split[1])));
                        }
                    }
                     LogUtil.println("提取代理ip失败, 返回值格式错误: " + response);
                }
            } else {
                 LogUtil.println("提取代理ip失败, 响应码: " + responseCode);
            }
        } catch (IOException e) {
             LogUtil.println("请求失败: " + e.getMessage());
        }
        return null;  // 请求失败或格式错误时返回 null
    }

}
