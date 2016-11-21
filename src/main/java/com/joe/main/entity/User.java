package com.joe.main.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	@JsonProperty(value = "Uin")
	private Long uin;
	/**
	 * 微信ID
	 */
	@JsonProperty(value = "UserName")
	private String userName;
	/**
	 * 昵称
	 */
	@JsonProperty(value = "NickName")
	private String nickName;
	@JsonProperty(value = "HeadImgUrl")
	private String headImgUrl;
	@JsonProperty(value = "RemarkName")
	private String remarkName;
	/**
	 * 群用户有该项，其他用户没有，群成员列表
	 */
	@JsonProperty(value = "MemberList")
	private List<User> memberList;
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
	@JsonProperty(value = "Signature")
	private String signature;

	public Long getUin() {
		return uin;
	}

	public void setUin(Long uin) {
		this.uin = uin;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<User> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<User> memberList) {
		this.memberList = memberList;
	}

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

}
