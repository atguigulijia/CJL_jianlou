package com.itguigu.controller;

import com.itguigu.model.*;
import com.itguigu.service.PurchaseService;
import com.itguigu.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/12 20:26
 **/
public class Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    //保证唯一对象
    private final PurchaseService purchaseService = PurchaseService.getInstance();

    @FXML
    private TextArea logTextArea;

    @FXML
    private ScrollBar scrollBar;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private ComboBox<GoodsCategoryInfo> goodsListCombox;


    //用户集合
    ObservableList<User> userList;

    ObservableList<GoodsCategoryInfo> goodsCategoryInfos;

    @FXML
    private CheckBox emailPushCheckBox;

    @FXML
    private TextField accTextField;
    @FXML
    private TextField pwdTextField;

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField inputCountTextField;
    @FXML
    private CheckBox proxyCheckBox;
    @FXML
    private TextField proxyTextField;

    @FXML
    private TextField requestDelayTimeTextField;


    @FXML
    private Button inputUserDataButton;

    @FXML
    private Button outputUserDataButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //绑定日志对象
        LogUtil.setLogTextArea(logTextArea);
        // 初始化用户列表数据
        userList = FXCollections.observableArrayList();
        // 将用户列表数据绑定到ListView
        userTableView.setItems(userList);
        //初始化userTableView
        UserTableView.initTableView(userTableView);
    }


    //用户登录
    public void userLogin(ActionEvent actionEvent) {
        //参数校验
        String acc = accTextField.getText();
        String pwd = pwdTextField.getText();
        String email = emailTextField.getText();
        String inputCount = inputCountTextField.getText();

        GoodsCategoryInfo goodsCategoryInfo = goodsListCombox.getValue();
        if (goodsCategoryInfo == null) {
            LogUtil.println("请选择你要捡漏的藏品");
            return;
        }

        User user = purchaseService.handlerLogin(acc, pwd, GlobalProperties.globalProxy);

        if (user != null) {
            user.setGoodsCategoryInfo(goodsCategoryInfo);
            user.setAcc(acc);
            user.setEmail(email);
            user.setInputCount(Integer.parseInt(inputCount));
            UsertListUtil.insertUser(userList, user);
            LogUtil.println(acc + "登录成功");
            logger.info(user.toString());
        }

    }

    //抓取藏品列表
    public void catchGoodsList(ActionEvent actionEvent) {
        List<GoodsCategoryInfo> goodsList = purchaseService.getGoodsList(GlobalProperties.globalProxy);
        //失败
        if (goodsList == null) {
            LogUtil.println("获取市场藏品集合失败");
            //尝试读取本地文件
            try {
                LogUtil.println("尝试读取本地文件");
                goodsList = CSVUtils.readFromCSV(GoodsCategoryInfo.class, GlobalProperties.filePath + "goodsCategoryInfo.csv", "\t");
                LogUtil.println("读取成功");
                this.goodsCategoryInfos = FXCollections.observableArrayList(goodsList);
                goodsListCombox.setItems(this.goodsCategoryInfos);
            } catch (Exception e) {
                LogUtil.println("尝试读取本地文件失败:" + e.getMessage());
            }
            return;
        }
        //写入数据保存到本地
        try {
            CSVUtils.writeToCSV(GoodsCategoryInfo.class, goodsList, GlobalProperties.filePath + "goodsCategoryInfo.csv", "\t");
            logger.info("数据保存成功");
        } catch (Exception e) {
            logger.error("数据保存失败:" + e.getMessage());
        }
        this.goodsCategoryInfos = FXCollections.observableArrayList(goodsList);
        goodsListCombox.setItems(this.goodsCategoryInfos);
        LogUtil.println("获取市场藏品集合成功");
    }


    //修改邮箱推送状态
    public void changeEmailPushStatus(ActionEvent actionEvent) {
        GlobalProperties.useEmailPush = emailPushCheckBox.isSelected();
        LogUtil.println("开启邮箱推送:" + GlobalProperties.useEmailPush);
    }

    //修改代理状态
    public void changeProxyStatus(ActionEvent actionEvent) {
        GlobalProperties.useProxy = proxyCheckBox.isSelected();
        GlobalProperties.globalProxyUrl = proxyTextField.getText();
        LogUtil.println("开启代理:" + GlobalProperties.useProxy);
        //测试代理是否有效
        if (GlobalProperties.useProxy) {
            GlobalProperties.globalProxy = ProxyUtils.getProxy(GlobalProperties.globalProxyUrl);
            GlobalProxyThread.notifyAllWaitingThreads();
        }
    }


    //修改请求间隔
    public void changeRequestDelayTime(ActionEvent actionEvent) {
//        LogUtil.println(requestDelayTimeTextField.getText());
        if ("".equals(requestDelayTimeTextField.getText())) {
            GlobalProperties.requestDelayTime = 300L;
            LogUtil.println("当前请求间隔为:" + GlobalProperties.requestDelayTime + "毫秒");
            return;
        }
        try {
            GlobalProperties.requestDelayTime = Long.parseLong(requestDelayTimeTextField.getText());
            LogUtil.println("当前请求间隔为:" + GlobalProperties.requestDelayTime + "毫秒");
        } catch (NumberFormatException e) {
            DialogUtil.showAlert("无效的字符串:" + requestDelayTimeTextField.getText());
        }
    }


    //导出用户数据
    public void outputUserData(ActionEvent actionEvent) {
        try {
            if (userList == null || userList.size() == 0) {
                DialogUtil.showAlert("用户列表为空，无数据导出");
                return;
            }
            CSVUtils.writeToCSV(User.class, userList, GlobalProperties.filePath + "userData.csv", "----", "uid", "acc", "token", "email", "inputCount", "goodsCategoryInfo");
            LogUtil.println("导出成功-userData.csv");
        } catch (IOException | ReflectiveOperationException e) {
            LogUtil.println("导出失败-userData.csv," + e.getMessage());
        }
    }

    //导入用户数据
    public void inputUserData(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File selectedFile = fileChooser.showOpenDialog(inputUserDataButton.getScene().getWindow());
        //取消文件导入
        if (selectedFile == null) return;
        try {
            String selectedFilePath = selectedFile.getAbsolutePath();
            // 在这里处理选中的文件
            LogUtil.println("Selected file: " + selectedFile.getAbsolutePath());
            //判断文件是否合法
            boolean validCSVFile = CSVUtils.isValidCSVFile(selectedFilePath);
            if (validCSVFile && selectedFile.length() != 0) {
                List<User> users = CSVUtils.readFromCSV(User.class, selectedFile.getAbsolutePath(), "----");
                //非空判断
                if (users != null && users.size() != 0) {
                    for (User user : users) {
                        //插入用户数据
                        UsertListUtil.insertUser(userList, user);
                    }
                }
                LogUtil.println("导入成功");
            } else {
                LogUtil.println("导入文件不合法");
                DialogUtil.showAlert("导入文件不合法");
            }
        } catch (IOException | ReflectiveOperationException e) {
            LogUtil.println("导入文件失败");
        }
    }

    //导出抢购成功数据
    public void outputSuccessData(ActionEvent actionEvent) {
        try {
            //查询用户成功数据
            List<User> successfulData = userList.stream()
                    .filter(item -> item.getSuccessfulCount().get() > 0)
                    .collect(Collectors.toList());
//            successfulData.forEach(System.out::println);
            //写入文件
            CSVUtils.writeToCSV(User.class, successfulData, GlobalProperties.filePath + "successfulData.csv", "----", "acc", "goodsCategoryInfo");
            LogUtil.println("导出成功-successfulData.csv");
        } catch (IOException | ReflectiveOperationException e) {
            logger.error(e.getMessage());
            LogUtil.println("导出失败-successfulData.csv");
        }
    }

    //终止运行
    public void stopRun(ActionEvent actionEvent) {
        if (GlobalProperties.isRun) {
            LogUtil.println("程序终止");
            GlobalProperties.isRun = false;
            purchaseService.stop();
        }

    }

    //启动程序
    public void startRun(ActionEvent actionEvent) {
        if (userList == null || userList.size() == 0) {
            LogUtil.println("请先完成用户登录");
            return;
        }


        //未启动
        if (!GlobalProperties.isRun) {
            GlobalProperties.isRun = true;//已经启动
            //启动定时器
            TableViewRefresher.startRefreshTimer(userTableView);
            LogUtil.println("程序启动");
            purchaseService.run(userList);
        } else {
            LogUtil.println("请不要重复启动，会导致线程池异常");
        }
    }

    //清空日志
    public void clearLog(ActionEvent actionEvent) {
        LogUtil.clear();

    }

}
