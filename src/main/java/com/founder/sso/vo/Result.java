package com.founder.sso.vo;

import java.util.Collections;
import java.util.Map;

public class Result {

	private int code;
	
	private String msg;
	
	private Map<String,Object> value;

	public Result(){
		this.code = 1;
		this.msg = "success";
		this.value = Collections.EMPTY_MAP;
	}
	
	public Result(Map<String, Object> value) {
		this.code = 1;
		this.msg = "success";
		this.value = value;
	}

	public Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, Object> getValue() {
		return value;
	}

	public void setValue(Map<String, Object> value) {
		this.value = value;
	}
	
}
