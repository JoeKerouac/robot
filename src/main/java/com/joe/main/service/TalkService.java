package com.joe.main.service;

/**
 * 聊天服务
 * 
 * @author joe
 *
 */
public interface TalkService {
	/**
	 * 聊天
	 * 
	 * @param content
	 *            对话信息
	 * @param userid
	 *            用户id
	 * @return 回应信息
	 */
	public String talk(String content, String userid);
}
