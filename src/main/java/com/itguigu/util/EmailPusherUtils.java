package com.itguigu.util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lijia
 * @create 2023-03-15 11:14
 * 邮箱推送消息
 */
public class EmailPusherUtils {
    private static final Logger logger = LoggerFactory.getLogger(EmailPusherUtils.class);



    public static void push(String yEmail, String title, String content) {
        if (yEmail == null || "".equals(yEmail)) {
            LogUtil.println("yEmail参数为空");
            return;
        }
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.qq.com");
            email.setAuthentication("1295905922@qq.com", "wgetpcmjfxiyhbah");
            email.setFrom("1295905922@qq.com", "IKUN");
            // 使用 SSL 端口 465
            email.setSmtpPort(465);
            email.setSSLOnConnect(true);
            email.setTextMsg(content);
            email.setCharset("UTF-8");
            email.setSubject(title);
            email.addTo(yEmail);
            email.send();
            logger.info("发送邮件成功");
        } catch (EmailException e) {
            logger.error("邮箱发送失败:"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        push("1295905922@qq.com","test","content");
    }



}
