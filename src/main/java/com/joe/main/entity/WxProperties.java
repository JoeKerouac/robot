package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WxProperties {
	private String skey;
	private String wxsid;
	private String wxuin;
	private String pass_ticket;

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getWxsid() {
		return wxsid;
	}

	public void setWxsid(String wxsid) {
		this.wxsid = wxsid;
	}

	public String getWxuin() {
		return wxuin;
	}

	public void setWxuin(String wxuin) {
		this.wxuin = wxuin;
	}

	public String getPass_ticket() {
		return pass_ticket;
	}

	public void setPass_ticket(String pass_ticket) {
		this.pass_ticket = pass_ticket;
	}

}
