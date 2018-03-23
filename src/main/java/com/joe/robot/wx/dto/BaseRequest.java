package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseRequest {
    @JsonProperty(value = "Uin")
    private Long uin;
    @JsonProperty(value = "Sid")
    private String sid;
    @JsonProperty(value = "Skey")
    private String skey;
    @JsonProperty(value = "DeviceID")
    private String deviceId;
}
