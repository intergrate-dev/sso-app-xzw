package com.founder.sso.validate;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

//@NotNullBoth
public class Login {

	@NotEmpty
	@Size(min=6,max=128)
	private String devid;
	
	//@Pattern(regexp="^0?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[0-9])[\\d]{8}$",message="手机号格式不正确")
	private String phone;
	
	@Pattern(regexp="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message="邮箱格式不正确")
	private String email;
	
	@Pattern(regexp="^[a-zA-Z][a-zA-Z0-9_]{2,20}$",message="用户名格式不正确，必须是字母开头，由字母数字下划线组成")
	private String username;

	@NotEmpty
	private String password;

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
