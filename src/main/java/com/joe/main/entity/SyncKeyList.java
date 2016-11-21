package com.joe.main.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncKeyList {
	@JsonProperty(value="Count")
	private Integer count;
	@JsonProperty(value="List")
	private List<SyncKey> list;
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public List<SyncKey> getList() {
		return list;
	}
	public void setList(List<SyncKey> list) {
		this.list = list;
	}
	
}
