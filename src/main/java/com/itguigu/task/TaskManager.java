package com.itguigu.task;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TaskManager {
    //taskName 格式 = "13397497249_buyTask"
    private final ScheduledExecutorService schedulerService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> userTasks;

    // 单例实例，使用 volatile 确保线程安全
    private static volatile TaskManager instance = null;

    // 固定的核心线程数
    private static final int CORE_POOL_SIZE = 50;

    // 私有构造函数，外部无法直接实例化
    private TaskManager() {
        // 使用固定大小的线程池来调度任务
        this.schedulerService = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
        this.userTasks = new ConcurrentHashMap<>();
    }

    // 获取单例实例的公共方法，使用双重检查锁实现懒加载
    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    // 周期性任务调度，重复提交时会取消已有的任务并重新创建任务
    public void addTaskWithTiming(String taskName, Runnable runTask, long initialDelay, long period) {
        stopTasksByIdPrefix(taskName);
        ScheduledFuture<?> future = schedulerService.scheduleAtFixedRate(runTask, initialDelay, period, TimeUnit.MILLISECONDS);
        userTasks.put(taskName, future);
    }

    // 立即执行的单次任务，重复提交时会取消已有的任务并重新创建任务
    public void addTask(String taskName, Runnable runTask) {
//        cancelExistingTask(taskName);
        ScheduledFuture<?> future = schedulerService.schedule(runTask, 0, TimeUnit.MILLISECONDS);
        userTasks.put(taskName, future);
    }

    // 停止特定任务
    public void stopTask(String taskName) {
        ScheduledFuture<?> future = userTasks.remove(taskName);
        if (future != null) {
            future.cancel(true);
        }
    }

    // 根据 taskId 前缀取消任务
    public void stopTasksByIdPrefix(String taskIdPrefix) {
        List<String> matchingTasks = getTasksByIdPrefix(taskIdPrefix);
        for (String taskName : matchingTasks) {
            stopTask(taskName);
        }
    }

    // 以 taskId 前缀查询任务列表
    public List<String> getTasksByIdPrefix(String taskIdPrefix) {
        return userTasks.keySet().stream()
                .filter(taskName -> taskName.startsWith(taskIdPrefix))
                .collect(Collectors.toList());
    }

    // 获取所有任务列表
    public List<String> getAllTasks() {
        return userTasks.keySet().stream().collect(Collectors.toList());
    }

    // 判断任务是否存在
    public boolean isTaskExists(String taskName) {
        return userTasks.containsKey(taskName);
    }

    // 停止所有任务
    public void shutdown() {
        schedulerService.shutdownNow();
    }

}
