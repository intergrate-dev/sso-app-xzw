package com.founder.sso.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.entity.SubsystemApp;

public interface SubsystemAppDao extends PagingAndSortingRepository<SubsystemApp, Long> {
	
	public List<SubsystemApp> findByEnabledTrue();
	public List<SubsystemApp> findByEnabledTrueAndCodeNot(String Code);
	public List<SubsystemApp> findByCode(String code);
}
