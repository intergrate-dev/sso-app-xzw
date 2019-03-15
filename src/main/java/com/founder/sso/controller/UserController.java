package com.founder.sso.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.founder.sso.component.OauthProviders;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.entity.SystemConfig;
import com.founder.sso.util.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.founder.redis.JedisClient;
import com.founder.sso.SsoLocaleProperties;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.User;
import com.founder.sso.entity.UserOauthBinding;
import com.founder.sso.exception.BusinessException;
import com.founder.sso.validate.Login;
import com.founder.sso.validate.Logout;
import com.founder.sso.validate.OpenLogin;
import com.founder.sso.validate.OpenUser;
import com.founder.sso.validate.Register;
import com.founder.sso.vo.Result;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

@RestController
@Validated
@RequestMapping("/api")
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final Logger log = LoggerFactory.getLogger("loginLogger");

    @Autowired
    JedisClient jedisClient;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    SsoLocaleProperties slp;

    @Autowired
    private SystemConfigDao systemConfigDao;

    /**
     * 仅支持邮箱注册
     *
     * @param register
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result register(@Valid Register register) {
        /*if(userService.findByPhone(register.getPhone())!=null){
			throw new BusinessException("user account exception", 
					messageSource.getMessage("register_phoneregistered", null, slp.getLocale()));
		}*/
        if (userService.findByEmail(register.getEmail()) != null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("register_emailregistered", null, slp.getLocale()));
        }

        User user = userService.register(register);
        System.out.println("------------------ register username: " + user.getUsername());
        if (StringUtils.isEmpty(user.getUsername())) {
            user.setUsername("APP USER" + user.getId());
            user.setNickname("APP USER" + user.getId());
        }
        System.out.println("++++++++++++++ register username: " + user.getUsername());

        String default_img = userService.setDefaultImg();
        user.setAvatarSmall(default_img);
        user.setAvatarMiddle(default_img);
        user.setAvatarLarge(default_img);
        JSONObject json = userService.synRegistToMember(user, "1", register.getPassword());

        Map<String, Object> map = Maps.newHashMap();
        if (json.getString("code").equals("0")) {
            map.put("code", json.get("code"));
            map.put("msg", json.get("msg"));
            return new Result(map);
        }
		/*int muid = json.getInteger("uid");
		user.setUid(muid);
		userService.save(user);*/
        map.putAll(user.asMap());
        getuserid(map, user.getId().toString());
        map.put("userid", String.valueOf(user.getUid()));
        return new Result(map);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@Valid Login login) {

        User user = null;

        if (login.getPhone() != null) {
            if (login.getPhone().indexOf("@") != -1) {
                if (checkEmail(login.getPhone())) {
                    user = userService.findByEmail(login.getPhone());
                    if (user == null) {
                        throw new BusinessException("user account exception",
                                messageSource.getMessage("emailunregister", null, slp.getLocale()));
                    }
                } else {
                    throw new BusinessException("user account exception",
                            messageSource.getMessage("email_format_error", null, slp.getLocale()));
                }
            } else if (judgeContainsStr(login.getPhone())) {
                if (checkUserName(login.getPhone())) {
                    user = userService.findByUsername(login.getPhone());
                    if (user == null) {
                        throw new BusinessException("user account exception",
                                messageSource.getMessage("usernameunregister", null, slp.getLocale()));
                    }
                } else {
                    throw new BusinessException("user account exception",
                            messageSource.getMessage("username_format_error_detail", null, slp.getLocale()));
                }
            } else {
                if (checkMobileNumber(login.getPhone())) {
                    user = userService.findByPhone(login.getPhone());
                    if (user == null) {
                        throw new BusinessException("user account exception",
                                messageSource.getMessage("phoneunregister", null, slp.getLocale()));
                    }
                } else {
                    throw new BusinessException("user account exception",
                            messageSource.getMessage("phone_format_error", null, slp.getLocale()));
                }
            }
        } else if (login.getEmail() != null) {
            user = userService.findByEmail(login.getEmail());
            if (user == null) {
                throw new BusinessException("user account exception",
                        messageSource.getMessage("emailunregister", null, slp.getLocale()));
            }
        } else if (login.getUsername() != null) {
            user = userService.findByUsername(login.getUsername());
            if (user == null) {
                throw new BusinessException("user account exception",
                        messageSource.getMessage("usernameunregister", null, slp.getLocale()));
            }
        }

        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }

        LocalPrincipal principal = user.getLocalPrincipal();
        if (principal.getPassword() == null || principal.getPassword().length() <= 0) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("old_user_modify_pwd", null, slp.getLocale()));
        }

        boolean match = localPrincipalService.isPasswordMatch(user.getLocalPrincipal(), login.getPassword());
        if (!match) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("name_or_pwd_error", null, slp.getLocale()));
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        jedisClient.set(login.getDevid(), token + "\t" + user.getId(), (365 * 24 * 60 * 60));
        Map<String, Object> map = Maps.newHashMap();
        map.putAll(user.asMap());
        getuserid(map, user.getId().toString());
        map.put("token", token);

        log.info("用户{}登录系统，手机号{}，设备号为{}。", user.getUsername(), user.getPhone(), login.getDevid());

        return new Result(map);
    }

    /**
     * 第三方登录接口，默认返回第三方登录用户信息。
     * 如果有绑定本地用户，则返回绑定本地用户信息，
     *
     * @param openLogin
     * @return
     */
    @RequestMapping(value = "/loginByOther", method = RequestMethod.POST)
    public Result loginByOther(@Valid OpenLogin openLogin) {
        System.out.println("============================== loginByOther ... , head: " + openLogin.getHead() + ", provider: " + openLogin.getProvider());
        Map<String, Object> map = null;
        try {
            map = Maps.newHashMap();
			/*if (!StringUtils.isEmpty(openLogin.getProvider())) {
				map.put("code", "0");
				map.put("msg", "参数：provider 不能为空");
				return new Result(map);
			}*/
            if (openLogin.getProvider().indexOf("google") != -1) {
                openLogin.setProvider(OauthProviders.GOOGLEPLUS.getValue());
            }
            User user = userService.findByOauthUidAndProvider(openLogin.getOid(), openLogin.getProvider());

            if (user == null) user = userService.register(openLogin);
            //同步到后台会员
            //this.loginByOther2xy(openLogin, user.getId().toString());

            if (!StringUtils.isEmpty(openLogin.getHead())) {
                user.setAvatarSmall(openLogin.getHead());
                user.setAvatarMiddle(openLogin.getHead());
                user.setAvatarLarge(openLogin.getHead());
            }
            System.out.println("register, call userService.synRegistToMember ... ");
            JSONObject json = userService.synRegistToMember(user, "1", "defult");

            if (json.getString("code").equals("0")) {
                map.put("code", json.get("code"));
                map.put("msg", json.get("msg"));
                return new Result(map);
            }
			/*int muid = json.getInteger("uid");
			user.setUid(muid);
			userService.save(user);*/

            UserOauthBinding binding = userService.findBinding(openLogin.getOid(), openLogin.getProvider());
            if (binding != null) user = binding.getUser();

            if (!user.isActived()) {
                throw new BusinessException("user account exception", "用户被禁用");
            }

            String token = UUID.randomUUID().toString().replaceAll("-", "");
            jedisClient.set(openLogin.getDevid(), token + "\t" + user.getId(), (365 * 24 * 60 * 60));


            getuserid(map, user.getId().toString());
            map.put("userid", String.valueOf(user.getUid()));
            map.put("token", token);
            map.put("head", openLogin.getHead());
            map.putAll(user.asMap());
        } catch (BusinessException e) {
            System.out.println("============================== loginByOther ... , result: " + new Result(map).toString());
            e.printStackTrace();
        }
        System.out.println("============================== loginByOther ... , result: " + new Result(map).toString());
        return new Result(map);
    }
	
	/*private void loginByOther2xy(OpenLogin openLogin, String ssoid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name",openLogin.getNickname()));
		params.add(new BasicNameValuePair("oid",openLogin.getOid()));
		params.add(new BasicNameValuePair("ssoid",ssoid));
		params.add(new BasicNameValuePair("headImg",openLogin.getHead()));
		String type = "1";
		if("facebook".equalsIgnoreCase(openLogin.getProvider())){
			type = "1";
		}else if("google".equalsIgnoreCase(openLogin.getProvider())){
			type = "2";
		}else if("twitter".equalsIgnoreCase(openLogin.getProvider())){
			type = "3";
		}else if("tencent_wechat".equalsIgnoreCase(openLogin.getProvider())){
			type = "4";
		}
		params.add(new BasicNameValuePair("type",type));
		String res = new HttpClientUtil().callAmucAPI("/api/member/syn/loginByOther.do", params);
		JSONObject resJson = JSONObject.parseObject(res);
		if(!"1".equals(resJson.getString("code"))){
			throw new BusinessException("user account exception", "调用amuc同步接口失败");
		}
	}*/

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result login(@Valid Logout logout) {
        String data = jedisClient.get(logout.getDevid());
        if (data == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist_or_quit", null, slp.getLocale()));
        }
        if (!data.equals(logout.getToken() + "\t" + logout.getUid())) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_illegal", null, slp.getLocale()));
        }
        jedisClient.del(logout.getDevid());
        return new Result();
    }

    @RequestMapping(value = "/getPortrait", method = RequestMethod.GET)
    public void getPortrait(@RequestParam(value = "uid") Long uid,
                            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("phoneunregister", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        if (user.getAvatarMiddle() == null || "".equals(user.getAvatarMiddle())) {
            throw new BusinessException("user info exception", "用户头像地址为空");
        }
        String defaultUrl = FaceUtil.default_avatarMiddle;
        if (defaultUrl.equals(user.getAvatarMiddle())) {
            System.out.println(defaultUrl);
            response.sendRedirect(defaultUrl);
        } else {
            System.out.println(user.getAvatarMiddle());
            response.sendRedirect(user.getAvatarMiddle());
        }
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(@RequestParam(required = true) Long uid,
                         @RequestParam(required = true) String nickname,
                         @RequestParam(required = false) String sex,
                         @RequestParam(required = false) String birthday,
                         @RequestParam(required = false) String address, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        nickname = nickname.trim();
        Preconditions.checkArgument(nickname.length() <= 32, "昵称长度不能大于32");
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }

        //调用amuc同步接口
		/*if(sex.equals("")||sex==null){
			sex="0";
		}*/
        String reg = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        if (!StringUtils.isEmpty(birthday)) {
            boolean matches = Pattern.compile(reg).matcher(birthday).matches();
            if (!matches) {
                throw new BusinessException("date format error",
                        messageSource.getMessage("出生日期格式不正确！", null, slp.getLocale()));
            }
            //birthday = birthday.concat(" 00:00:00");
        }

        System.out.println("============================== UserController modify, nickname : " + nickname);
        if (!StringUtils.isEmpty(nickname) && !ValidateUtil.CheckNickName(nickname)) {
            StringBuilder ss = new StringBuilder("修改失败！");
            ss.append("昵称不支持包含表情");
            throw new BusinessException("nickName format error", ss.toString());
        }

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", user.getPhone()));
            params.add(new BasicNameValuePair("email", user.getEmail()));
            params.add(new BasicNameValuePair("nickname", nickname));
            params.add(new BasicNameValuePair("sex", sex));
            params.add(new BasicNameValuePair("birthday", birthday));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("uid", String.valueOf(user.getId())));
            String res = new HttpClientUtil().callAmucAPI("/api/member/syn/modify.do", params);
            JSONObject resJson = JSONObject.parseObject(res);
            if (!"1".equals(resJson.getString("code"))) {
                return new Result(2, resJson.getString("msg"));
            }
            user.setNickname(nickname);
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("nickName format error", "用户修改资料失败");
        }
        return new Result();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result save(
            @RequestParam(required = true) String nickname,
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String political,
            @RequestParam(required = true) String address,
            @RequestParam(required = true) String mobile,
            @RequestParam(required = true) String phone,
            @RequestParam(required = true) String num,
            @RequestParam(required = true) String cardNo,
            @RequestParam(required = true) String jOrgName,
            @RequestParam(required = true) String cOrgName,
            @RequestParam(required = true) String kOrgName,
            @RequestParam(required = true) String position,
            @RequestParam(required = true) String orgId,
            @RequestParam(required = true) String pwd) {
        User user = userService.findByPhone(mobile);
//		LocalPrincipal principal = localPrincipalService.getByUserId()
        if (user != null) {
            user.setNickname(nickname);
            user.setPhone(phone);
            user.setEmail(email);
        } else {
            user = new User();
            user.setNickname(nickname);
            user.setPhone(mobile);
            user.setEmail(email);
            user.setAvatarSmall("/static/images/face/default_avatarSmall.jpg");
            user.setAvatarMiddle("/static/images/face/default_avatarMiddle.jpg");
            user.setAvatarLarge("/static/images/face/default_avatarLarge.jpg");
//			user.
        }

        userService.save(user);
        return new Result();
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public Result updatePassword(@RequestParam(required = true) Long uid,
                                 @RequestParam(required = true) String password,
                                 @RequestParam(required = true) String newPassword) {
        password = password.trim();
        newPassword = newPassword.trim();
        Preconditions.checkArgument(!newPassword.equals(password),
                messageSource.getMessage("new_old_pwd_cannot_repeat", null, slp.getLocale()));
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        LocalPrincipal localPrincipal = user.getLocalPrincipal();
        if (localPrincipal == null) {
            throw new BusinessException("user account exception", "第三方账号不能更新密码");
        }
        if (!userService.isPasswordMatch(localPrincipal, password)) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("updatePassword_oldpassworderror", null, slp.getLocale()));
        }

        //调用amuc同步接口
        String if_to_amuc = SystemConfigHolder.getConfig("if_to_amuc");
        if (if_to_amuc != null && if_to_amuc.equals("2")) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", user.getPhone()));
            params.add(new BasicNameValuePair("password", newPassword));
            String res = new HttpClientUtil().callAmucAPI("/api/member/syn/updatePassword.do", params);
            JSONObject resJson = JSONObject.parseObject(res);
            if (!"1".equals(resJson.getString("code"))) {
                return new Result(2, resJson.getString("msg"));
            }
        }
        localPrincipalService.resetPassword(localPrincipal, newPassword);
        return new Result();
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public Result resetPassword(@RequestParam(value = "email", required = true) String email,
                                @RequestParam(value = "password", required = true) String password) {
        password = password.trim();
        //User user = userService.findByPhone(phone);
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        LocalPrincipal localPrincipal = user.getLocalPrincipal();
        if (localPrincipal == null) {
            throw new BusinessException("user account exception", "开放账号不能更新密码");
        }

        //调用amuc同步接口
        String if_to_amuc = SystemConfigHolder.getConfig("if_to_amuc");
        if (if_to_amuc != null && if_to_amuc.equals("2")) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", user.getEmail()));
            params.add(new BasicNameValuePair("password", password));
            String res = new HttpClientUtil().callAmucAPI("/api/member/syn/updatePassword.do", params);
            JSONObject resJson = JSONObject.parseObject(res);
            if (!"1".equals(resJson.getString("code"))) {
                return new Result(2, resJson.getString("msg"));
            }
        }
        localPrincipalService.resetPassword(localPrincipal, password);
        return new Result();
    }

    @RequestMapping(value = "/bindFromOther", method = RequestMethod.POST)
    public Result bindFromOther(@RequestParam("uid") Long uid,
                                @Valid Login login) {
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception", "开放账号不存在");
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception", "开放账号被禁用");
        }
        if (user.getLocalPrincipal() != null) {
            throw new BusinessException("user account exception", "开放账号不合法");
        }

        User localUser = userService.findByPhone(login.getPhone());
        if (localUser == null) {
            throw new BusinessException("user account exception", "手机号未被注册");
        }
        if (!localUser.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        boolean match = localPrincipalService.isPasswordMatch(localUser.getLocalPrincipal(), login.getPassword());
        if (!match) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("name_or_pwd_error", null, slp.getLocale()));
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        jedisClient.set(login.getDevid(), token + "\t" + user.getId(), (365 * 24 * 60 * 60));

        userService.bindUser(user, localUser);
        Map<String, Object> map = Maps.newHashMap();
        getuserid(map, localUser.getId().toString());
        map.put("token", token);
        map.putAll(localUser.asMap());

        return new Result(map);
    }

    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    public Result bind(@RequestParam(value = "uid", required = true) Long uid,
                       @Valid OpenUser openUser,
                       @RequestParam(required = true) String nickname) {
        nickname = nickname.trim();
        Preconditions.checkArgument(nickname.length() <= 32, "昵称长度不能大于32");
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        if (user.getLocalPrincipal() == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_illegal", null, slp.getLocale()));
        }

        UserOauthBinding binding = userService.findBinding(openUser.getOid(), openUser.getProvider());
        if (binding != null) {
            throw new BusinessException("user bind exception", "开放账号已绑定该平台用户");
        }
        binding = userOauthBingService.findByUserIdAndProvider(user.getId(), openUser.getProvider());
        if (binding != null) {
            throw new BusinessException("user bind exception", "用户已经绑定该平台其他开放账号");
        }

        binding = new UserOauthBinding();
        binding.setBindTime(Clock.DEFAULT.getCurrentDate());
        binding.setUser(user);
        binding.setProvider(openUser.getProvider());
        binding.setOauthUid(openUser.getOid());
        binding.setNickname(nickname);
        userOauthBingService.save(binding);

        Set<UserOauthBinding> bindings = user.getUserOauthBindings();
        Map<String, Object> map = Maps.newHashMap();
        for (UserOauthBinding bind : bindings) {
            Map<String, Object> data = Maps.newHashMap();
            data.put("nickname", bind.getNickname());
            data.put("oid", bind.getOauthUid());
            map.put(bind.getProvider(), data);
        }

        return new Result(map);
    }

    @RequestMapping(value = "/unbind", method = RequestMethod.POST)
    public Result bind(@RequestParam(required = true) Long uid,
                       @Valid OpenUser openUser) {
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        if (user.getLocalPrincipal() == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_illegal", null, slp.getLocale()));
        }

        UserOauthBinding binding = userService.findByUserIdAndOauthUidAndProvider(user.getId(), openUser.getOid(), openUser.getProvider());
        if (binding == null) {
            throw new BusinessException("user bind exception", "开放账号位于该用户绑定，无法解绑");
        }

        userOauthBingService.delete(binding);

        Set<UserOauthBinding> bindings = user.getUserOauthBindings();
        Map<String, Object> map = Maps.newHashMap();
        for (UserOauthBinding bind : bindings) {
            Map<String, Object> data = Maps.newHashMap();
            data.put("nickname", bind.getNickname());
            data.put("oid", bind.getOauthUid());
            map.put(bind.getProvider(), data);
        }

        return new Result(map);
    }

    @RequestMapping(value = "/openAccounts", method = RequestMethod.POST)
    public Result openAccounts(@RequestParam(required = true) Long uid) {
        System.out.println("============================== openAccounts ... , uid: " + uid);
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_notexist", null, slp.getLocale()));
        }
        if (!user.isActived()) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_disabled", null, slp.getLocale()));
        }
        if (user.getLocalPrincipal() == null) {
            throw new BusinessException("user account exception",
                    messageSource.getMessage("user_illegal", null, slp.getLocale()));
        }

        Set<UserOauthBinding> bindings = user.getUserOauthBindings();
        Map<String, Object> map = Maps.newHashMap();
        for (UserOauthBinding binding : bindings) {
            Map<String, Object> data = Maps.newHashMap();
            data.put("nickname", binding.getNickname());
            data.put("oid", binding.getOauthUid());
            map.put(binding.getProvider(), data);
        }
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("mobile",user.getPhone()));
//		String res = new HttpClientUtil().callAmucAPI("/api/member/syn/openAccounts", params);
//		//把json数据转换成map
//		JSONObject jsonMap = JSONObject.parseObject(res);
//		Set<String> set = jsonMap.keySet();
//		Iterator<String> it = set.iterator();
//		while(it.hasNext()){
//			String key = (String) it.next();
//			map.put(key, jsonMap.get(key));
//		}
        return new Result(map);
    }

    public void getuserid(Map<String, Object> map, String id) {
        try {
            //调amuc接口,像amuc中保存用户信息
            String inner_api_url = "";
            if (System.getenv("INNER_API_URL") != null && System.getenv("INNER_API_URL") != "") {
                inner_api_url = System.getenv("INNER_API_URL");
            } else {
                inner_api_url = SystemConfigHolder.getConfig("inner_api_url");
            }
            String httpOrgCreateTest = inner_api_url + "/api/member/getUserMessage.do";
            String charset = "utf-8";
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("ssoid", id));
            System.out.println("获取到的url:" + httpOrgCreateTest);
            String httpOrgCreateTestRtn = new HttpClientUtil().doPost(httpOrgCreateTest, formparams, charset);
            System.out.println("result:" + httpOrgCreateTestRtn);
            JSONObject obj = JSONObject.parseObject(httpOrgCreateTestRtn);
            if (obj.getString("code").equals("1")) {
                map.put("userid", obj.getString("uid"));
                map.put("score", obj.getString("mScore"));
                map.put("sex", obj.getString("sex") == null ? "" : obj.getString("sex"));
                map.put("birthday", obj.getString("birthday") == null ? "" : obj.getString("birthday"));
                map.put("head", obj.getString("mHead"));
                map.put("address", obj.getString("address") == null ? "" : obj.getString("address"));
                map.put("nickname", obj.getString("nickname") == null ? "" : obj.getString("nickname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 9、根据用户uid_sso,删除用户
     *
     * @param uid_sso
     * @return
     * @throws JSONException
     */
    @RequestMapping("/delete")
    public ResponseEntity<String> deleteUserById(String uid_sso) {
        System.out.println("--- SSO：删除用户接口开始 ---");
        System.out.println("参数：uid_sso=" + uid_sso);
        userService.delete(Long.parseLong(uid_sso));
        JSONObject json = new JSONObject();
        String jsonStr = json.toString();
        System.out.println("--- SSO：删除用户接口结束 ---");
        ResponseEntity<String> re = new ResponseEntity<String>(jsonStr, HttpStatus.OK);
        return re;
    }

    @RequestMapping("/changePhone")
    public Result changePhone(String uid, String newPhone) {
        System.out.println("---进入修改手机号接口，参数：uid=" + uid + " newPhone=" + newPhone);
        User check = userService.findByPhone(newPhone);
        if (check != null) {
            throw new BusinessException("user account exception", "新手机号已存在!");
        }
        User user = userService.findUserById(Long.parseLong(uid));
        user.setPhone(newPhone);
        user.getLocalPrincipal().setPhone(newPhone);
        userService.save(user);
        System.out.println("------------------完成修改手机号接口-----------------------");
        return new Result(user.asMap());
    }

    //邮箱的正则表达式验证
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //手机号的正则表达式验证
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile("^0?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[0-9])[\\d]{8}$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //用户名的正则表达式验证
    public static boolean checkUserName(String userName) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{2,20}$");
            Matcher matcher = regex.matcher(userName);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //是否含有小写字母
    public boolean judgeContainsStr(String cardNum) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }

    /**
     * 发送手机/邮箱验证码
     * type:phone 手机；email 邮箱
     * value：手机号或邮箱
     */
    @RequestMapping(value = "sendCode")
    public Result sendCode(String type, String value, String useType, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=gbk");//第二句，设置浏览器端解码
        String code = StringUtil.getRandom(6);
        System.out.println(type + "===================== 号码为" + value + "的验证码：" + code + " =======================");
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(useType) || StringUtils.isEmpty(type) || StringUtils.isEmpty(value)) {
            map.put("code", "fail");
            map.put("msg", "参数 useType、value、useType不能为空");
            return new Result(map);
        }

        List<SystemConfig> systemConfigs = (List<SystemConfig>) systemConfigDao.findAll();
        if ("phone".equals(type)) {
            // 检查email格式是否正确
            if (!ValidateUtil.CheckPhone(value)) {
                map.put("code", "fail");
                map.put("msg", "手机号码格式不正确");
                return new Result(map);
            }

            User u = userService.getUserByField(value, "phone");
            if (useType.equals("0")) {
                //是否已经注册
                if (u != null) {
                    map.put("code", "fail");
                    map.put("msg", "该号码已被注册！");
                    return new Result(map);
                }
            } else {
                //找回密码
                if (u == null) {
                    map.put("code", "fail");
                    map.put("msg", "无法找回密码，该号码未注册！");
                    return new Result(map);
                }
            }

            //TODO 发送手机验证码
            String SERVER = "";
            String PORT = "";
            String ACCOUNT_SID = "";
            String AUTH_TOKEN = "";
            String AppId = "";
            for (SystemConfig systemConfig : systemConfigs) {
                if (systemConfig.getScode().equals("SERVER"))
                    SERVER = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("PORT"))
                    PORT = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("ACCOUNT_SID"))
                    ACCOUNT_SID = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("AUTH_TOKEN"))
                    AUTH_TOKEN = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("AppId"))
                    AppId = systemConfig.getSstatus();
            }
            Properties prop = new Properties();
            FileOutputStream oFile = null;
            try {
                ///保存属性到properties文件
                oFile = new FileOutputStream("msgsend.properties", true);
                prop.setProperty("SERVER", SERVER);
                prop.setProperty("PORT", PORT);
                prop.setProperty("ACCOUNT_SID", ACCOUNT_SID);
                prop.setProperty("AUTH_TOKEN", AUTH_TOKEN);
                prop.setProperty("AppId", AppId);
                prop.store(oFile, "The New properties file");
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (oFile != null) {
                    try {
                        oFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            cacheCode("phonecode::", value, code, session);
            logger.info("cacheCode: {}", new Object[]{code});
            ActsocialMsgSender msgSender = new ActsocialMsgSender();
            msgSender.sendMsgCode(value, code);

        } else if ("email".equals(type)) {
            // 检查email格式是否正确
            if (!ValidateUtil.CheckEmail(value)) {
                map.put("code", "fail");
                map.put("msg", "email格式不正确");
                return new Result(map);
            }

            if (value.indexOf("@qq.") != -1 && !ValidateUtil.CheckEmail_QQ(value)) {
                map.put("code", "fail");
                map.put("msg", "qq邮箱无效！");
                return new Result(map);
            }

            User u = userService.getUserByField(value, "email");
            if (useType.equals("0")) {
                //是否已经注册
                if (u != null) {
                    map.put("code", "fail");
                    map.put("msg", "该邮箱已被注册！");
                    return new Result(map);
                }
            } else {
                //找回密码
                if (u == null) {
                    map.put("code", "fail");
                    map.put("msg", "无法找回密码，该邮箱未注册！");
                    return new Result(map);
                }
            }

            for (SystemConfig systemConfig : systemConfigs) {
                if (systemConfig.getScode().equals("emailHost"))
                    ActsocialMailSender.emailHost = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("email_userName"))
                    ActsocialMailSender.userName = systemConfig.getSstatus();
                if (systemConfig.getScode().equals("email_password"))
                    ActsocialMailSender.password = systemConfig.getSstatus();
            }
            cacheCode("emailcode::", value, code, session);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("code", code);
            try {
                //ActsocialMailSender.sendMail(value, code, useType);
                ActsocialMailSender.sendMailByGmail(value, code, useType);
            } catch (Exception e) {
                e.printStackTrace();
                String exceptionMsg = e.getMessage();
                Exception nextException = ((MessagingException) e).getNextException();
                if (nextException != null) {
                    exceptionMsg = nextException.getMessage();
                }
                map.put("code", "fail");
                map.put("msg", "发送邮件失败！" + (exceptionMsg.indexOf("Connection timed out") != -1 ? exceptionMsg : ""));
                return new Result(map);
            }
        }
        map.put("code", "success");
        map.put("msg", "验证码已发送");
        return new Result(map);
    }

    private void cacheCode(final String prefix, final String value, final String code, final HttpSession session) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String codeTime = SystemConfigHolder.getConfig("codeTime"); //redis中验证码有效期
				jedisClient.set(prefix+value, code, Integer.parseInt(codeTime));
				System.out.println("============= cacheCode into jedis, emial: " + prefix+value + ", code: " + jedisClient.get(prefix+value) +
						", sessionId: " + session.getId() + ", ======================");
			}
		});


        t.start();
        try {
            t.join(1000);
            if (t.isAlive()) {
                session.setAttribute(prefix + value, code);
                System.out.println("============ cacheCode into session, emial: " + prefix + value + ", code: " + session.getAttribute(prefix + value) +
                        ", sessionId: " + session.getId() + " ======================");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (session != null && session.getAttribute(prefix + value) != null) {
                            session.removeAttribute(prefix + value);
                            System.out.println("============= cacheCode remove from session, key: " + prefix + value + " ======================");
                        }
                    }
                }).start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查验证码是否输入正确。
     */
    @RequestMapping(value = "checkCode")
    public Result checkCode(String type, String value, String inputCode, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");//第二句，设置浏览器端解码
        Map<String, Object> map = Maps.newHashMap();
        map.put("msg", "验证码错误");
        map.put("code", "fail");
        //  处理手机注册及邮箱注册 手机注册比对验证码
        String codeName = "";
        if ("email".equals(type))
            codeName = "emailcode::" + value;
        else
            codeName = "phonecode::" + value;

        String sessionCode = getCachedCode(codeName, session);
        if (StringUtils.isEmpty(inputCode)) {
            map.put("msg", "验证码不能为空");
            return new Result(map);
        }

        if (StringUtils.isEmpty(sessionCode)) {
            map.put("msg", "验证码已失效");
        } else if (inputCode.trim().equalsIgnoreCase(sessionCode.trim())) {
            map.put("msg", "验证码正确");
            map.put("code", "success");
        }
        return new Result(map);
    }

    private String getCachedCode(final String codeName, final HttpSession session) {

        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<String> f = exec.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                System.out.println("============ getCachedCode from redis, emial: " + codeName + ", code: " +
                        ", sessionId: " + session.getId() + jedisClient.get(codeName));
                return jedisClient.get(codeName);
            }
        });
        try {
            String code = f.get(10, TimeUnit.SECONDS);
            System.out.println("============ getCachedCode form future, emial: " + codeName + ", code: " + code + ", sessionId: " + session.getId() + " ======================");
            return code;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Object attribute = session.getAttribute(codeName);
        System.out.println("============ getCachedCode from session, emial: " + codeName + ", code: " +
                (String) attribute + ", sessionId: " + session.getId() + " ======================");
        if (attribute != null) {
            session.removeAttribute(codeName);
            System.out.println("============= cacheCode remove from session, key: " + codeName + ", sessionId: " + session.getId() + " ======================");
        }
        return attribute == null ? null : (String) attribute;
    }

}
