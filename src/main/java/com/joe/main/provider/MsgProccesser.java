package com.joe.main.provider;

import com.joe.main.entity.WxMsgBody;
import com.joe.main.session.WechatSession;

/**
 * 消息读取
 * 
 * @author joe
 *
 */
public interface MsgProccesser {
	/**
	 * 消息是否能够处理
	 * @param wxMsgBody
	 * 	消息体
	 * @return
	 * <li>true：能够处理</li>
	 * <li>false：不能能够处理</li>
	 */
	public boolean isReadable(WxMsgBody wxMsgBody);

	/**
	 * 处理消息
	 * @param wxMsgBody
	 * 	消息体
	 * @return
	 * 要响应的消息
	 */
	public void proccess(WxMsgBody wxMsgBody,WechatSession wechatSession);
}
