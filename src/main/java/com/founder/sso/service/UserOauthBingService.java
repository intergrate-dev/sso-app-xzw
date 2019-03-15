package com.founder.sso.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.dao.UserOauthBindingDao;
import com.founder.sso.entity.UserOauthBinding;

@Transactional
@Service
public class UserOauthBingService {
	
	@Autowired
	UserOauthBindingDao userOauthBindingDao;
	
	public UserOauthBinding save(UserOauthBinding userOauthBinding){
		return userOauthBindingDao.save(userOauthBinding);
	}
	
	public UserOauthBinding findByUserIdAndProvider(Long userId,String provider){
		return userOauthBindingDao.findByUserIdAndProvider(userId, provider);
	}

	public void delete(UserOauthBinding binding) {
		userOauthBindingDao.delete(binding);
	}
}
