package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 初始化能得到该对象，包含用户信息
 * @author qiao9
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Init {
	@JsonProperty(value = "SyncKey")
	private SyncKeyList syncKeyList;
	@JsonProperty(value = "User")
	private User user;
	@JsonProperty(value = "BaseResponse")
	private BaseResponse baseResponse;
}
