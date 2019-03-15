package com.founder.sso.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class ClusterConfigurationProperties{

	List<String> jedisClusterNode;
	int timeout;
	int maxRedirects;
	int connectionTimeout;
    int soTimeout;
    int maxAttempts;
    String password;
    
	public List<String> getJedisClusterNode() {
		String REDIS1_ADDR = System.getenv("REDIS1_ADDR");
		String REDIS2_ADDR = System.getenv("REDIS2_ADDR");
		String REDIS3_ADDR = System.getenv("REDIS3_ADDR");

		List<String> nodes = new ArrayList<String>();
		if(REDIS1_ADDR !="" && REDIS1_ADDR !=null){
			nodes.add(REDIS1_ADDR+":7000");
			nodes.add(REDIS2_ADDR+":7001");
			nodes.add(REDIS3_ADDR+":7002");
			nodes.add(REDIS3_ADDR+":7003");
			nodes.add(REDIS1_ADDR+":7004");
			nodes.add(REDIS2_ADDR+":7005");
			return nodes;
		}else{
			return jedisClusterNode;
		}	
	}

	public void setJedisClusterNode(List<String> nodes) {
		this.jedisClusterNode = nodes;
	}

	public int getTimeout() {
		
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public String getPassword() {
		String password1 = System.getenv("password");
		if(password1 !="" && password1 !=null){
			return password1;
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
