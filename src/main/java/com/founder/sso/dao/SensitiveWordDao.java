package com.founder.sso.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.entity.SensitiveWord;


public interface SensitiveWordDao extends PagingAndSortingRepository<SensitiveWord, Long>{

}
