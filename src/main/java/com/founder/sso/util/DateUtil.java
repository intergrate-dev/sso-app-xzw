package com.founder.sso.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	public static final String default_parttern="yyyy-MM-dd HH:mm:ss";
	
	public static final String default_date_parttern="yyyy-MM-dd";
	
	/**
	 * 得到yyyy-MM-dd HH:mm:ss字符串
	 */
	public static String getTimeStr(Date date){
		return getDateStr(date, default_parttern);
	}
	
	/**
	 * 得到yyyy-MM-dd字符串
	 */
	public static String getDateStr(Date date){
		return getDateStr(date, default_date_parttern);
	}
	/**
	 * 得到指定格式的日期字符串
	 */
	public static String getDateStr(Date date, String parttern){
		SimpleDateFormat sf = new SimpleDateFormat(parttern);
		return sf.format(date);
	}
	
	
	 /**
     * 得到时间的字符串yyyyMMdd
     */
    public static String getCurDate() {
		GregorianCalendar gcDate = new GregorianCalendar();
		int year = gcDate.get(GregorianCalendar.YEAR);
		int month = gcDate.get(GregorianCalendar.MONTH)+1;
		//如果生成的月份或日期是个位数，需要在前面加0
		String monthStr = month<10 ? "0"+month : ""+month;
		int day = gcDate.get(GregorianCalendar.DAY_OF_MONTH);
		String dayStr = day<10 ? "0"+day : ""+day;
		return "" + year + monthStr + dayStr;
	}
    
}
