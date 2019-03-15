package com.founder.sso.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.founder.sso.entity.IdEntity;

/**
 * app端子系统的实体类
 * @author zhangmc
 */
@Entity
@Table(name = "subsystem_app")
public class SubsystemApp extends IdEntity implements Serializable{

	private static final long serialVersionUID = 6990917414326635885L;
	//子系统代码，唯一标识；子系统进行登录认证时的身份代码
	private String code;
	private String name;
	//子系统的域名
    private String domain;
    //子系统加密类型
    private String encryptType;
    //子系统同步退出的url
    private String logoutUrl;
    //子系统登陆的url
    private String loginUrl;
    //子系统安全码
    private String secretKey;
	private String description;
	//是否启用子系统
    private boolean enabled;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getEncryptType() {
		return encryptType;
	}
	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}
	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	public String getLoginUrl() {
		return loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
