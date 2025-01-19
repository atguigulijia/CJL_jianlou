package com.itguigu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: lijia
 * @Description: 日期时间戳转换工具
 * @CreateTime: 2023/4/4 19:53
 **/
public class TimeUtils {
    public static String getCurrentDateTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
        return currentTime.format(formatter);
    }

    /**
     * @param timeText
     * @return
     */
    public static Long convertTimeStamp(String timeText){
        try {
            if (timeText == null && timeText.equals("")){
                return null;
            }
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = sdf.parse(timeText);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertTimeText(Long timeStamp){
        if (timeStamp==null || timeStamp <0){
            return null;
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date(timeStamp));
        return format;
    }

}
