package com.joe.main.provider;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joe.ext.TlMsgService;
import com.joe.main.entity.WxMsgBody;
import com.joe.main.service.LoggerService;
import com.joe.main.service.MsgService;
import com.joe.main.service.TalkService;
import com.joe.main.session.WechatSession;


/**
 * 文本消息读取，只读取文本消息
 * @author joe
 *
 */
@Component
public class TextMsgProccesser implements MsgProccesser {
	@Resource(type=TlMsgService.class)
	private TalkService tlMsgService;
	@Autowired
	private MsgService msgService;
	public boolean isReadable(WxMsgBody wxMsgBody) {
		if (wxMsgBody == null || wxMsgBody.getMsgType() == null) {
			return false;
		}
		return wxMsgBody.getMsgType() == 1;
	}

	public void proccess(WxMsgBody wxMsgBody,WechatSession wechatSession) {
		LoggerService.info("消息是：{}" , wxMsgBody.getContent());
		String response = tlMsgService.talk(wxMsgBody.getContent(), wxMsgBody.getFromUserName());
		LoggerService.info("响应消息是：{}" , response);
		if(response == null || response.isEmpty()){
			response = "测试";
		}
		msgService.reply(wxMsgBody, response, wechatSession);
	}
}
