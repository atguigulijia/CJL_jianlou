package com.itguigu.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TaskManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private final ScheduledExecutorService schedulerService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> userTasks;

    // 单例实例，使用 volatile 确保线程安全
    private static volatile TaskManager instance = null;

    // 固定的核心线程数
    private static final int CORE_POOL_SIZE = 50;

    // 私有构造函数，外部无法直接实例化
    private TaskManager() {
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

    // 周期性任务调度
    public void addTaskWithTiming(String taskName, Runnable runTask, long initialDelay, long period) {
        // 确保任务唯一性
        ScheduledFuture<?> future = schedulerService.scheduleAtFixedRate(runTask, initialDelay, period, TimeUnit.MILLISECONDS);
        userTasks.putIfAbsent(taskName, future);
    }

    // 立即执行的单次任务
    public void addTask(String taskName, Runnable runTask) {
        ScheduledFuture<?> future = schedulerService.schedule(runTask, 0, TimeUnit.MILLISECONDS);
        userTasks.put(taskName, future);
    }

    // 停止特定任务
    public void stopTask(String taskName) {
        ScheduledFuture<?> future = userTasks.remove(taskName);
        if (future != null && !future.isCancelled()) {
            boolean wasCancelled = future.cancel(true);
            if (wasCancelled) {
                logger.info("Task " + taskName + " was successfully cancelled.");
            } else {
                logger.info("Failed to cancel task " + taskName);
            }
        } else {
            logger.info("Task " + taskName + " does not exist or is already cancelled.");
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

    // 停止所有任务，但保持线程池活跃
    //取消了所有调度任务，并且清空了 userTasks 中存储的任务列表。
    //线程池不被销毁，保持活跃状态，可以继续调度新的任务。
    public void shutdownTasksOnly() {
        logger.info("Cancelling all tasks...");
        // 取消所有调度的任务
        for (ScheduledFuture<?> future : userTasks.values()) {
            future.cancel(true);
        }
        userTasks.clear();  // 清除任务列表
    }

    // 强制关闭所有任务和线程池（如果需要关闭线程池）
    public void shutdown() {
        schedulerService.shutdownNow();
    }

    // 优雅地停止所有任务（保持线程池活跃）
    //调用 shutdownTasksOnly() 来停止任务，同时保持线程池活跃。
    //可以根据需要添加更多的优雅关闭逻辑，确保任务不会丢失，并且线程池可以继续工作。
    public void shutdownGracefully() {
        logger.info("Gracefully stopping all tasks...");
        shutdownTasksOnly();
        // 注意：线程池仍然保持活跃，可以继续调度新任务
    }
}
