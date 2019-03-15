package com.founder.sso.dao;

import com.founder.sso.entity.SystemConfig;
import org.springframework.data.repository.CrudRepository;

public interface SystemConfigDao extends CrudRepository<SystemConfig, Long> {
	
	SystemConfig findByScode(String scode);
}