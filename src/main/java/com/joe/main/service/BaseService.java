package com.joe.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.main.entity.BaseRequest;
import com.joe.main.entity.Send;
import com.joe.main.entity.SendMsg;
import com.joe.main.session.WechatSession;
import com.joe.tools.JsonParser;

/**
 * 基础service
 * 
 * @author joe
 *
 */
@Service
public class BaseService {
	@Autowired
	private JsonParser jsonParser;

	/**
	 * 获取baserequest请求体的json数据
	 * 
	 * @param wxPropties
	 * @return
	 */
	public String createBaseRequestJson(WechatSession wechatSession) {
		BaseRequest request = createBaseRequest(wechatSession);
		return jsonParser.toJson(request);
	}
	
	/**
	 * 获取baserequest请求体
	 * 
	 * @param wxPropties
	 * @return
	 */
	public BaseRequest createBaseRequest(WechatSession wechatSession) {
		BaseRequest request = new BaseRequest();
		request.setDeviceId("e" + (long) (Math.random() * 10000000000000000L));
		request.setSid(wechatSession.getWxProperties().getWxsid());
		request.setSkey(wechatSession.getWxProperties().getSkey());
		request.setUin(Long.parseLong(wechatSession.getWxProperties().getWxuin()));
		return request;
	}
	
	/**
	 * 生成要发送的文本消息
	 * 
	 * @param from
	 * @param to
	 * @param content
	 * @param type
	 * @param wxPropties
	 * @return
	 */
	public Send createSendTextMsgBody(String from, String to, String content ,WechatSession wechatSession) {
		return createSendMsgBody(from, to, content, 1,wechatSession);
	}

	/**
	 * 生成要发送的消息
	 * 
	 * @param from
	 * @param to
	 * @param content
	 * @param type
	 * @param wxPropties
	 * @return
	 */
	private Send createSendMsgBody(String from, String to, String content, int type , WechatSession wechatSession) {
		Send send = new Send();

		BaseRequest request = createBaseRequest(wechatSession);

		SendMsg msg = new SendMsg();
		msg.setFrom(from);
		msg.setContent(content);
		msg.setTo(to);
		msg.setType(type);
		String id = System.currentTimeMillis() + "" + (int) (Math.random() * 9000 + 1000);
		msg.setClientMsgId(id);
		msg.setLocalId(id);

		send.setRequest(request);
		send.setMsg(msg);
		send.setScene(0);

		return send;
	}
}
