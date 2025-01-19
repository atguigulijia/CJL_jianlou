package com.itguigu.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2023/9/14 18:47
 **/
public class DialogUtil {
    public static void showAlert( String contentText) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("我爱抽玉溪");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
