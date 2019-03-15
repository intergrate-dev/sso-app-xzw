package com.founder.sso.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sensitiveword")
public class SensitiveWord extends IdEntity implements Serializable{

	private static final long serialVersionUID = 3590844600591605258L;
	
	private String word;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
