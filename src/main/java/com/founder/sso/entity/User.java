package com.founder.sso.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.founder.sso.component.OauthProviders;
import org.springframework.util.StringUtils;

import com.founder.sso.util.DateUtil;
import com.founder.sso.util.FaceUtil;
import com.founder.sso.util.SystemConfigHolder;
import com.google.common.collect.Maps;

@Entity
@Table(name = "users")
public class User extends IdEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    //private static String application = SystemConfigHolder.getConfig("application");
    //private static String faceRootPath = (System.getenv("FACE_PATH") != null && System.getenv("FACE_PATH") != "") ? System.getenv("FACE_PATH"):SystemConfigHolder.getConfig("face_path");
    private static final boolean DEFAULT_ACTIVE = true;
    private String username;
    private String nickname;
    // 注册日期
    private Date registerDate;
    // 是否是活动状态
    private boolean actived = DEFAULT_ACTIVE;
    // 手机号
    private String phone;
    // 邮箱
    private String email;
    //会员中心id
    private int uid;
	// oauth的id
    private String oauthUid;
    // oauth的供应商
    private String provider;
    //小头像50*50
    private String avatarSmall = FaceUtil.default_avatarSmall;
    //中头像100*100
    private String avatarMiddle = FaceUtil.default_avatarMiddle;
    //大头像322*322
    private String avatarLarge = FaceUtil.default_avatarLarge;
    
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "principal_id")
    private LocalPrincipal localPrincipal;
    
    @OneToMany(cascade = {CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "user") 
    private Set<UserOauthBinding> userOauthBindings = new HashSet<UserOauthBinding>();  
    
	public LocalPrincipal getLocalPrincipal() {
		return localPrincipal;
	}

	public void setLocalPrincipal(LocalPrincipal localPrincipal) {
		this.localPrincipal = localPrincipal;
	}

	
	public Set<UserOauthBinding> getUserOauthBindings() {
		return userOauthBindings;
	}

	public void setUserOauthBindings(Set<UserOauthBinding> userOauthBindings) {
		this.userOauthBindings = userOauthBindings;
	}


	@Column
    @org.hibernate.annotations.Type(type = "yes_no")
    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }
    
    public String getConvertNickname() {
    	return nickname==null?"":nickname;
    }
    

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getRegisterDate() {
        return registerDate;
    }
    
    public String getConvertRegisterDate() {
    	return registerDate==null?"":DateUtil.getTimeStr(registerDate);
    }
   
    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getConvertPhone() {
    	return phone==null?"":phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    
    public String getConvertEmail() {
    	return email==null?"":email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOauthUid() {
		return oauthUid;
	}
    
    public String getConvertOauthUid() {
		return oauthUid==null?"":oauthUid;
	}

	public void setOauthUid(String oauthUid) {
		this.oauthUid = oauthUid;
	}

	public String getProvider() {
		return provider;
	}
	
	public String getConvertProvider() {
		return provider==null?"":provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

    public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public void setAvatarSmall(String avatarSmall) {
		this.avatarSmall = avatarSmall;
	}

	public String getAvatarMiddle() {
		return avatarMiddle;
	}
	
	/*public String getFullAvatarMiddle() {
		return getFullFace(getAvatarMiddle());
	}*/

	public void setAvatarMiddle(String avatarMiddle) {
		this.avatarMiddle = avatarMiddle;
	}

	public String getAvatarLarge() {
		return avatarLarge;
	}
	
	public void setAvatarLarge(String avatarLarge) {
		this.avatarLarge = avatarLarge;
	}
	
	
	public void resetFace(){
		this.avatarSmall = FaceUtil.default_avatarSmall;
		this.avatarMiddle = FaceUtil.default_avatarMiddle;
		this.avatarLarge = FaceUtil.default_avatarLarge;
	}

	public Map<String, Object> getIdentities() {
        HashMap<String, Object> identities = Maps.newHashMap();
        identities.put("uid", getId().toString());
        if (!StringUtils.isEmpty(username)) {
            identities.put("username", username);
        }
        if (!StringUtils.isEmpty(nickname)) {
        	identities.put("nickname", nickname);
        }
        if (!StringUtils.isEmpty(phone)) {
            identities.put("phone", phone);
        }
        if (!StringUtils.isEmpty(email)) {
            identities.put("email", email);
        }
        return identities;
    }

	public Map<String,Object> asMap() {
		Map<String,Object> map = Maps.newHashMap();
		map.putAll(getIdentities());
		map.put("username", username==null?"":username);
		map.put("nickname", nickname==null?"":nickname);
		map.put("isOpen", getLocalPrincipal()==null?1:0);//0是主账号登录，1是三方登录
        if (!StringUtils.isEmpty(provider)) {
            if (provider.indexOf("google") != -1) {
                provider = OauthProviders.GOOGLEPLUS.getValue();
            }
            map.put("provider", provider);
        }
        return map;
	}
	
}