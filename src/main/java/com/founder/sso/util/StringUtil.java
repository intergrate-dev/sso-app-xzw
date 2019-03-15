package com.founder.sso.util;

import java.util.Random;

public class StringUtil {
	
    /**
     * 得到length位数的数字随机数
     * @param length
     */
    public static String getRandom(int length){
    	Random r = new Random(); 
    	Double d = r.nextDouble(); 
    	String s = d + ""; 
    	s=s.substring(3,3+length); 
    	return s;
    }

}
