package com.itguigu.util;

import com.itguigu.model.User;
import com.itguigu.task.TaskManager;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteButtonCellFactory<T> implements Callback<TableColumn<T, Void>, TableCell<T, Void>> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteButtonCellFactory.class);

    private final TaskManager taskManager = TaskManager.getInstance();

    @Override
    public TableCell<T, Void> call(TableColumn<T, Void> param) {
        return new TableCell<>() {
            private final Button deleteButton = new Button("删除");

            {
                deleteButton.setOnAction(event -> {
                    T item = getTableRow().getItem();
                    logger.info(item.toString());
                    if (item != null) {
                        getTableView().getItems().remove(item);
                        if (item instanceof User) {
                            User user = (User) item;
                            taskManager.stopTasksByIdPrefix(user.getAcc());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null); // 空行不显示按钮
                } else {
                    setGraphic(deleteButton); // 非空行显示按钮
                }
            }
        };
    }
}
