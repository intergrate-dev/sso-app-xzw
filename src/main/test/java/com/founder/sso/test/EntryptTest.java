package com.founder.sso.test;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by yuan-pc on 2018/11/8.
 */
public class EntryptTest {
    public static final String DEFAULT_HASH_ALGORITHM = "SHA-1";

    public static void main(String[] args) {
        public static String entryptPassword(String plainPwd, String salt) {
            //return entryptPassword(plainPwd, salt, DEFAULT_HASH_ALGORITHM, DEFAULT_HASH_ITERATIONS);
            String ss = entryptPassword(plainPwd, salt, DEFAULT_HASH_ALGORITHM, 2);
            System.out.println("ss: " + ss);
        }

        entryptPassword("111qqq", "44035792c271a9f3")
    }
}
