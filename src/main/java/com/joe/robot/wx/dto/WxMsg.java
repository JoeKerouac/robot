package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 响应消息
 *
 * @author qiao9
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
    //删除好友列表，当在手机端删除某个好友时会收到通知
    @JsonProperty(value = "DelContactList")
    private List<DelContact> delContactList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DelContact {
        @JsonProperty(value = "UserName")
        private String userName;
        @JsonProperty(value = "ContactFlag")
        private int contactFlag;
    }
}
