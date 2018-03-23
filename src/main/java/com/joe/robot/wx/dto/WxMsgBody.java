package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WxMsgBody {
    @JsonProperty("MsgId")
    private String msgId;
    /**
     * 格式：
     *
     * @@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9
     * 或者@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9 如果是群消息就是第一种
     * ，
     * 如果是个人消息就是第二种
     */
    @JsonProperty("FromUserName")
    private String fromUserName;
    /**
     * 格式：@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9
     * 注意：fromUserName是两个@，而toUserName是一个@
     */
    @JsonProperty("ToUserName")
    private String toUserName;
    /**
     * 1：文本消息 3：图片 34：语音 37：好友添加 43：视频 47：表情符号 10000：系统提示（已知红包消息是这个） 10002：消息撤回
     */
    @JsonProperty("MsgType")
    private Integer msgType;
    /**
     * 文本消息的格式： @7565bd3dd47d15972f58eb8bdb6bb695:<br/>
     * 出来报个信，或者是 其中@7565bd3dd47d15972f58eb8bdb6bb695是用户的ID，:<br/>
     * 是固定格式，出来报个信是消息内容，QQ表情属于文本消息，例如[微笑]
     */
    @JsonProperty("Content")
    private String content;
    @JsonProperty("Status")
    private Integer status;
    @JsonProperty("ImgStatus")
    private Integer imgStatus;
    @JsonProperty("CreateTime")
    private Long createTime;
    @JsonProperty("VoiceLength")
    private Integer voiceLength;
    @JsonProperty("PlayLength")
    private Integer playLength;
    @JsonProperty("FileName")
    private String fileName;
    @JsonProperty("FileSize")
    private String fileSize;
    @JsonProperty("MediaId")
    private String mediaId;
    @JsonProperty("Url")
    private String url;
    @JsonProperty("AppMsgType")
    private Integer appMsgType;
    @JsonProperty("StatusNotifyCode")
    private Integer statusNotifyCode;
    @JsonProperty("StatusNotifyUserName")
    private String statusNotifyUserName;
    @JsonProperty("ForwardFlag")
    private Integer forwardFlag;
    @JsonProperty("ImgHeight")
    private Integer imgHeight;
    @JsonProperty("ImgWidth")
    private Integer imgWidth;
    @JsonProperty("NewMsgId")
    private Long newMsgId;
    //消息是添加好友时有该值
    @JsonProperty("RecommendInfo")
    private RecommendInfo recommendInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecommendInfo {
        //对应微信ID，例如：@5ede763ab0295296290a122cf016f2700d3eaf5a80dc625acff0ac6e8dce0c25
        @JsonProperty("UserName")
        private String userName;
        //对应昵称
        @JsonProperty("NickName")
        private String nickName;
        //对应QQ号
        @JsonProperty("QQNum")
        private String qqNum;
        //添加好友时填写的附加内容
        @JsonProperty("Content")
        private String content;
        //对应签名
        @JsonProperty("Signature")
        private String signature;
        //对应微信号，例如：qiao1213812243
        @JsonProperty("Alias")
        private String alias;
        @JsonProperty("Ticket")
        private String ticket;
    }
}
