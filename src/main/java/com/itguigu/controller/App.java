package com.itguigu.controller;

import com.itguigu.model.PlatformInfo;
import com.itguigu.util.LogUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/12 20:28
 **/
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/itguigu/view/screen.fxml"));
            primaryStage.setTitle(PlatformInfo.name + PlatformInfo.use + PlatformInfo.version);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/dog.png")));
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(event -> {
                // 在窗口关闭时执行需要的操作（如保存数据等）
                LogUtil.println("Closing the application");
                // 关闭程序
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            LogUtil.println(e.getMessage());
        }
    }
}
