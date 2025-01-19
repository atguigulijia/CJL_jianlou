package com.itguigu.util;

import com.itguigu.model.GlobalProperties;
import com.itguigu.model.User;
import com.itguigu.service.PurchaseService;
import javafx.collections.ObservableList;

import java.lang.reflect.Field;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/5/18 20:39
 **/
public class UsertListUtil {
    private static PurchaseService purchaseService = PurchaseService.getInstance();

    /**
     * 更新用户属性值
     * @param target
     * @param source
     */
    private static void updateUserProperties(User target, User source) {
        // 使用反射获取User类的所有字段
        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                // 将source对象的字段值复制到target对象中
                field.set(target, field.get(source));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 插入用户(不重复 根据acc是否equals)
     * @param userList
     * @param newUser
     */
    public static void insertUser(ObservableList<User> userList, User newUser) {
        //查询是否重复(根据acc属性equals判断)
        int index = -1;
        for (int i = 0; i < userList.size(); i++) {
            User existingUser = userList.get(i);
            if (existingUser.getAcc().equals(newUser.getAcc())) {
                index = i;
                break;
            }
        }

        //老用户
        if (index != -1) {
//            userList.set(index, newUser); // 替换现有用户
            //更新用户原有属性值
            updateUserProperties(userList.get(index),newUser);

        } else {
            userList.add(newUser); // 直接插入新用户
            //如果程序正在运行，新用户则添加任务
            if (GlobalProperties.isRun){
                purchaseService.addPurchaseTask(newUser);
            }
        }
    }
}
