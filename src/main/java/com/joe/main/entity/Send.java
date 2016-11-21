package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 要发送的消息对象
 * 
 * @author qiao9
 *
 */
@Data
public class Send {
	@JsonProperty(value = "BaseRequest")
	private BaseRequest request;
	@JsonProperty(value = "Msg")
	private SendMsg msg;
	@JsonProperty(value = "Scene")
	private int scene;
}
