package com.itguigu.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class LogUtil {
    private static TextArea logTextArea; // TextArea组件
    private static double savedScrollTop = 0; // 保存的滚动位置
    private static boolean autoScrollEnabled = false; // 默认启用自动滚动

    public static void setLogTextArea(TextArea textArea) {
        logTextArea = textArea;
        // 添加文本内容改变监听器，记录滚动位置
        logTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!autoScrollEnabled) {
                savedScrollTop = logTextArea.getScrollTop(); // 仅在禁用自动滚动时记录滚动位置
            }
        });
    }

    // 启用或禁用自动滚动
    public static void setAutoScrollEnabled(boolean enabled) {
        autoScrollEnabled = enabled;
        if (autoScrollEnabled) {
            savedScrollTop = Double.MAX_VALUE; // 启用自动滚动时，始终滚动到底部
        }
    }

    public static void print(String msg) {
        Platform.runLater(() -> {
            if (logTextArea != null) {
                logTextArea.appendText(TimeUtils.getCurrentDateTime() + ": " + msg);
                updateScrollPosition(); // 更新滚动位置
            }
        });
    }

    public static void println(String msg) {
        Platform.runLater(() -> {
            if (logTextArea != null) {
                logTextArea.appendText(TimeUtils.getCurrentDateTime() + ": " + msg + "\n");
                updateScrollPosition(); // 更新滚动位置
            }
        });
    }

    private static void updateScrollPosition() {
        if (autoScrollEnabled) {
            logTextArea.setScrollTop(Double.MAX_VALUE); // 自动滚动到底部
        } else {
            logTextArea.setScrollTop(savedScrollTop); // 恢复到原来的滚动位置
        }
    }

    public static void clear() {
        if (logTextArea != null) {
            logTextArea.clear();
        }
    }
}
