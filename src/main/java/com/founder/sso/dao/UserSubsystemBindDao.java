package com.founder.sso.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.entity.UserSubsystemBind;


public interface UserSubsystemBindDao extends PagingAndSortingRepository<UserSubsystemBind, Long> {

	List<UserSubsystemBind> findByUserIdAndSubsystemId(int userid, int subsystemid);
	
}
