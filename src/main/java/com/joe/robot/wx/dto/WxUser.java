package com.joe.robot.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WxUser {
	//暂时不知道有什么用
	@JsonProperty(value = "Uin")
	private Long uin;
	//微信ID，例如：@54b833106d59c9d64d0b524865b78b8ab08334c88d9dd0100b0982d17e5590a1
	@JsonProperty(value = "UserName")
	private String userName;
	//昵称
	@JsonProperty(value = "NickName")
	private String nickName;
	//头像，例如：/cgi-bin/mmwebwx-bin/webwxgeticon?seq=191885221&username=@54b833106d59c9d64d0b524865b78b8ab08334c88d9dd0100b0982d17e5590a1&skey=@crypt_d011c14e_88e4f975839441d2de8337ededbede3f
	@JsonProperty(value = "HeadImgUrl")
	private String headImgUrl;
	@JsonProperty(value = "RemarkName")
	private String remarkName;
	/**
	 * 群用户有该项，其他用户没有，群成员列表
	 */
	@JsonProperty(value = "MemberList")
	private List<WxUser> memberList;
	/**
	 * 群用户有该项，其他用户没有，群成员数量
	 */
	@JsonProperty(value = "MemberCount")
	private Integer memberCount;
	/**
	 * 1：男 2：女 0：群
	 */
	@JsonProperty(value = "Sex")
	private Integer sex;
	//签名
	@JsonProperty(value = "Signature")
	private String signature;
}
