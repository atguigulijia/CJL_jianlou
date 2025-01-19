package com.itguigu.service;

import com.itguigu.model.GlobalProperties;
import com.itguigu.model.Response;
import com.itguigu.model.User;
import com.itguigu.util.MD5Util;
import com.itguigu.util.UserAgentUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class RequestApi {

    // 用户登录
    public static Response login(String acc, String pwd, Proxy proxy) throws Exception {
        URL url = new URL("https://oauth.cangjingling.com/connect/authorize");
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("POST");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setDoOutput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8")) {
            writer.write("{\"userName\":\"" + acc + "\",\"password\":\"" + MD5Util.encryptWith32Bit(pwd).toUpperCase() + "\"}");
            writer.flush();
        }

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    // 用户登录
    public static Response login1(String code, Proxy proxy) throws Exception {
        URL url = new URL("https://oauth.cangjingling.com/connect/accessToken?code=" + code);
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    //获取用户信息
    public static Response getUserInfo(String token, Proxy proxy) throws Exception {
        URL url = new URL("https://oauth.cangjingling.com/connect/userInfo");
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("POST");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("Authorization", token);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    //获取市场藏品类别id列表
    public static Response getGoodsCategoryIdList(Proxy proxy) throws Exception {
        URL url = new URL("https://api.cangjingling.com/api/Parameter?code=Classify");
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    //根据类别id获取藏品类别信息列表
    public static Response getGoodsCategoryListByCategoryId(String categoryId, String pageNo, Proxy proxy) throws Exception {
        URL url = new URL("https://api.cangjingling.com/api/Market/MarketArtwordIndex?pageNo=" + pageNo + "&pageSize=10&category=" + categoryId);
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    //根据藏品id获取市场藏品集合
    public static Response getGoodsInfoListByGoodsId(String goodsId, Proxy proxy) throws Exception {
        URL url = new URL("https://api.cangjingling.com/api/Market/MarketArtwordList?artworkUid=" + goodsId + "&pageNo=1&pageSize=20&orderBy=");
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("GET");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");

        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }

    //创建市场订单
    public static Response createOrder(User user,Long  publishPrice,String accountArtworkUid,Proxy proxy) throws Exception {
        URL url = new URL("https://api.cangjingling.com/api/Market/Order");
        HttpURLConnection httpConn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));


        httpConn.setRequestMethod("POST");
        httpConn.setReadTimeout(5000);
        httpConn.setConnectTimeout(5000);
        httpConn.setRequestProperty("Authorization", user.getToken());
        httpConn.setRequestProperty("User-Agent", UserAgentUtil.randomGetUA());
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setDoOutput(true);
        try (OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8")) {
            writer.write("{\"publishPrice\":"+publishPrice+",\"accountUid\":\""+user.getUid()+"\",\"accountArtworkUid\":\""+accountArtworkUid+"\"}");
            writer.flush();
        }
        String resBody = getResponseBody(httpConn);
        return Response.build(httpConn.getResponseCode(), resBody);
    }


    // 获取响应体
    private static String getResponseBody(HttpURLConnection httpConn) throws IOException {
        InputStream responseStream;
        try {
            responseStream = httpConn.getInputStream();
        } catch (IOException e) {
            responseStream = httpConn.getErrorStream();
        }

        if (responseStream != null) {
            if ("gzip".equals(httpConn.getContentEncoding())) {
                responseStream = new GZIPInputStream(responseStream);
            }
            try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        }
        return "";
    }

}
