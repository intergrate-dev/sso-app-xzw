package com.founder.sso.controller;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.founder.sso.component.RedisUtil;
import com.founder.redis.JedisClient;
import com.founder.sso.entity.SubsystemApp;
import com.founder.sso.entity.User;
import com.founder.sso.exception.RedirectException;
import com.founder.sso.service.SubsystemAppService;
import com.founder.sso.util.EncryptUtil;
import com.founder.sso.util.SystemConfigHolder;
import com.google.common.collect.Maps;

@Controller
@Validated
@RequestMapping("/sso")
public class RedirectController extends BaseController {

	@Autowired
	SubsystemAppService subsystemAppService;

	@Autowired
	JedisClient jedisClient;

	//接口停用
	@RequestMapping(value = "/ssoLogin")
	public String ssoLogin(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String redirectUrl, @RequestParam(required = true) String code) {
		try {
			SubsystemApp app = subsystemAppService.findByCode(code);
			if (app == null) {// code非法
				throw new RedirectException("redirect exception", "code is invalid");
			}
			if (app.getDomain() == null || app.getDomain().isEmpty()) {
				throw new RedirectException("redirect exception", "domain is null");
			}
			if (!redirectUrl.startsWith(app.getDomain())) {
				throw new RedirectException("redirect exception", "redirect url is't matched with the domain");
			}
			HttpSession session = request.getSession(true);
			Object sessionUid = session.getAttribute("uid");
			boolean isLogin = true;
			User user = null;
			if (sessionUid == null) {
				isLogin = false;
			}else{
				Long uid = Long.valueOf(sessionUid.toString());
				user = userService.findUserById(uid);
				if (user == null || !user.isActived()) {
					session.removeAttribute("uid");
					isLogin = false;
				}
			}
			if (!isLogin) {
				String path = SystemConfigHolder.getConfig("redirect_path");
				Long random = RandomUtils.nextLong(1000000000l, 10000000000l);
				Map<String, Object> data = Maps.newHashMap();
				data.put("redirectUrl", redirectUrl);
				data.put("random", random);
				data.put("code", code);
				cacheLogin(session, JSONObject.valueToString(data));
				return redirectToView(model,
						path + "?redirectUrl=" + redirectUrl + "&code=" + code + "&random=" + random,
						"/redirect");
			}
			return executeLogin(model, redirectUrl, code, app, session, user);
		} catch (Throwable th) {
			th.printStackTrace();
			model.addAttribute("toPage",redirectUrl);
			return redirectToException(model, th, "app/loginError");
		}
	}

	private String executeLogin(Model model, String redirectUrl, String code, SubsystemApp app, HttpSession session,
			User user) {
		List<SubsystemApp> subsystemList = subsystemAppService.findByEnabledTrue();
		model.addAttribute("subsystemList", subsystemList);
		String suffix = null;
		Map<String, Object> data = user.getIdentities();
		data.put("createTime", System.currentTimeMillis());
		data.put("_random", UUID.randomUUID().toString());
		data.put("code", code);
		String msg = JSONObject.valueToString(data);
		if ("none".equals(app.getEncryptType())) {
			try {
				suffix = URLEncoder.encode(msg, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				suffix = URLEncoder.encode(EncryptUtil.aesEncrypt(app.getSecretKey(), msg.getBytes("utf-8")),
						"utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cacheTicket(session, suffix);
		}
		model.addAttribute("ticket", suffix);
		return redirectToView(model, redirectUrl, "/login");
	}

	@RequestMapping(value = "/login")
	public String appLogin(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String redirectUrl, @RequestParam(required = true) String code,
			@RequestParam(required = true) Long random,  @RequestParam(required = true) String devid,
			@RequestParam(required = true) Long uid,  @RequestParam(required = true) String token) {
		try {
			SubsystemApp app = subsystemAppService.findByCode(code);
			if (app == null) {// code非法
				throw new RedirectException("redirect exception", "code is invalid");
			}
			if (app.getDomain() == null || app.getDomain().isEmpty()) {
				throw new RedirectException("redirect exception", "domain is null");
			}
			if (!redirectUrl.startsWith(app.getDomain())) {
				throw new RedirectException("redirect exception", "redirect url is't matched with the domain");
			}
			HttpSession session = request.getSession(true);
			Map<String, Object> data = Maps.newHashMap();
			data.put("redirectUrl", redirectUrl);
			data.put("random", random);
			data.put("code", code);
			if(existLogin(session, JSONObject.valueToString(data))){//合法的请求
				String loginInfo = jedisClient.get(devid);
				if(loginInfo == null){
					throw new RedirectException("redirect exception", "user is invalid");
				}
				String[] info = loginInfo.split("\t");
				if(info.length==2&&token.equals(info[0])&&uid.equals(Long.valueOf(info[1]))){//合法的用户
					session.setAttribute("uid", uid);
					return executeLogin(model, redirectUrl, code, app, session, userService.findUserById(uid));
				}else{
					throw new RedirectException("redirect exception", "user is invalid");
				}
			}else{
				throw new RedirectException("redirect exception", "login is invalid");
			}
		} catch (Throwable th) {
			th.printStackTrace();
			model.addAttribute("toPage",redirectUrl);
			return redirectToException(model, th, "/loginError");
		}
	}
	
	@RequestMapping(value = "/simpleLogin")
	public String simpleLogin(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) String code, @RequestParam(required = true) String devid,
			@RequestParam(required = true) Long uid,  @RequestParam(required = true) String token,
			@RequestParam(required = true) String redirectUrl) {
		System.out.println("uid :"+uid+"\ttoken :"+token+"\tdevid :"+devid);
		try {
			SubsystemApp app = subsystemAppService.findByCode(code);
			if (app == null) {// code非法
				throw new RedirectException("redirect exception", "code is invalid");
			}
			if (app.getDomain() == null || app.getDomain().isEmpty()) {
				throw new RedirectException("redirect exception", "domain is null");
			}
			if (!redirectUrl.startsWith(app.getDomain())) {
				throw new RedirectException("redirect exception", "redirect url is't matched with the domain");
			}
			HttpSession session = request.getSession(true);
			Object sessionUid = session.getAttribute("uid");
			if(token.isEmpty()&&uid==null){
				if(sessionUid != null)
					session.removeAttribute("uid");
				List<SubsystemApp> subsystemList = subsystemAppService.findByEnabledTrue();
				model.addAttribute("subsystemList", subsystemList);
				return redirectToView(model,redirectUrl,"/logout");
			}
			String loginInfo = jedisClient.get(devid);
			boolean isvalid = false;
			if(loginInfo != null){
				String[] info = loginInfo.split("\t");
				isvalid = info.length==2&&token.equals(info[0])&&uid.equals(Long.valueOf(info[1]));
			}
			if(!isvalid){
				if(sessionUid != null)
					session.removeAttribute("uid");
			}else if(sessionUid == null|| uid.longValue()!=Long.parseLong(sessionUid.toString())){//用户不一致，需要重新登录
				session.setAttribute("uid", uid);
				return executeLogin(model, redirectUrl, code, app, session, userService.findUserById(uid));
			}
			return redirectToView(model,redirectUrl,"/login");
		} catch (Throwable th) {
			th.printStackTrace();
			model.addAttribute("toPage",redirectUrl);
			return redirectToException(model, th, "/loginError");
		}
	}

	private String redirectToException(Model model, Throwable th, String view) {
		if (th instanceof RedirectException) {
			RedirectException ex = (RedirectException) th;
			model.addAttribute("type", ex.getExceptionType());
		} else {
			model.addAttribute("type", "process exception");
		}
		model.addAttribute("msg", th.getMessage());
		return view;
	}

	private void cacheLogin(final HttpSession session, final String value) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				jedisClient.set(session.getId(), value, (5 * 60));
			}
		});
		t.start();
		try {
			t.join(1000);
			if (t.isAlive()) {
				session.setAttribute(session.getId(), value);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(5 * 60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						session.removeAttribute(session.getId());
					}
				}).start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void cacheTicket(final HttpSession session, final String ticket) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				jedisClient.set(ticket, "", (5 * 60));
			}
		});
		t.start();
		try {
			t.join(1000);
			if (t.isAlive()) {
				session.setAttribute(ticket, "");
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(5 * 60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						session.removeAttribute(ticket);
					}
				}).start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean existLogin(final HttpSession session, final String value) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Boolean> f = exec.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String sid = jedisClient.get(session.getId());
				if (sid != null && sid.equals(sid)) {
//					redisUtil.del(sid);
					result = true;
				}
				return result;
			}
		});
		try {
			return f.get(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		Object sid = session.getAttribute(session.getId());
		if (sid != null && sid.toString().equals(value)) {
//			session.removeAttribute(session.getId());
			return true;
		}
		return false;
	}

	private String redirectToView(Model model, String redirectUrl,String view) {
		model.addAttribute("toPage", redirectUrl);
		return view;
	}

	@RequestMapping(value = "/ticket/isLegel", method = RequestMethod.POST)
	public ResponseEntity<String> isLegel(Model model, HttpServletRequest request,
			@RequestParam(required = true) String ticket) {
		Boolean result = existTicket(request, ticket);
		ResponseEntity<String> re = new ResponseEntity<String>(result.toString(), HttpStatus.OK);
		return re;
	}

	private boolean existTicket(final HttpServletRequest request, final String ticket) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Boolean> f = exec.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				if (jedisClient.exists(ticket)) {
//					redisUtil.del(ticket);
					result = true;
				}
				return result;
			}
		});
		try {
			return f.get(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession(true);
		if (session.getAttribute(ticket) != null) {
//			session.removeAttribute(ticket);
			return true;
		}
		return false;
	}

}
