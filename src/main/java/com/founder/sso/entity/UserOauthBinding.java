package com.founder.sso.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.founder.sso.entity.IdEntity;
import com.founder.sso.entity.User;

@Entity
public class UserOauthBinding extends IdEntity {
	
    private String provider;
    
    @ManyToOne(cascade=CascadeType.REFRESH,optional=false)
    @JoinColumn(name = "user_id") 
    private User user;
    
    private String oauthUid;
    
    private Date bindTime;
    
    private String nickname;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOauthUid() {
		return oauthUid;
	}

	public void setOauthUid(String oauthUid) {
		this.oauthUid = oauthUid;
	}

	public UserOauthBinding() {
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

	public Date getBindTime() {
		return bindTime;
	}

	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
