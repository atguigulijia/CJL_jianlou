package com.itguigu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/9 19:19
 **/
public class User {

    //uid
    private String uid;
    //账号
    private String acc;
    //token
    private String token;
    //email
    private String email;
    //用户期望购买次数(默认1次)
    private Integer inputCount = 1;

    //用户需要捡漏的市场藏品信息
    private GoodsCategoryInfo goodsCategoryInfo;


    private String result = "等待执行";//响应信息

    private AtomicInteger successfulCount = new AtomicInteger(0); //成功次数

    //记录临时参数(包括请求参数和响应参数)
    private Map<String, Object> tempData = new ConcurrentHashMap<>();


    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", acc='" + acc + '\'' +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", inputCount=" + inputCount +
                ", goodsCategoryInfo=" + goodsCategoryInfo +
                ", result='" + result + '\'' +
                ", successfulCount=" + successfulCount +
                ", tempData=" + tempData +
                '}';
    }

    public GoodsCategoryInfo getGoodsCategoryInfo() {
        return goodsCategoryInfo;
    }

    public void setGoodsCategoryInfo(GoodsCategoryInfo goodsCategoryInfo) {
        this.goodsCategoryInfo = goodsCategoryInfo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getInputCount() {
        return inputCount;
    }

    public void setInputCount(Integer inputCount) {
        this.inputCount = inputCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public AtomicInteger getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(AtomicInteger successfulCount) {
        this.successfulCount = successfulCount;
    }

    public synchronized Map<String, Object> getTempData() {
        return tempData;
    }

    public synchronized void setTempData(Map<String, Object> tempData) {
        this.tempData = tempData;
    }

}
