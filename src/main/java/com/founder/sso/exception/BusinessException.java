package com.founder.sso.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String exceptionType;
	
	public BusinessException(String exceptionType,String message) {
		super(message);
		this.exceptionType = exceptionType;
	}
	
	public String getExceptionType(){
		return this.exceptionType;
	}

	
}
