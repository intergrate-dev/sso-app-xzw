package com.founder.sso.component;

public enum OauthProviders {
    SINA_WB("新浪微博", "sina_weibo", "wb"),
    TENCENT_WECHAT("微信", "tencent_wechat", "wx"),
    TENCENT_QQ("腾讯QQ", "tencent_QQ", "qq"),
	FACEBOOK("Facebook", "facebook", "fb"),
	GOOGLEPLUS("GooglePlus", "googlePlus", "gp"),
    TWITTER("Twitter", "twitter", "tw"),
	SOHU_CY("搜狐畅言", "sohu_cy", "cy");
    
    private String name;
    private String value;
    private String prefix;

    private OauthProviders(String name, String value, String prefix) {
        this.name = name;
        this.value = value;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public static OauthProviders value(String value){
		if(SINA_WB.getValue().equals(value))
			return SINA_WB;
		if(TENCENT_WECHAT.getValue().equals(value))
			return TENCENT_WECHAT;
		if(TENCENT_QQ.getValue().equals(value))
			return TENCENT_QQ;
		if(FACEBOOK.getValue().equals(value))
			return FACEBOOK;
		if(GOOGLEPLUS.getValue().equals(value))
			return GOOGLEPLUS;
		if(TWITTER.getValue().equals(value))
			return TWITTER;
		return null;
	}

}
