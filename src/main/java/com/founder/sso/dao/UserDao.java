package com.founder.sso.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.entity.User;

public interface UserDao extends PagingAndSortingRepository<User, Long> , JpaSpecificationExecutor<User>{
	
	User findByUsername(String username);
	
	User findByPhone(String phone);
	
	User findByEmail(String email);
    
	User findByUsernameAndActivedIsTrue(String username);

    User findByNickname(String nickname);
	
    User findByOauthUidAndProvider(String oauthId, String provider);
    
}
