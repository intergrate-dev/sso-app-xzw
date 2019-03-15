package com.founder.sso.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

	public static final String EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	public static final String EMAIL_QQ = "^[0-9]{5,12}@[q]{2}\\.[com]{3}$";
	public static final String PHONE = "^0?(13[0-9]|15[012356789]|17[0-8]|18[0-9]|14[57])[\\d]{8}$";
	public static final String USERNAME = "^[a-zA-Z0-9_]{2,20}$";//以字母开头，3-20个字符，支持数字、大小写字母和下划线
	//public static final String NICKNAME = "^([a-zA-Z0-9\\u4e00-\\u9fa5_;:,?~!@#$%^&*\\-\\.\\s]*){2,20}$";//以字母开头，3-20个字符，支持数字、大小写字母和下划线
	public static final String NICKNAME = "(\\w*|\\W*|[\\u4e00-\\u9fa5]*)*";//以字母开头，3-20个字符，支持数字、大小写字母和下划线
	public static final String VALIDCODE = "^[0-9]{6}$";//以字母开头，3-20个字符，支持数字、大小写字母和下划线

	//检查用户名格式
	public static boolean CheckUsername(String username){
		String regular = USERNAME;
		return isValidate(regular, username);
	}

	//检查用户名格式
	public static boolean CheckNickName(String nickName){
		String regular = NICKNAME;
		return isValidate(regular, nickName);
	}

	//检查email格式
	public static boolean CheckEmail(String email){
		String regular = EMAIL;
		return isValidate(regular, email);
	}

	//检查email格式
	public static boolean CheckEmail_QQ(String email){
		String regular = EMAIL_QQ;
		return isValidate(regular, email);
	}

	//检查手机号格式
	public static boolean CheckPhone(String phone){
		String regular = PHONE;
		return isValidate(regular, phone);
	}
	//检查email 验证码格式
	public static boolean CheckValidCode(String emailCode){
		String regular = EMAIL;
		return isValidate(regular, emailCode);
	}
	private static boolean isValidate(String regular, String exp){
		if(exp == null || exp == "") return false;
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(exp);
		return matcher.matches();
	}

	public static void main(String[] args) {
		//String ss = "4543~!@#$%^&*98090 中文tyyt-_.?6u76u;:";
		//String ss = "~!@#$%^&*.?;:_98090iuyiyu ；‘DDFR《》？。，Að¢SSASA、中文";
		String ss = "中文ð\u009F\u0096\u0090";
		System.out.println(CheckNickName(ss));

	}

}
