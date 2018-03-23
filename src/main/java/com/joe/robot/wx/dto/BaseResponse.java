package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@Data
public class BaseResponse {
	@JsonProperty(value = "Ret")
	private Integer ret;
	@JsonProperty(value = "ErrMsg")
	private String errMsg;
}
