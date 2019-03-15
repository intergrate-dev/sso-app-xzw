package com.founder.redis;

public interface JedisClient {

	public String get(String key);
	
	public long del(String key);

	public boolean exists(String key);
		
	public void set(String key, String value, int expireTime);
}