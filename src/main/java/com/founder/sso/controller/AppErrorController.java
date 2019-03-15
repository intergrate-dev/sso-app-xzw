package com.founder.sso.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.founder.sso.exception.BusinessException;
import com.founder.sso.util.Clock;
import com.founder.sso.util.ExceptionI18Message;
import com.google.common.collect.Maps;

@RestController
public class AppErrorController implements ErrorController {

	private final static String ERROR_PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	@RequestMapping(value = ERROR_PATH)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
		HttpStatus status = getStatus(request);
		body.put("status", status.value());
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null) {
			try {
				return HttpStatus.valueOf(statusCode);
			} catch (Exception ex) {
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}
	
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}
	
	private String getPath(RequestAttributes requestAttributes) {
		return getAttribute(requestAttributes, "javax.servlet.error.request_uri");
	}

	private BindingResult extractBindingResult(Throwable error) {
		if (error instanceof BindingResult) {
			return (BindingResult) error;
		}
		if (error instanceof MethodArgumentNotValidException) {
			return ((MethodArgumentNotValidException) error).getBindingResult();
		}
		return null;
	}
	
	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Throwable throwable = errorAttributes.getError(requestAttributes);
		Map<String, Object> map = Maps.newHashMap();
		map.put("code", 0);
		map.put("path", getPath(requestAttributes));
		map.put("timestamp", Clock.DEFAULT.getCurrentTimeInMillis());
		if(throwable==null){
			return map;
		}
		
		BindingResult result = extractBindingResult(throwable);
		if(result != null && result.hasErrors()){
			Map<String, Object> fieldErrorMap = new HashMap<String, Object>();
	        List<FieldError> fieldErrors = result.getFieldErrors();
	        for (FieldError fieldError: fieldErrors) {
	            fieldErrorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
	        }
	        map.put("msg", "invalid request params");
	        map.put("error", fieldErrorMap);
	        return map;
		}else if(throwable instanceof BusinessException){
			BusinessException ex = (BusinessException) throwable;
			map.put("msg", ex.getExceptionType());
	        map.put("error", ExceptionI18Message.getLocaleMessage(throwable.getMessage()));
		}else{
			map.put("msg", "process exception");
			map.put("error", throwable.getMessage());
		}
		return map;
	}

}
