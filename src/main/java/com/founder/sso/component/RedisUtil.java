package com.founder.sso.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

@Component
public class RedisUtil {

	@Autowired
	RedisConnectionFactory redisConnectionFactory;
	
	public void set(String key,String value,Expiration expiration){
		getConnection().set(key.getBytes(Charsets.UTF_8), value.getBytes(Charsets.UTF_8), expiration, null);
	}

	private RedisClusterConnection getConnection() {
		return redisConnectionFactory.getClusterConnection();
	}
	
	public String get(String key){
		byte[] data = getConnection().get(key.getBytes());
		return data == null ? null : new String(data , Charsets.UTF_8);
	}
	
	public long del(String key){
		return getConnection().del(key.getBytes());
	}

	public boolean exists(String key) {
		return getConnection().exists(key.getBytes());
	}
}
