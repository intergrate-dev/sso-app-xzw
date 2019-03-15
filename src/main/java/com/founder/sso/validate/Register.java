package com.founder.sso.validate;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.founder.sso.entity.User;
import com.founder.sso.util.Clock;

public class Register {

	@Size(min=3, max=20)
	@Pattern(regexp="^[a-zA-Z][a-zA-Z0-9_]{2,20}$",message="用户名格式不正确，必须是字母开头，由字母数字下划线组成")
	@Sentive
	private String username;
	
	@Size(max=32)
	@Sentive
	private String nickname;
	
	// @NotEmpty
	//@Pattern(regexp="^0?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[0-9])[\\d]{8}$",message="手机号格式不正确")
	private String phone;

	// {"email": "不能为空"}
	@NotEmpty
	private String email;
	
	private String siteid;
	
	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	@NotEmpty
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User asUser() {
		User user = new User();
		user.setUsername(this.username);
		user.setNickname(this.nickname==null?this.username:this.nickname);
		user.setActived(true);
		user.setPhone(phone);
		user.setEmail(email);
		user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
		return user;
	}
	
}
