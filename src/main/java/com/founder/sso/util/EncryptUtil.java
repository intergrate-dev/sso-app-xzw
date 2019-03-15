package com.founder.sso.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptUtil {

	static Map<String, Cipher> CIPHERS = new HashMap<String, Cipher>();
	
	public static String aesEncrypt(String key, byte[] datasource) throws Exception {
		if(key==null||key.length()<16){
			return null;
		}
		Cipher cipher = CIPHERS.get(key);
		if (cipher == null) {
			SecretKeySpec keyspec = new SecretKeySpec(key.substring(0,16).getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(key.substring(key.length()-16).getBytes());
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			CIPHERS.put(key, cipher);
		}
		int blockSize = cipher.getBlockSize();
		
		int plaintextLength = datasource.length;
		if (plaintextLength % blockSize != 0) {
			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
		}
		
		byte[] plaintext = new byte[plaintextLength];
		System.arraycopy(datasource, 0, plaintext, 0, datasource.length);
		
		return new String(new Base64().encode(cipher.doFinal(plaintext)));
	}

	public static void main(String[] args) throws Exception {
		System.out.println(URLEncoder.encode(aesEncrypt("2a0ac002dbe04b5d841a164a1db844ee", "{\"username\":\"lnb\",\"_random\":\"16681019-ba98-4afd-9b45-b4f6ebc7e5f1\",\"uid\":\"141\",\"code\":\"xc_wenzheng\",\"email\":\"ierover@qq.com\",\"createTime\":1464833934206}".getBytes("utf-8")),"utf-8"));
	}
}
