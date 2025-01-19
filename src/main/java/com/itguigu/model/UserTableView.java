package com.itguigu.model;

import com.itguigu.util.DeleteButtonCellFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.Field;

public class UserTableView {
    /**
     * 初始化首行的每一列
     *
     * @param userTableView
     */
    public static void initTableView(TableView<User> userTableView) {

        // 设置列自适应策略
        userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 创建属性列，每个属性对应一列
        TableColumn<User, String> accColumn = createColumn("acc", "账户");
        TableColumn<User, String> tokenColumn = createColumn("token", "Token");
        TableColumn<User, String> emailColumn = createColumn("email", "邮箱");

        TableColumn<User, Integer> successfulCountColumn = createColumn("successfulCount", "成功次数");

        TableColumn<User, String> resultColumn = createColumn("result", "状态");
        resultColumn.setMinWidth(230); // 设置“状态”列的最小宽度，使其更宽

        TableColumn<User, Void> actionColumn = new TableColumn<>("操作");
        actionColumn.setCellFactory(new DeleteButtonCellFactory<>());

        // 将属性列添加到TableView
        userTableView.getColumns().addAll(accColumn, tokenColumn, emailColumn, successfulCountColumn, resultColumn, actionColumn);
    }

    // 创建属性列的通用方法
    public static <S, T> TableColumn<S, T> createColumn(String property, String columnName) {
        TableColumn<S, T> column = new TableColumn<>(columnName);
        column.setCellValueFactory(cellData -> {
            try {
                Field field = cellData.getValue().getClass().getDeclaredField(property);
                field.setAccessible(true);
                T value = (T) field.get(cellData.getValue());
                return new SimpleObjectProperty<>(value);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
        return column;
    }
}
