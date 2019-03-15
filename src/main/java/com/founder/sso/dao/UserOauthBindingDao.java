package com.founder.sso.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.founder.sso.entity.UserOauthBinding;

public interface UserOauthBindingDao extends CrudRepository<UserOauthBinding, Long> {

    List<UserOauthBinding> findByUserId(long userId);
    
    UserOauthBinding findByOauthUidAndProvider(String oauthUid, String provider);
    
    UserOauthBinding findByUserIdAndProvider(long userId, String provider);
    
	UserOauthBinding findByUserIdAndOauthUidAndProvider(Long id, String uid, String provider);
    
}
