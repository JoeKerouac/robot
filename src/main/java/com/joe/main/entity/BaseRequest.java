package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseRequest {
	@JsonProperty(value = "Uin")
	private Long uin;
	@JsonProperty(value = "Sid")
	private String sid;
	@JsonProperty(value = "Skey")
	private String skey;
	@JsonProperty(value = "DeviceID")
	private String deviceId;

	public Long getUin() {
		return uin;
	}

	public void setUin(Long uin) {
		this.uin = uin;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
