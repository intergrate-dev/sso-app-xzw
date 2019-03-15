package com.founder.sso.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.dao.SubsystemAppDao;
import com.founder.sso.entity.SubsystemApp;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@Service
public class SubsystemAppService {

	@Autowired
	private SubsystemAppDao subsystemAppDao;

	/**
	 * 查询所有的数据
	 * @return
	 */
	public List<SubsystemApp> findAll(){
		return ImmutableList.copyOf(subsystemAppDao.findAll());
	}
	/**
	 * 查询所有正常的系统
	 * @return
	 */
	public List<SubsystemApp> findByEnabledTrue(){
		return subsystemAppDao.findByEnabledTrue();
	}
	/**
	 * 查询除此之外的，所有正常的系统
	 * @return
	 */
	public List<SubsystemApp> findByEnabledTrueAndCodeNot(String exceptSystemCode){
		return subsystemAppDao.findByEnabledTrueAndCodeNot(exceptSystemCode);
	}
	
	public SubsystemApp findByCode(String code){
		List<SubsystemApp> systemList = subsystemAppDao.findByCode(code);
		if(systemList!=null&&!systemList.isEmpty())
			return systemList.get(0);
		return null;
	}
	
	/**
	 * 查询某一Subsystem
	 * @return
	 */
	public SubsystemApp findOne(Long id){
		return subsystemAppDao.findOne(id);
	}
	
	/**
	 * 保存
	 * @return
	 */
	public SubsystemApp save(SubsystemApp subsystemApp){
		//新增和修改两种情况
		//根据subsystemid 去查询数据库里是否有记录，没有记录就是新增，有记录就是修改
		Long subsystemid = subsystemApp.getId();
		SubsystemApp  sub_old = null;
		if(subsystemid == null|| subsystemid <= 0){
			sub_old = null;
		}else{
			sub_old = subsystemAppDao.findOne(subsystemid);
			if(sub_old!=null&&!sub_old.getCode().equals(subsystemApp.getCode())){
				return subsystemApp;
			}
		}
		if(sub_old != null){
			//修改时,code和secretkey保持数据库内已有的值不变
			subsystemApp.setSecretKey(sub_old.getSecretKey());
			subsystemApp.setCode(sub_old.getCode());
		}else{
			subsystemApp.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		return subsystemAppDao.save(subsystemApp);
	}
	
	/**
	 * 删除
	 * @return
	 */
	public void delete(Long id){
		 subsystemAppDao.delete(id);
	}
	public JSONObject findByCode(String code, Long systemId) {
		Map<String,Object> json = Maps.newHashMap();
		List<SubsystemApp> systemList = subsystemAppDao.findByCode(code);
		String result = "true";
    	if(systemList!=null && systemList.size()>0){
    		for (SubsystemApp subsystem : systemList) {
				if(subsystem.getId()!=systemId){
					result = "false";
					break;
				}
			}
    	}
   		json.put("result", result);
		return new JSONObject(json);
	}

}
