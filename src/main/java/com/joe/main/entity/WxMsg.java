package com.joe.main.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 响应消息
 * @author qiao9
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WxMsg {
	private BaseResponse baseResponse;
	@JsonProperty(value = "AddMsgCount")
	private Integer addMsgCount;
	@JsonProperty(value = "AddMsgList")
	private List<WxMsgBody> addMsgList;
	@JsonProperty(value = "SyncKey")
	private SyncKeyList syncKeyList;
}
