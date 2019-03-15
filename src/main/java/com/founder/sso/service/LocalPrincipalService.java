package com.founder.sso.service;

import javax.transaction.Transactional;

import com.founder.sso.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.dao.LocalPrincipalDao;
import com.founder.sso.dao.UserDao;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.util.Digests;
import com.founder.sso.util.Encodes;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Transactional
public class LocalPrincipalService {
    public static final String DEFAULT_HASH_ALGORITHM = "SHA-1";
    // 加密算法的次数
    public static final int DEFAULT_HASH_ITERATIONS = 2;
    // 盐值的长度
    public static final int DEFAULT_SALT_SIZE = 8;

    @Autowired
    LocalPrincipalDao dao;
    
    @Autowired
    UserDao userDao;

    public LocalPrincipal getByUserId(Long userId) {
        return dao.findByUserId(userId);
    }



    public boolean updatePassword(LocalPrincipal principal, String currentPassword, String newPassword){
        if (isPasswordMatch(principal, currentPassword)) {
            principal.setPassword(entryptPassword(newPassword, principal.getSalt()));
            dao.save(principal);
            return true;
        } 
        return false;
    }

    //修改密码
    public void resetPassword(LocalPrincipal principal, String newPassword) {
    	if(principal!=null){
    		principal.setPassword(entryptPassword(newPassword, principal.getSalt()));
    		dao.save(principal);
    	}
    }
    
    
    public void resetPasswordByPhone(String phone, String newPassword) {
    	LocalPrincipal principal = dao.findByPhone(phone);
    	if(principal!=null){
    		resetPassword(principal,newPassword);
    	}
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



	public void resetPasswordByPhone(String phone, String password,
			String username) {
		LocalPrincipal principal = dao.findByPhone(phone);
		principal.setUsername(username);
    	if(principal!=null){
    		resetPassword(principal,password);
    	}
	}
	
	public void resetPasswordByPhone1(long id, String password,
			String username,String phone) {
		LocalPrincipal principal = dao.findByUserId(id);
		principal.setUsername(username+"123");
		principal.setPhone(phone+"123");
    	if(principal!=null){
    		resetPassword(principal,password);
    	}
	}

    /**
     * 注册、修改用户资料时，都需要同步principal，分为
     * 1、注册时，principal为null
     * 2.1、完善资料，principal为null
     * 2.2、完善资料，principal不为null
     * @param user
     * @param plainPwd
     * @return
     */
    public LocalPrincipal savePrincipal(User user, String plainPwd) {
        checkArgument(user != null && user.getId() != null, "User Must set And The User Object Must have a id");
        Map<String, Object> identities = user.getIdentities();

        LocalPrincipal principal = getByUserId(user.getId());
        if(principal == null){
            principal = new LocalPrincipal();
        }

        if(StringUtils.isNotBlank(plainPwd)){
            String salt = Encodes.encodeHex(Digests.generateSalt(DEFAULT_SALT_SIZE));
            principal.setPassword(entryptPassword(plainPwd, salt));
            principal.setSalt(salt);
        }
        principal.setUser(user);
        for (Map.Entry<String, Object> entry : identities.entrySet()) {
            if ("username".equals(entry.getKey())) {
                principal.setUsername((String) entry.getValue());
                continue;
            }
            if ("phone".equals(entry.getKey())) {
                principal.setPhone((String) entry.getValue());
                continue;
            }
            if ("email".equals(entry.getKey())) {
                principal.setEmail((String) entry.getValue());
            }
        }
        LocalPrincipal persisted = dao.save(principal);
        user.setLocalPrincipal(principal);
        userDao.save(user);
        return persisted;
    }
}
