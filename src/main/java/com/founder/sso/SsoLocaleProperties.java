package com.founder.sso;

import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sso.locale")
public class SsoLocaleProperties {

	String lang;
	Locale locale;
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public Locale getLocale() {
		if(this.lang.equals("ko-KR"))
			return Locale.KOREA;
		
		if(this.lang.equals("bo-CN"))
			return new Locale("bo", "CN");
		return Locale.CHINA;
	}
}
