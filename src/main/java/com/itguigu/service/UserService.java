package com.itguigu.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.itguigu.api.RequestApi;
import com.itguigu.model.Response;
import com.itguigu.model.User;
import com.itguigu.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2025/1/20 18:13
 * 有关用户所有操作处理逻辑
 **/
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // 单例实例，使用 volatile 确保线程安全
    private static volatile UserService instance = null;

    // 私有构造函数，外部无法直接实例化
    private UserService() {

    }

    // 获取单例实例的公共方法，使用双重检查锁实现懒加载
    public static UserService getInstance() {
        if (instance == null) {
            synchronized (PurchaseService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }


    /**
     * @param acc
     * @param pwd
     * @param proxy
     * @return
     */
    public User handlerLogin(String acc, String pwd, Proxy proxy) {
        String code = null;
        String token = null;
        String uid = null;
        //获取code
        try {
            Response res = RequestApi.login(acc, pwd, proxy);
            String msg = res.getMsg();
//            logger.info(msg);

            if (JsonUtils.isValidJson(msg)) {
                //json数据->返回格式正确
                JSONObject json = JSON.parseObject(msg);
                if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                    code = json.getJSONObject("data").getString("code");
                } else {
                    logger.info("登录失败:" + msg);
                }
            } else {
                //非json数据->可能被墙或者读取乱码
                logger.info("登录失败:" + res.getCode());
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
        }
        //根据code获取token
        try {
            Response res = RequestApi.login1(code, proxy);
            String msg = res.getMsg();
//            logger.info(msg);
            if (JsonUtils.isValidJson(msg)) {
                //json数据->返回格式正确
                JSONObject json = JSON.parseObject(msg);
                if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                    token = "bearer " + json.getJSONObject("data").getString("accessToken");
                } else {
                    logger.info("登录失败:" + msg);
                }
            } else {
                //非json数据->可能被墙或者读取乱码
                logger.info("登录失败:" + res.getCode());
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
        }
        //根据token获取userid
        try {
            Response res = RequestApi.getUserInfo(token, proxy);
            String msg = res.getMsg();
//            logger.info(msg);
            if (JsonUtils.isValidJson(msg)) {
                //json数据->返回格式正确
                JSONObject json = JSON.parseObject(msg);
                if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                    uid = json.getJSONObject("data").getString("uid");
                } else {
                    logger.info("登录失败:" + msg);
                }
            } else {
                //非json数据->可能被墙或者读取乱码
                logger.info("登录失败:" + res.getCode());
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
        }


        if (code != null && token != null && uid != null) {
            User user = new User();
            user.setToken(token);
            user.setUid(uid);
            return user;
        }
        return null;
    }

//    public static void main(String[] args) {
//        User user = UserService.getInstance().handlerLogin("1", "1", null);
//        System.out.println(user);
//    }
}
