package com.founder.sso.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.founder.sso.entity.OauthClientConfig;

public interface OauthClientConfigDao extends CrudRepository<OauthClientConfig, Long> {
	
    OauthClientConfig findByProvider(String provider);
    
    List<OauthClientConfig> findByEnabled(boolean enabled);

}