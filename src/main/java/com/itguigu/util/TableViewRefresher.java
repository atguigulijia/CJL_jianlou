package com.itguigu.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/15 17:49
 **/
public class TableViewRefresher {
    private static Timeline timeline;

    public static synchronized void startRefreshTimer(TableView<?> tableView) {
        // 创建定时器，每隔一段时间刷新TableView
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.millis(55), event -> tableView.refresh()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public static synchronized void stopRefreshTimer() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
}
