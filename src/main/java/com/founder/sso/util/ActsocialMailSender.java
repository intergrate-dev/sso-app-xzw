package com.founder.sso.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;

/**
 * 发邮件的类
 */
public class ActsocialMailSender {
    //从配置文件中读取相应的邮件配置属性
	public static String emailHost = null;
	public static String userName = null;
	public static String password = null;
    private static Map<String, Object> proMap = null;
    
    private static final String mailAuth = "true";
    private static JavaMailSenderImpl instance = null;
    private static VelocityEngine velocityEngine = null;

    static {
        proMap = new HashMap<String, Object>();
        proMap.put("resource.loader", "class");
        proMap.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    public static JavaMailSender getInstance() {
        if (null == instance) {
            synchronized (JavaMailSenderImpl.class) {
                if (null == instance) {
                    instance = new JavaMailSenderImpl();
                    instance.setHost(emailHost);
                    instance.setUsername(userName);
                    instance.setPassword(password);
                    Properties properties = new Properties();
                    properties.setProperty("mail.smtp.auth", mailAuth);
                   
                  /*  properties.setProperty("proxySet", "true");
                    properties.setProperty("socksProxyHost", "isasrv");
                    properties.setProperty("socksProxyPort","80");*/
                    
                    //使用gmail或qq发送邮件是必须设置如下参数的 主要是port不一样
                    if (emailHost.indexOf("smtp.gmail.com")>=0 || emailHost.indexOf("smtp.qq.com")>=0) {
                        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
                        properties.setProperty("mail.smtp.port", "465");
                        properties.setProperty("mail.smtp.socketFactory.port", "465");
                    }
                    instance.setJavaMailProperties(properties);
                }
            }
        }

        return instance;
    }

    public static VelocityEngine getVelocityEngineInstance() {
        if (null == velocityEngine) {
            synchronized (VelocityEngine.class) {
                if (null == velocityEngine) {
                    velocityEngine = new VelocityEngine();
                    for (Map.Entry<String, Object> entry : proMap.entrySet()) {
                        velocityEngine.setProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return velocityEngine;
    }

    /*发送邮件服务*/
    public static void sendMail(String email, String sendCode, String useType) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", sendCode);
        String sendContent = VelocityEngineUtils.mergeTemplateIntoString(
                ActsocialMailSender.getVelocityEngineInstance(), "templates/sendCode.html", "UTF-8", model);

        String mailSSLPort = "465";
        Properties props = new Properties();
        props.put("mail.smtp.host", ActsocialMailSender.emailHost);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.port", mailSSLPort);
        props.put("mail.smtp.port", mailSSLPort);

        String senSubject = useType.equals("0") ? "星洲网注册验证码" : "星洲网找回密码验证码";
        //String sendContent = "您好！您正在星洲网本次操作的验证码为：" + sendCode + ", 10分钟内有效，请不要泄露给他人。";
        System.out.println("============= ActsocialMailSender sendMail, time: " + System.currentTimeMillis() + ", sendContent: " + sendContent + ", email: " + email);
        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ActsocialMailSender.userName));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(senSubject);
        message.setContent(sendContent, "text/html;charset=gbk");
        message.setSentDate(new Date());
        message.saveChanges();
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect(ActsocialMailSender.userName, ActsocialMailSender.password);
            System.out.println("============= ActsocialMailSender sendMail start, time: " + System.currentTimeMillis() + ", email: " + email);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
        System.out.println("============= ActsocialMailSender sendMail end, time: " + System.currentTimeMillis() + ", email: " + email);
    }

    public static void sendMailByGmail(String email, String sendCode, String useType) throws Exception {
        System.out.println("============= ActsocialMailSender sendMail, sendCode: " + sendCode + ", email: " + email);
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        /*props.setProperty("mail.smtp.host", "smtp.gmail.com");*/
        props.put("mail.smtp.host", ActsocialMailSender.emailHost);
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");

        /*final String username = "zhaoyucai2018";
        final String password = "012580zyc";*/
        /*final String username = "foundermaojs";
        final String password = "Founder123";*/

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ActsocialMailSender.userName, ActsocialMailSender.password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(ActsocialMailSender.userName + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email, false));

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("code", sendCode);
        /*String sendContent = VelocityEngineUtils.mergeTemplateIntoString(
                ActsocialMailSender.getVelocityEngineInstance(), "templates/sendCode.html", "UTF-8", model);*/
        String sendContent = "您好！您正在星洲网本次操作的验证码为：" + sendCode + ", 10分钟内有效，请不要泄露给他人。";
        String sendSubject = useType.equals("0") ? "星洲网注册验证码" : "星洲网找回密码验证码";
        System.out.println("============= ActsocialMailSender sendMail, time: " + System.currentTimeMillis() + ", sendContent: " + sendContent + ", email: " + email);

        msg.setSubject(sendSubject);
        msg.setText(sendContent);
        msg.setSentDate(new Date());
        System.out.println("============= ActsocialMailSender sendMail start, time: " + System.currentTimeMillis() + ", email: " + email);
        Transport.send(msg);
        System.out.println("============= ActsocialMailSender sendMail end, time: " + System.currentTimeMillis()  + ", email: " + email);

    }
}
