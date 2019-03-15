package com.founder.sso.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

public class ExceptionI18Message {

	public static String getLocaleMessage(String key){
        HttpServletRequest request =  ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        RequestContext requestContext = new RequestContext(request);
        return requestContext.getMessage(key,key);
    }
}
