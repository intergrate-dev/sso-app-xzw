package com.founder.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.founder.sso.util.HttpClientUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JedisClientCluster implements JedisClient{
	
	@Autowired
	private JedisCluster jedisCluster;
	
	//private String serverInfo = "172.30.129.145:7379,172.30.129.145:7380,172.30.129.146:7379,172.30.129.146:7380,172.30.129.147:7379,172.30.129.147:7380";  
	  
    private JedisCluster getClusterInfo() {  
    	String serverInfo = getJedisClusterNodes();
        Set<HostAndPort> set = new HashSet<HostAndPort>();  
        if(serverInfo==null||"".equals(serverInfo.length())) {  
            throw new RuntimeException("The serverInfo can not be empty");  
        }  
        String ipPort[] = serverInfo.split(",");  
        int len = ipPort.length;  
        for(int i=0;i<len;i++) {  
            String server[] = ipPort[i].split(":");  
            set.add(new HostAndPort(server[0], Integer.parseInt(server[1])));  
        }  
        JedisCluster jc = new JedisCluster(set);
        return jc;  
    }
    
	private String getJedisClusterNodes() {
		String cn_key = "clusterNodes";
		String clusterNodes = jedisCluster.get(cn_key);
		if (!StringUtils.isEmpty(clusterNodes)) {
			return clusterNodes;
		}
		//调用amuc同步接口
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("",""));
		String res = new HttpClientUtil().callAmucAPI("/api/param/paramConfig.do", params);
		if(res.indexOf("REDIS1_ADDR") == -1){
			return null;
		}
		org.json.JSONObject json;
		String REDIS1_ADDR = null;
		String REDIS2_ADDR = null;
		String REDIS3_ADDR = null;
		try {
			json = new org.json.JSONObject(res);
			REDIS1_ADDR = json.getString("REDIS1_ADDR");
			REDIS2_ADDR = json.getString("REDIS2_ADDR");
			REDIS3_ADDR = json.getString("REDIS3_ADDR");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		clusterNodes = REDIS1_ADDR+":7000,"+REDIS2_ADDR+":7001,"+REDIS3_ADDR+":7002,"+REDIS3_ADDR+":7003,"+REDIS1_ADDR+":7004,"+REDIS2_ADDR+":7005";
		jedisCluster.set(cn_key, clusterNodes);
		jedisCluster.expire(cn_key, 24 * 3600);
		System.out.println("------------------clusterNodes-----------"+clusterNodes);
		return clusterNodes;
	}

	@Override
	public void set(String key, String value, int expireTime) {
		try {
			if(getJedisClusterNodes()==null){
                jedisCluster.set(key, value);
                jedisCluster.expire(key, expireTime);
            }else{
                getClusterInfo().set(key, value);
                getClusterInfo().expire(key, expireTime);
            }
		} catch (Exception e) {
			System.out.println("------------------Exception, jedisClientCluster set key: -----------" + key);
			e.printStackTrace();
		}
	}
	
	@Override
	public String get(String key) {
		try {
			if(getJedisClusterNodes()==null){
                return jedisCluster.get(key);
            }else{
                JedisCluster clusterInfo = getClusterInfo();
                if (clusterInfo.exists(key)) {
                    return getClusterInfo().get(key);
                }
            }
		} catch (Exception e) {
			System.out.println("------------------Exception, jedisClientCluster get key: -----------" + key);
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean exists(String key) {
		if(getJedisClusterNodes()==null){
			return jedisCluster.exists(key);
		}else{
			return getClusterInfo().exists(key);
		}
	}
	
	@Override
	public long del(String key){
		try {
			if(getJedisClusterNodes()==null){
                return jedisCluster.del(key);
            }else{
                JedisCluster clusterInfo = getClusterInfo();
                if (clusterInfo.exists(key)) {
                    return getClusterInfo().del(key);
                }
            }
		} catch (Exception e) {
			System.out.println("------------------Exception, jedisClientCluster del key: -----------" + key);
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public JedisClientCluster() {
		System.out.println("这是redis集群版");
	}
}
