package com.joe.robot.wx.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncKeyList {
	@JsonProperty(value="Count")
	private Integer count;
	@JsonProperty(value="List")
	private List<SyncKey> list;
}
