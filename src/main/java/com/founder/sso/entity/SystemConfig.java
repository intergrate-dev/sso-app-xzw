package com.founder.sso.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "system_config")
public class SystemConfig extends IdEntity implements Serializable{

    private static final long serialVersionUID = -3938998426078622254L;
    private String sname;
    private String scode;
    private String sstatus;
    private String sdescribe;
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getScode() {
		return scode;
	}
	public void setScode(String scode) {
		this.scode = scode;
	}
	public String getSstatus() {
		return sstatus;
	}
	public void setSstatus(String sstatus) {
		this.sstatus = sstatus;
	}
	public String getSdescribe() {
		return sdescribe;
	}
	public void setSdescribe(String sdescribe) {
		this.sdescribe = sdescribe;
	}
    
	
}
