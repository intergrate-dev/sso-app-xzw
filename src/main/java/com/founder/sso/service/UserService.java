package com.founder.sso.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.founder.sso.component.OauthProviders;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.entity.SystemConfig;
import com.founder.sso.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.founder.sso.dao.LocalPrincipalDao;
import com.founder.sso.dao.UserDao;
import com.founder.sso.dao.UserOauthBindingDao;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.User;
import com.founder.sso.entity.UserOauthBinding;
import com.founder.sso.validate.OpenLogin;
import com.founder.sso.validate.Register;
import com.google.common.base.Preconditions;

@Service
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class UserService {
	
	public static final String DEFAULT_HASH_ALGORITHM = "SHA-1";
    // 加密算法的次数
    public static final int DEFAULT_HASH_ITERATIONS = 2;
    // 盐值的长度
    public static final int DEFAULT_SALT_SIZE = 8;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserOauthBindingDao userOauthBindingDao;
    @Autowired
    private LocalPrincipalDao localPrincipalDao;

    @Autowired
    private LocalPrincipalService localPrincipalService;

    @Autowired
    private SystemConfigDao systemConfigDao;
    /**
     * 保存用户
     */
    public User save(User user) {
        return userDao.save(user);
    }

    /**
     * 查询用户
     */
    public User findUserById(long userId) {
        return userDao.findOne(userId);
    }
    

    /**
     * 通过用户名去查询一个用户
     * 
     * @return 相应用户对象 不存在返回null
     */
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        return user;
    }
    
    /**
     * 查询绑定
     * @return 相应用户对象 不存在返回null
     */
    public UserOauthBinding findBinding(String oauthUid, String provider) {
    	UserOauthBinding binding = userOauthBindingDao.findByOauthUidAndProvider(oauthUid, provider);
    	return binding;
    }
    
    /**
     * 查询用户
     * @return 相应用户对象 不存在返回null
     */
    public User findByOauthUidAndProvider(String oauthUid, String provider) {
    	return userDao.findByOauthUidAndProvider(oauthUid, provider);
    }
    
    /**
     * 删除一个用户
     */
    public void delete(long userId) {
        userDao.delete(userId);
    }

    public void updateUser(User user) {
        userDao.save(user);
    }

	public UserOauthBinding findByUserIdAndOauthUidAndProvider(Long id, String uid, String provider) {
		return userOauthBindingDao.findByUserIdAndOauthUidAndProvider(id, uid, provider);
	}

	public User findByPhone(String phone) {
		return userDao.findByPhone(phone);
	}
	
	public User findByEmail(String email){
		return userDao.findByEmail(email);
	}

	public User register(Register register) {
		User user = userDao.save(register.asUser());
		Preconditions.checkArgument(user != null && user.getId() != null, "User Must set And The User Object Must have a id");
        Map<String, Object> identities = user.getIdentities();
        
        LocalPrincipal principal = localPrincipalDao.findByUserId(user.getId());
        if(principal == null){
        	principal = new LocalPrincipal();
        }
        
        if(!StringUtils.isEmpty(register.getPassword())){
        	String salt = Encodes.encodeHex(Digests.generateSalt(DEFAULT_SALT_SIZE));
        	principal.setPassword(entryptPassword(register.getPassword(), salt));
        	principal.setSalt(salt);
        }
        principal.setUser(user);
        for (Map.Entry<String, Object> entry : identities.entrySet()) {
            if ("username".equals(entry.getKey())) {
                principal.setUsername(entry.getValue().toString());
                continue;
            }
            if ("phone".equals(entry.getKey())) {
                principal.setPhone(entry.getValue().toString());
                continue;
            }
            if ("email".equals(entry.getKey())) {
                principal.setEmail(entry.getValue().toString());
            }
        }
        principal = localPrincipalDao.save(principal);
		user.setLocalPrincipal(principal);
		return userDao.save(user);
	}

    /**
     * 根据明文password和salt生成密文password
     * @param plainPwd
     * @param salt
     * @return
     */
    public String entryptPassword(String plainPwd, String salt) {
        return entryptPassword(plainPwd, salt, DEFAULT_HASH_ALGORITHM, DEFAULT_HASH_ITERATIONS);
    }

    public String entryptPassword(String plainPwd, String salt, String ALGORITHM, int iterations) {
        return Encodes.encodeHex(Digests.sha1(plainPwd.getBytes(), Encodes.decodeHex(salt), iterations));
    }

    public boolean isPasswordMatch(LocalPrincipal principal, String credential) {
        String salt = principal.getSalt();
        String hashedCredential = entryptPassword(credential, salt);
        if (hashedCredential.equals(principal.getPassword())) {
            return true;
        }
        return false;
    }

	public User register(OpenLogin openLogin) {
        return userDao.save(openLogin.asUser());
	}

	/**
     * 将图片从faceUrl下载下来，生成大中小三种尺寸的头像，并赋值给user
     * @param user
     * @param faceUrl
     */
    public static User generateFace(User user, String faceUrl){
    	

        user.setAvatarSmall(faceUrl);
        user.setAvatarMiddle(faceUrl);
        user.setAvatarLarge(faceUrl);
    	return user;
    }

	public void bindUser(User user, User localUser) {
		UserOauthBinding userOauthBinding = new UserOauthBinding();
		userOauthBinding.setUser(localUser);
		userOauthBinding.setNickname(user.getNickname());
		userOauthBinding.setProvider(user.getProvider());
		userOauthBinding.setOauthUid(user.getOauthUid());
		userOauthBinding.setBindTime(Clock.DEFAULT.getCurrentDate());
		userOauthBindingDao.save(userOauthBinding);
	}
    
	/**
	 * 激活用户
	 * @param id 用户标识
	 */
	public void activeUser(long id) {
		User user = userDao.findOne(id);
		user.setActived(true);
		userDao.save(user);
	}
	
	/**
	 * 禁用用户
	 * @param id 用户标识
	 */
	public void disableUser(long id) {
		User user = userDao.findOne(id);
		user.setActived(false);
		userDao.save(user);
	}

    /**
     * 根据字段查询用户
     */
    public User getUserByField(Object value, String field) {
        if (field.equals("phone")) {
            return userDao.findByPhone(value.toString());
        } else if (field.equals("username")) {
            return userDao.findByUsername(value.toString());
        } else if (field.equals("email")) {
            return userDao.findByEmail(value.toString());
        }
        return null;
    }

    public String setDefaultImg() {
        SystemConfig config = systemConfigDao.findByScode("head_img_path");
        String default_img = config != null ? config.getSstatus() : "undifine";
        default_img += "default/head_img.jpg";
        return default_img;
    }

    public JSONObject synRegistToMember(User user, String siteId, String password) {
        String res = null;
        JSONObject json = null;
        /*JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(user.getProvider())) {
            json.put("code", "0");
            json.put("msg", "参数：provider 不能为空");
            return json;
        }*/
        try {
            if (user.getId() == null) {
                this.save(user);
            }
            //调用amuc同步接口
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("siteid", "" + siteId));
            params.add(new BasicNameValuePair("ssoid", "" + user.getId()));
            params.add(new BasicNameValuePair("username", user.getUsername()));
            params.add(new BasicNameValuePair("nickname", user.getNickname()));
            params.add(new BasicNameValuePair("mobile", user.getPhone()));
            params.add(new BasicNameValuePair("headImg", user.getAvatarLarge()));
            params.add(new BasicNameValuePair("email", user.getEmail()));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("provider", user.getProvider()));
            params.add(new BasicNameValuePair("oauthid", user.getOauthUid()));
            System.out.println("register, call syn/registerEx.do call start ... ");
            res = new HttpClientUtil().callAmucAPI("/api/member/syn/registerEx.do", params);
            json = JSONObject.parseObject(res);
            System.out.println("register, call syn/registerEx.do result: " + json.toString());
            if (json.getString("code").equals("1")) {
                if (json.containsKey("head")) {
                    user.setAvatarLarge(json.getString("head"));
                    user.setAvatarMiddle(json.getString("head"));
                    user.setAvatarLarge(json.getString("head"));
                }
                //user.setUid(json.getInt("uid"));
                user.setUid(json.getInteger("uid"));
                this.save(user);
                if (!org.apache.commons.lang3.StringUtils.isEmpty(password)) {
                    localPrincipalService.savePrincipal(user, password);
                }
            } else {
                if (user.getId() != null ) {
                    this.delete(user.getId());
                }
                json.put("code", "0");
                json.put("msg", "注册失败！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (user.getId() != null) {
                this.delete(user.getId());
            }
            json.put("code", "0");
            json.put("msg", "注册失败！");
        }
        return json;
    }
}
