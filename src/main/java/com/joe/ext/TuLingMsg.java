package com.joe.ext;

import lombok.Data;
/**
 * 图灵机器人消息
 * @author joe
 *
 */
@Data
public class TuLingMsg {
	/**
	 * 内容
	 */
	private String info;
	/**
	 * 图灵机器人APIkey
	 */
	private String key;
	/**
	 * 聊天用户的唯一标示，方便上下文联想
	 */
	private String userid;
}
