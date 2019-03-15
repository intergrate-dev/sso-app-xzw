package com.founder.sso.test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by yuan-pc on 2018/11/8.
 */
public class EmailTest {
    /*发送邮件服务*/
    public static void sendMail(String mail_from, String mail_user, String mail_password, String toMail, String senSubject, String sendContent, String mail_host) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", mail_host);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        // 是否使用ssl加密端口
        String mailSSLPort = "465";
        String mailPort = "25";
        //if(WebConfig.mailSSL.equals("1")){
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.socketFactory.port", mailSSLPort);
            props.put("mail.smtp.port",mailSSLPort);
        //}else {
            //props.put("mail.smtp.port",mailPort);
        //}

        javax.mail.Session session = javax.mail.Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail_from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
        message.setSubject(senSubject);
        message.setContent(sendContent, "text/html;charset=gbk");
        message.setSentDate(new Date());
        message.saveChanges();
        try {
            Transport transport = session.getTransport();
            transport.connect(mail_user, mail_password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
    }

    public static void main(String[] args) {
        String mail_host = "smtp.163.com";
        String mail_user = "zm212896@163.com";
        String mail_password = "yuxin@212896*km";
        String mail_from = "zm212896@163.com";

        /*String mail_host = "smtp.163.com";
        String mail_user = "zhouyy0919@163.com";
        String mail_password = "zhouyyyx8";
        String mail_from = "zhouyy0919@163.com";*/


        // String email = "yuan.zk123@gmail.com";
        String email = "2491042435@qq.com";
        // String email = "yuan.zk@founder.com.cn";
        // String email = "yuanzk123@hotmail.com";
        // String email = "yuan_zk@sina.com";
        String sendCode = "xxx666";
        String sendSubject = "发送验证码";
        String sendContent = "测试邮件，发送的验证码：" + sendCode;
        try {
            sendMail(mail_from, mail_user, mail_password, email, sendSubject, sendContent, mail_host);
            System.out.println("send complete ...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (int i = 0; i < 100; i++) {
            Integer dig = (int)((Math.random()*9+1)*100000);
            System.out.println("i: " + i + " ====== dig: " + dig);
        }*/
    }
}
