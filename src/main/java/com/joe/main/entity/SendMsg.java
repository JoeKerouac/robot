package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 发送的消息体
 * 
 * @author qiao9
 *
 */
@Data
public class SendMsg {
	@JsonProperty(value = "Type")
	private int type;
	@JsonProperty(value = "Content")
	private String content;
	@JsonProperty(value = "ToUserName")
	private String to;
	@JsonProperty(value = "FromUserName")
	private String from;
	@JsonProperty(value = "LocalID")
	private String localId;
	@JsonProperty(value = "ClientMsgId")
	private String clientMsgId;
}
