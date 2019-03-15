package com.founder.sso.exception;

public class RedirectException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String exceptionType;
	
	public RedirectException(String exceptionType,String message) {
		super(message);
		this.exceptionType = exceptionType;
	}

	public String getExceptionType(){
		return this.exceptionType;
	}
}
