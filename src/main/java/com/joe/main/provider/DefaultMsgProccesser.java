package com.joe.main.provider;

import com.joe.main.entity.WxMsgBody;
import com.joe.main.service.LoggerService;
import com.joe.main.session.WechatSession;

public class DefaultMsgProccesser implements MsgProccesser{

	public boolean isReadable(WxMsgBody wxMsgBody) {
		return true;
	}

	public void proccess(WxMsgBody wxMsgBody, WechatSession wechatSession) {
		int msgType = wxMsgBody.getMsgType();
		if(msgType == 10002){
			LoggerService.info("撤销消息是：{}" , wxMsgBody);
		}
	}
}
