package com.itguigu.util;

import com.itguigu.model.GlobalProperties;

/**
 * @Author: lijia
 * @Description: GlobalProxyThread的单例模式实现
 * @CreateTime: 2024/3/14 20:47
 **/
public class GlobalProxyThread extends Thread {
    // 保证唯一性的锁对象
    private static final Object lock = new Object(); // 保持为 private

    private static volatile GlobalProxyThread instance;

    // 私有化构造方法，避免外部实例化
    private GlobalProxyThread() {
    }

    // 提供公共的获取实例方法（懒汉式单例）
    public static GlobalProxyThread getInstance() {
        if (instance == null) {
            synchronized (GlobalProxyThread.class) {
                if (instance == null) {
                    instance = new GlobalProxyThread();
                }
            }
        }
        return instance;
    }

    // 提供一个公有方法让外部线程可以唤醒等待的线程
    public static void notifyAllWaitingThreads() {
        synchronized (lock) {
            lock.notifyAll();  // 唤醒所有等待的线程
        }
    }

    @Override
    public void run() {
        while (GlobalProperties.isRun) {
            synchronized (lock) {
                try {
                    // 等待条件满足
                    while (!GlobalProperties.useProxy) {
                        lock.wait(); // 条件不满足时，线程等待
                    }
                    // 条件满足时，执行提取代理IP的操作
                    GlobalProperties.globalProxy = ProxyUtils.getProxy(GlobalProperties.globalProxyUrl);
                    // 代理IP提取逻辑
                    Thread.sleep(1555L); // 睡眠一定时间后继续执行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 恢复中断状态
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
