package com.founder.sso.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseConfigurationProperties {
	String url;
	String username;
	String password;
	String driverClassName;
	String testWhileIdle;
	String timeBetweenEvictionRunsMillis;

	public String getUrl() {
		return (System.getenv("DB_URL") != null && System.getenv("DB_URL") != "") ? System.getenv("DB_URL") : url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return (System.getenv("DB_USER") != null && System.getenv("DB_USER") != "") ? System.getenv("DB_USER")
				: username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return (System.getenv("DB_PASSWORD") != null && System.getenv("DB_PASSWORD") != "")
				? System.getenv("DB_PASSWORD") : password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return (System.getenv("DB_DRIVER") != null && System.getenv("DB_DRIVER") != "") ? System.getenv("DB_DRIVER")
				: driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(String testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public String getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(String timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

}