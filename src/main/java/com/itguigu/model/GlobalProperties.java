package com.itguigu.model;

import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/9 20:14
 * 全局参数设置
 **/
public class GlobalProperties {
    public static boolean isRun = false;//是否在运行

    public static List<GoodsCategoryInfo> goodsCategoryInfos;//市场藏品

    public static Long requestDelayTime = 300L;//请求延迟默认300mm

    public static Long requestAheadTime = 0L; //抢购提前时间

    public static Proxy globalProxy;//全局代理

    public static String globalProxyUrl;//全局代理链接

    public static boolean useProxy = false;//是否使用代理

    public static boolean useEmailPush = false;//是否开启邮箱推送

    public static final String filePath = System.getProperty("user.dir") + "\\file\\"; //文件存放路径前缀

    static {
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
