package com.itguigu.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.itguigu.model.*;
import com.itguigu.task.TaskManager;
import com.itguigu.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PurchaseService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    // 单例实例，使用 volatile 确保线程安全
    private static volatile PurchaseService instance = null;

    //任务管理对象
    private static TaskManager taskManager = TaskManager.getInstance();

    // 私有构造函数，外部无法直接实例化
    private PurchaseService() {

    }

    // 获取单例实例的公共方法，使用双重检查锁实现懒加载
    public static PurchaseService getInstance() {
        if (instance == null) {
            synchronized (PurchaseService.class) {
                if (instance == null) {
                    instance = new PurchaseService();
                }
            }
        }
        return instance;
    }

    // 启动
    public void run(List<User> userList) {
        // 启动代理
        GlobalProxyThread.getInstance().start();
        for (User user : userList) {
            this.addPurchaseTask(user);
        }
    }

    // 停止
    public void stop() {
        taskManager.shutdown();
    }


    //添加捡漏任务(定时执行器的任务)
    public void addPurchaseTask(User user) {
        // 添加总任务到任务管理器，使用 taskName 标识任务类型
        taskManager.addTaskWithTiming(user.getAcc() + "_jianlou1Task", () -> {
            //任务已经完成
            if (user.getSuccessfulCount().get() >= user.getInputCount()) {
                logger.info("任务已经完成");
                user.setResult("任务已经完成");
                taskManager.stopTasksByIdPrefix(user.getAcc());
                return;
            }
            //任务未完成
            //1.刷新市场藏品列表
            try {
                Response res = RequestApi.getGoodsInfoListByGoodsId(user.getGoodsCategoryInfo().getGoodsId(), GlobalProperties.globalProxy);
                String msg = res.getMsg();
                logger.info(msg);
                if (isInvalidMessage(msg)) {
                    return;
                }
                JSONObject json = JSON.parseObject(msg);
                if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                    //获取市场藏品列表
                    List<GoodsInfo> goodsInfos = json.getJSONObject("data").getJSONArray("items").toJavaList(GoodsInfo.class);
                    //列表为空
                    if (goodsInfos == null || goodsInfos.size() == 0) {
//                        LogUtil.println("当前藏品\"" + user.getGoodsCategoryInfo().getGoodsName() + "\"市场列表为空");
                        user.setResult("当前藏品\"" + user.getGoodsCategoryInfo().getGoodsName() + "\"市场列表为空");
                        return;
                    }
                    //2.获取第一个藏品，服务器默认按价格升序
                    GoodsInfo goodsInfo = goodsInfos.get(0);
                    //3.下单
                    try {
                        Response res1 = RequestApi.createOrder(user, goodsInfo.getPrice(), goodsInfo.getGoodsId(), GlobalProperties.globalProxy);
                        String msg1 = res1.getMsg();
                        logger.info(msg1);
                        if (isInvalidMessage(msg1)) {
                            return;
                        }
                        JSONObject json1 = JSON.parseObject(msg1);
                        if (json1 != null && json1.containsKey("status") && "200".equals(json1.getString("status"))) {
                            //获取orderid
                            String orderId = json1.getString("data");
                            if (GlobalProperties.useEmailPush) {
                                ThirdMsgUtil.qiruiEV(user.getAcc());
                                EmailPusherUtils.push(user.getEmail(), PlatformInfo.name + PlatformInfo.use, msg1);
                            }
                            LogUtil.println("锁单成功，订单id为" + orderId);
                            user.setResult("锁单成功，订单id为" + orderId);
                            user.getSuccessfulCount().incrementAndGet();
                        } else {
                            logger.info("锁单失败:" + msg1);
                            user.setResult("锁单失败:" + msg1);
                        }
                    } catch (Exception e) {
                        logger.error("锁单异常:", e);
                        user.setResult("锁单异常" + e.getMessage());
                        return;
                    }
                } else {
                    logger.info("获取市场藏品列表失败:" + msg);
                    user.setResult("获取市场藏品列表失败:" + msg);
                }
            } catch (Exception e) {
                logger.error("获取市场藏品列表异常:", e);
                user.setResult("获取市场藏品列表异常:" + e.getMessage());
                return;
            }

        }, 0, GlobalProperties.requestDelayTime);
    }

    //无效信息
    private boolean isInvalidMessage(String msg) {
        if (msg == null || msg.isEmpty()) {
            logger.info("服务器响应体为空");
            return true;
        } else {
            return false;
        }
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
            logger.info(msg);
            if (isInvalidMessage(msg)) {
                return null;
            }
            JSONObject json = JSON.parseObject(msg);
            if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                code = json.getJSONObject("data").getString("code");
            } else {
                logger.info("登录失败:" + msg);
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
            return null;
        }
        //根据code获取token
        try {
            Response res = RequestApi.login1(code, proxy);
            String msg = res.getMsg();
            logger.info(msg);
            if (isInvalidMessage(msg)) {
                return null;
            }
            JSONObject json = JSON.parseObject(msg);
            if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                token = "bearer " + json.getJSONObject("data").getString("accessToken");
            } else {
                logger.info("登录失败:" + msg);
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
            return null;
        }
        //根据token获取userid
        try {
            Response res = RequestApi.getUserInfo(token, proxy);
            String msg = res.getMsg();
            logger.info(msg);
            if (isInvalidMessage(msg)) {
                return null;
            }
            JSONObject json = JSON.parseObject(msg);
            if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                uid = json.getJSONObject("data").getString("uid");
            } else {
                logger.info("登录失败:" + msg);
            }
        } catch (Exception e) {
            logger.error("登录异常:", e);
            return null;
        }

        User user = new User();
        user.setToken(token);
        user.setUid(uid);
        return user;

    }


    /**
     * 抓取市场藏品
     *
     * @param proxy
     * @return
     */
    public List<GoodsCategoryInfo> getGoodsList(Proxy proxy) {
        //记录categoryid集合
        List<GoodsCategoryInfo> goodsCategoryInfoList = new ArrayList<>();
        //首先获取类别集合
        try {
            Response res = RequestApi.getGoodsCategoryIdList(proxy);
            String msg = res.getMsg();
            logger.info(msg);
            if (isInvalidMessage(msg)) {
                return null;
            }
            JSONObject json = JSON.parseObject(msg);
            if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                goodsCategoryInfoList = json.getJSONObject("data").getJSONArray("value").toJavaList(GoodsCategoryInfo.class);
                logger.info(goodsCategoryInfoList.toString());
            } else {
                logger.info("获取藏品类别id集合失败:" + msg);
            }
        } catch (Exception e) {
            logger.error("获取藏品类别id集合异常:", e);
            return null;
        }
        //再根据类别id获取对应的藏品集合
        List<GoodsCategoryInfo> temp = new ArrayList<>();
        try {
            for (int i = 0; i < goodsCategoryInfoList.size(); i++) {
                String categoryId = goodsCategoryInfoList.get(i).getCategoryId();
                String categoryName = goodsCategoryInfoList.get(i).getCategoryName();
                //获取page1,2页的数据
                for (int i1 = 1; i1 < 3; i1++) {
                    Response res = RequestApi.getGoodsCategoryListByCategoryId(categoryId, i1 + "", proxy);
                    String msg = res.getMsg();
                    if (isInvalidMessage(msg)) {
                        return null;
                    }
                    JSONObject json = JSON.parseObject(msg);
                    if (json != null && json.containsKey("status") && "200".equals(json.getString("status"))) {
                        List<GoodsCategoryInfo> items = json.getJSONObject("data").getJSONArray("items").toJavaList(GoodsCategoryInfo.class);
                        items.stream().map(item -> {
                            item.setCategoryId(categoryId);
                            item.setCategoryName(categoryName);
                            return item;
                        }).collect(Collectors.toList());
                        logger.info(items.toString());
                        temp.addAll(items);
                    } else {
                        logger.info("获取藏品类别集合失败:" + msg);
                    }
                    Thread.sleep(666);
                }
            }
        } catch (Exception e) {
            logger.error("获取藏品类别集合异常:", e);
            return null;
        }
        return temp;
    }
}
