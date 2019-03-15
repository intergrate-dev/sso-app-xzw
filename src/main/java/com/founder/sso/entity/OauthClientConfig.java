package com.founder.sso.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.founder.sso.entity.IdEntity;

@Entity
@Table(name = "oauth_client_config")
public class OauthClientConfig extends IdEntity implements Serializable{

    private static final long serialVersionUID = -3938998426078629854L;
    private String providerName;
    private String provider;
    private String oauthVersion = "2.0";
    private String description;
    private String appId = "";
    private String secretKey = "";
    private boolean enabled;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProvider() {
        return provider;
    }

    public String getOauthVersion() {
        return oauthVersion;
    }

    public String getDescription() {
        return description;
    }

    public String getAppId() {
        return appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setOauthVersion(String oauthVersion) {
        this.oauthVersion = oauthVersion;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
