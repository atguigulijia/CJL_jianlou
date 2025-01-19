package com.itguigu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/5/18 20:13
 * 三方短信推送服务
 **/
public class ThirdMsgUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThirdMsgUtil.class);

    /**
     * 奇瑞ev
     *
     * @param phone
     */
    public static void qiruiEV(String phone) {
        try {
            String urlParam = "/applet/wechat/smsCode";
            String timestamp = System.currentTimeMillis() + "";
            String chars = "e61bf2854f7341839fce4e492a163d2dchery-iCar";
            URL url = new URL("https://midend.icar-ecology.com/applet/wechat/smsCode");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");

            httpConn.setRequestProperty("app", "cheryev");
            httpConn.setRequestProperty("app-v", "3.0.0");
            httpConn.setRequestProperty("lng", "0.00");
            httpConn.setRequestProperty("signature", MD5Util.encryptWith32Bit(chars + urlParam + timestamp + "[" + phone + "]"));
            httpConn.setRequestProperty("keys", "receiver");
            httpConn.setRequestProperty("channel", "2");
            httpConn.setRequestProperty("nonce", "chery-iCar");
            httpConn.setRequestProperty("platform", "7");
            httpConn.setRequestProperty("url", urlParam);
            httpConn.setRequestProperty("net-type", "wifi");
            httpConn.setRequestProperty("dev-id", "24d011fcd0567366a8f0e93308d4041e3");
            httpConn.setRequestProperty("dev-model", "SM-G9730");
            httpConn.setRequestProperty("lat", "0.00");
            httpConn.setRequestProperty("timestamp", timestamp);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Host", "midend.icar-ecology.com");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "okhttp/4.10.0");

            httpConn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
            writer.write("receiver=" + phone);
            writer.flush();
            writer.close();
            httpConn.getOutputStream().close();

            InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                    ? httpConn.getInputStream()
                    : httpConn.getErrorStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String response = s.hasNext() ? s.next() : "";
            logger.info(phone+"推送成功");
        } catch (IOException e) {
            logger.error("推送失败,"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        qiruiEV("13397497249");
    }
}
