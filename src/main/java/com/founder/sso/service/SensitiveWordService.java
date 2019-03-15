package com.founder.sso.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.dao.SensitiveWordDao;
import com.founder.sso.entity.SensitiveWord;
import com.google.common.collect.ImmutableList;

@Transactional
@Service
public class SensitiveWordService {
	@Autowired
	private SensitiveWordDao sensitivewordDao;

	/**
	 * 查询所有敏感词
	 * 
	 * @return
	 */
	public List<SensitiveWord> findAll() {
		return ImmutableList.copyOf(sensitivewordDao.findAll());
	}

	public Set<String> readSensitiveWordTable() {
		Set<String> set = new HashSet<String>();
		List<SensitiveWord> list = ImmutableList.copyOf(sensitivewordDao.findAll());
		if (list != null && list.size() > 0) {
			for (SensitiveWord sensitiveWord : list) {
				set.add(sensitiveWord.getWord());
			}
		}
		return set;
	}
}
