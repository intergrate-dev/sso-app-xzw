package com.founder.sso.component;

import java.util.Date;
public class RemoteUser{
	
	//第三方账号的id
    protected String uid;
    protected String nickname;
    protected String avatarSmall;
    protected String avatarMiddle;
    protected String avatarLarge;
    protected String accessToken;
    protected Date tokenExpiresTime;
    protected String provider;
   
    public RemoteUser(String uid, String nickname, String avatarMiddle, String[] remoteUserAttrName){
        this.uid= uid;
        this.nickname= nickname;
        this.avatarMiddle= avatarMiddle;
        this.avatarSmall=avatarMiddle;
        this.avatarLarge=avatarMiddle;
    }
    
    
    public RemoteUser() {
        super();
    }


    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getNickname() {
        return nickname;
    }
    public String getAvatarSmall() {
        return avatarSmall;
    }
    public String getAvatarLarge() {
        return avatarLarge;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public Date getTokenExpiresTime() {
        return tokenExpiresTime;
    }
    public String getProvider() {
        return provider;
    }
  
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }
    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setTokenExpiresTime(Date tokenExpiresTime) {
        this.tokenExpiresTime = tokenExpiresTime;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
	public String getAvatarMiddle() {
		return avatarMiddle;
	}
	public void setAvatarMiddle(String avatarMiddle) {
		this.avatarMiddle = avatarMiddle;
	}
    public String convertUserName(){
    	return OauthProviders.value(provider).getPrefix()+"-"+uid;
    }
}
