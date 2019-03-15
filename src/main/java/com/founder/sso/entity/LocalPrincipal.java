package com.founder.sso.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

/**
 * 对本地保存的用户登录凭证的封装
 * 本地用户可以通过用户名、邮箱、手机号进行登录
 * 将这些登录凭证封装在这里并从用户实体中抽离，已方便不同认证渠道的认证信息隔离
 * 
 * @author fengdd
 * 
 */
@Entity
@Table(name = "local_principal")
@NamedNativeQuery(name = "LocalPrincipal.findByIdentities", query = "select * from Local_principal p1 where p1.username=?1 union all  select * from Local_principal p2 where p2.phone=?1 union all select * from Local_principal p3 where  p3.email=?1", resultClass = LocalPrincipal.class)
public class LocalPrincipal extends IdEntity {
    public static final String LOGIN_TYPE = "LOCAL_PRINCIPAL";
    public static enum IdentityType{
        EMAIL, PHONE, USERNAME;
    }
    private String username;
    private String phone;
    private String email;
    private String password;
    private String salt;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    
	public Map<String, String> getIdentities() {
        HashMap<String, String> identities = Maps.newHashMap();
        if (!StringUtils.isEmpty(username)) {
            identities.put("username", username);
        }
        if (!StringUtils.isEmpty(phone)) {
            identities.put("phone", phone);
        }
        if (!StringUtils.isEmpty(email)) {
            identities.put("email", email);
        }
        return identities;
    }
}
