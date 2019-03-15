package com.founder.sso.validate;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class OpenUser {

	@NotEmpty
	private String oid;

	@NotEmpty
	//@Pattern(regexp="^(sina_weibo|tencent_wechat|tencent_QQ)$")
	private String provider;
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
