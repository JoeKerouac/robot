package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SyncKey {
	/*
	 * 消息类型
	 */
	@JsonProperty(value = "Key")
	private Integer key;
	/*
	 * 消息数目
	 */
	@JsonProperty(value = "Val")
	private Integer val;
	/**
	 * 消息数目加上value
	 * @param value
	 */
	public void add(int value){
		this.val += value;
	}
}
