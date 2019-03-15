package com.founder.sso.validate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Logout {

	@NotEmpty
	@Size(min=6,max=128)
	private String devid;
	
	@NotNull
	private Long uid;
	
	@NotEmpty
	@Size(min=32,max=32)
	private String token;


	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
