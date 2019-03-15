package com.founder.sso.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_subsystem_binding")
public class UserSubsystemBind extends IdEntity implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String name;
	private int subsystemId;
	private int userId;
	private Date bindTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSubsystemid() {
		return subsystemId;
	}
	public void setSubsystemid(int subsystemid) {
		this.subsystemId = subsystemid;
	}
	public int getUserid() {
		return userId;
	}
	public void setUserid(int userid) {
		this.userId = userid;
	}
	public Date getBindtime() {
		return bindTime;
	}
	public void setBindtime(Date bindtime) {
		this.bindTime = bindtime;
	}
	
}
