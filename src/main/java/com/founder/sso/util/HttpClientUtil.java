package com.founder.sso.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	
	private final static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	
	public String doPost(String url,List<NameValuePair> list,String charset) {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost    
		HttpPost httppost = new HttpPost(url);
		UrlEncodedFormEntity uefEntity;
		CloseableHttpResponse response = null;
		try {
			uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public String callAmucAPI(String api,List<NameValuePair> params){
		String inner_api_url = "";
		if(System.getenv("INNER_API_URL") != null && System.getenv("INNER_API_URL") != ""){
			inner_api_url = System.getenv("INNER_API_URL");
		}else{
			inner_api_url = SystemConfigHolder.getConfig("inner_api_url");
		}

		String url = inner_api_url + api;
		String charset = "utf-8";
		params.add(new BasicNameValuePair("fromSSO","true"));
		String result = doPost(url,params,charset); 
		log.info("同步amuc会员表返回结果：{}",result);
		return result;
	}
}
