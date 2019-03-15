package com.founder.sso.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.founder.sso.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.founder.sso.dao.LocalPrincipalDao;
import com.founder.sso.entity.User;
import com.founder.sso.util.MD5Util;
import com.founder.sso.validate.Register;
import com.founder.sso.vo.Result;

@RestController
@Validated
@RequestMapping("/api/syn")
public class UserSynController extends BaseController {
	
	@Autowired
    LocalPrincipalDao dao;

	@RequestMapping(value="/register",method=RequestMethod.POST)
	public Result register(Register register) {
		register.setPassword(MD5Util.md5(register.getPassword()));
		if(userService.findByPhone(register.getPhone())!=null){
			return new Result(2,"手机号已经被注册");
		}
		
		User user = userService.register(register);String default_img = userService.setDefaultImg();

		user.setAvatarSmall(default_img);
		user.setAvatarMiddle(default_img);
		user.setAvatarLarge(default_img);
		JSONObject json = userService.synRegistToMember(user, "1", register.getPassword());

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("ssoid", user.getId());
		return new Result(map);
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.POST)
	public Result modify(@RequestParam(required=true) String phone, 
				@RequestParam(required=true) String username, 
				@RequestParam(required=true) String email,
				@RequestParam(required=true) String password, 
				@RequestParam(required=true) String nickname){
		User user = userService.findByPhone(phone);
		if(user==null){
			return new Result(2,"用户不存在");
		}
		user.setUsername(username);
		user.setNickname(nickname);
		StringBuilder ss = new StringBuilder("修改失败！");
		if (nickname.indexOf("\\x") != -1) {
			ss.append("昵称不支持包含表情");
			return new Result(2,ss.toString());
		}
		user.setEmail(email);
		userService.save(user);
		password = MD5Util.md5(password);
		localPrincipalService.resetPasswordByPhone(phone, password,username);
		return new Result();
	}
	
	@RequestMapping(value="/updatePwd", method=RequestMethod.POST)
	public Result updatePassword(@RequestParam(required=true) String phone, 
	          @RequestParam(required=true) String password){
		password = password.trim();
		User user = userService.findByPhone(phone);
		if(user==null){
			return new Result(2,"用户不存在");
		}
		password = MD5Util.md5(password);
		localPrincipalService.resetPasswordByPhone(phone, password);
		return new Result();
	}
	
	@RequestMapping(value="/loginByOther", method=RequestMethod.POST)
	public Result saveOtherInfo(User user){
		user.setRegisterDate(new Date());
		userService.save(user);
		return new Result();
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public Result delete(@RequestParam(required=true) String uid_sso){
		userService.delete(Long.parseLong(uid_sso));
		return new Result();
	}
	
	/**
	 * 翔宇会员中心发出的启用账号指令，通过接口同步到sso中
	 * @param uid_sso 会员的sso关联标识ssoid
	 * @return
	 */
	@RequestMapping(value="/activeUser", method=RequestMethod.POST)
	public Result activeUser(@RequestParam(required=true) String ssoid){
		userService.activeUser(Long.parseLong(ssoid));
		return new Result();
	}
	
	/**
	 * 翔宇会员中心发出的禁用账号指令，通过接口同步到sso中
	 * @param uid_sso 会员的sso关联标识ssoid
	 * @return
	 */
	@RequestMapping(value="/disableUser", method=RequestMethod.POST)
	public Result disableUser(@RequestParam(required=true) String ssoid){
		userService.disableUser(Long.parseLong(ssoid));
		return new Result();
	}
}
