package com.founder.sso.validate;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.founder.sso.component.OauthProviders;
import com.founder.sso.entity.User;
import com.founder.sso.service.UserService;
import com.founder.sso.util.Clock;

public class OpenLogin {

	/*
	 * 设备id
	 */
	@NotEmpty
	@Size(min=6,max=128)
	private String devid;
	
	/*
	 * 第三方账号标识oauthid
	 */
	@NotEmpty
	private String oid;
	
	/*
	 * 第三方账号昵称
	 */
	@NotEmpty
	@Size(max=32)
	private String nickname;

	/*
	 * 第三方账号类型
	 */
	@NotEmpty
	//@Pattern(regexp="^(facebook|google|twitter|tencent_wechat)$")
	private String provider;
	
	//@Pattern(regexp="^(http|ftp|https)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$")
	private String head;

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public User asUser() {
		User user = new User();
    	user.setUsername(OauthProviders.value(provider).getPrefix()+"-"+oid);
    	user.setNickname(this.nickname);
    	user.setActived(true);
    	user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
    	user.setProvider(provider);
    	user.setOauthUid(oid);
    	if(head!=null)
    		UserService.generateFace(user, head);
		return user;
	}
}
