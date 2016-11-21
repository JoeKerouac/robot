package com.joe.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略这个类中不存在的key-value
public class WxMsgBody {
	@JsonProperty(value = "MsgId")
	private String msgId;
	/**
	 * 格式：
	 * 
	 * @@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9或者@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9 如果是群消息就是第一种
	 *                                                                                                                                       ，
	 *                                                                                                                                       如果是个人消息就是第二种
	 */
	@JsonProperty(value = "FromUserName")
	private String fromUserName;
	/**
	 * 格式：@1236ff1781b5ae3a7009cd9216db293bc6779587a79896d8fec45771bab61fa9
	 * 注意：fromUserName是两个@，而toUserName是一个@
	 */
	@JsonProperty(value = "ToUserName")
	private String toUserName;
	/**
	 * 1：文本消息 3：图片 34：语音 43：视频 47：表情符号 10000：系统提示（已知红包消息是这个）
	 */
	@JsonProperty(value = "MsgType")
	private Integer msgType;
	/**
	 * 文本消息的格式： @7565bd3dd47d15972f58eb8bdb6bb695:<br/>
	 * 出来报个信，或者是 其中@7565bd3dd47d15972f58eb8bdb6bb695是用户的ID，:<br/>
	 * 是固定格式，出来报个信是消息内容，QQ表情属于文本消息，例如[微笑]
	 */
	@JsonProperty(value = "Content")
	private String content;
	@JsonProperty(value = "Status")
	private Integer status;
	@JsonProperty(value = "ImgStatus")
	private Integer imgStatus;
	@JsonProperty(value = "CreateTime")
	private Long createTime;
	@JsonProperty(value = "VoiceLength")
	private Integer voiceLength;
	@JsonProperty(value = "PlayLength")
	private Integer playLength;
	@JsonProperty(value = "FileName")
	private String fileName;
	@JsonProperty(value = "FileSize")
	private String fileSize;
	@JsonProperty(value = "MediaId")
	private String mediaId;
	@JsonProperty(value = "Url")
	private String url;
	@JsonProperty(value = "AppMsgType")
	private Integer appMsgType;
	@JsonProperty(value = "StatusNotifyCode")
	private Integer statusNotifyCode;
	@JsonProperty(value = "StatusNotifyUserName")
	private String statusNotifyUserName;
	@JsonProperty(value = "ForwardFlag")
	private Integer forwardFlag;
	@JsonProperty(value = "ImgHeight")
	private Integer imgHeight;
	@JsonProperty(value = "ImgWidth")
	private Integer imgWidth;
	@JsonProperty(value = "NewMsgId")
	private Long newMsgId;
}
