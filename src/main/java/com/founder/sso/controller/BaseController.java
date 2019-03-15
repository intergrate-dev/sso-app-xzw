package com.founder.sso.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.RedirectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.founder.sso.service.LocalPrincipalService;
import com.founder.sso.service.SensitiveWordService;
import com.founder.sso.service.UserOauthBingService;
import com.founder.sso.service.UserService;
import com.founder.sso.util.Clock;
import com.google.common.collect.Maps;

@EnableWebMvc
@ControllerAdvice
public abstract class BaseController {
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected LocalPrincipalService localPrincipalService;
	
	@Autowired
	protected SensitiveWordService sensitiveWordService;
	
	@Autowired
	protected UserOauthBingService userOauthBingService;
	
    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> requestHandlingNoHandlerFound(HttpServletRequest req, NoHandlerFoundException ex) {
        Map<String, Object> map = Maps.newHashMap();
		map.put("code", 0);
		map.put("status", HttpStatus.NOT_FOUND);
		map.put("path", ex.getRequestURL());
		map.put("timestamp", Clock.DEFAULT.getCurrentTimeInMillis());
        map.put("msg", "url not found");
        map.put("error", ex.getMessage());
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

}

