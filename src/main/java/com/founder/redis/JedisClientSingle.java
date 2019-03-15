package com.founder.redis;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientSingle implements JedisClient{
	
	@Autowired
	private JedisPool jedisPool;

	@Override
	public void set(String key, String value, int expireTime) {
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.expire(key, expireTime);
		
		jedis.close();
	}
		
	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = (jedis.exists(key)) ? jedis.get(key) : null;
		
		jedis.close();
		return result;
	}
	
	@Override
	public long del(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.del(key);	
		jedis.close();
		return result;
	}
	
	@Override
	public boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		boolean result = jedis.exists(key);
	
		jedis.close();
		return result;
	}
	
	public JedisClientSingle() {
		System.out.println("这是redis单机版");
	}
}
