package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendStatus {
	@JsonProperty(value = "BaseResponse")
	private BaseResponse baseResponse;
	@JsonProperty(value = "MsgID")
	private String msgId;
	@JsonProperty(value = "LocalID")
	private String localId;
}
