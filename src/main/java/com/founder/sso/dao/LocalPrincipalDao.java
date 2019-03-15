package com.founder.sso.dao;

import org.springframework.data.repository.CrudRepository;

import com.founder.sso.entity.LocalPrincipal;

public interface LocalPrincipalDao extends CrudRepository<LocalPrincipal, Long> {
    
	LocalPrincipal findByUserId(Long userId);

    LocalPrincipal findByUsername(String username);

    LocalPrincipal findByPhone(String phone);
    
}
