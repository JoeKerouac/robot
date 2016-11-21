package com.joe.main.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.http.HttpClientUtils;
import com.joe.main.entity.Send;
import com.joe.main.entity.SendStatus;
import com.joe.main.entity.WxMsg;
import com.joe.main.entity.WxMsgBody;
import com.joe.main.provider.MsgProccesser;
import com.joe.main.session.WechatSession;
import com.joe.tools.JsonParser;

@Service
public class MsgService {
	private static final Logger logger = LoggerFactory.getLogger(MsgService.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private HttpClientUtils httpClientUtils;
	@Autowired
	private JsonParser jsonParser;
	private List<MsgProccesser> msgReaders = new ArrayList<MsgProccesser>();

	/**
	 * 开始处理消息
	 */
	public void proccess(WechatSession wechatSession) {
		WxMsg wxMsg = receive(wechatSession);
		if (wxMsg == null) {
			return;
		}
		wechatSession.getInit().setSyncKeyList(wxMsg.getSyncKeyList());
		List<WxMsgBody> msgs = wxMsg.getAddMsgList();
		if (msgs == null || msgs.isEmpty()) {
			return;
		}
		for (WxMsgBody msg : msgs) {
			read(msg, wechatSession);
		}
	}

	/**
	 * 回复消息
	 * 
	 * @param wxMsgBody
	 *            要回复的源消息
	 * @param msg
	 *            回复的消息
	 * @return
	 */
	public boolean reply(WxMsgBody wxMsgBody, String msg, WechatSession wechatSession) {
		String from = wechatSession.getInit().getUser().getUserName();
		String to = wxMsgBody.getFromUserName();
		to = to.equals(from) ? wxMsgBody.getToUserName() : to;
		return sendMsg(from, to, msg, wechatSession);
	}

	/**
	 * 发送消息
	 * 
	 * @param from
	 *            发送人（一般就是自己）
	 * @param to
	 *            接收人
	 * @param msg
	 *            要发送的消息
	 * @return
	 */
	private boolean sendMsg(String from, String to, String msg, WechatSession wechatSession) {
		logger.info("准备发送消息.........................................................");
		Send send = baseService.createSendTextMsgBody(from, to, msg, wechatSession);
		logger.info("要发送的消息是{}：", send);

		logger.info("开始发送消息........................");
		logger.info("构建请求............................");

		String url = URLService.getSendMsgURL(wechatSession);

		try {
			String result = httpClientUtils.post(url, jsonParser.toJson(send));
			logger.info("微信响应消息：{}", result);
			SendStatus status = jsonParser.readAsObject(result, SendStatus.class);
			if (status.getBaseResponse().getRet() != 0) {
				logger.info("消息发送失败");
				return false;
			}
			logger.info("消息发送完毕");
			return true;
		} catch (Exception e) {
			logger.error("网络连接错误，发送消息失败", e);
			return false;
		}
	}
	
	/**
	 * 注册消息处理器
	 * @param msgProccesser
	 */
	public void register(Class<? extends MsgProccesser> clazz){
		try {
			this.msgReaders.add(clazz.newInstance());
		} catch (Exception e) {
			logger.error("实例化{}失败" , clazz , e);
		} 
	}
	
	/**
	 * 注册消息处理器
	 * @param processer
	 */
	public void register(MsgProccesser processer){
		this.msgReaders.add(processer);
	}

	/**
	 * 读取消息并选择合适的处理器处理
	 * 
	 * @param selector
	 *            消息类型
	 */
	private void read(WxMsgBody wxMsgBody, WechatSession wechatSession) {
		if(wxMsgBody.getFromUserName().equals(wechatSession.getInit().getUser().getUserName())){
			//自己在其他客户端发送的消息，不进行处理
			return;
		}
		for (MsgProccesser msgReader : msgReaders) {
			if (msgReader.isReadable(wxMsgBody)) {
				msgReader.proccess(wxMsgBody, wechatSession);
				break;
			}
		}
	}

	/**
	 * 接收消息
	 * 
	 * @return
	 */
	private WxMsg receive(WechatSession wechatSession) {
		logger.info("开始接收消息........................");
		logger.info("构建请求............................");
		String url = URLService.getReceiveMsg(wechatSession);

		Map<String, Object> dataMap = new HashMap<String, Object>();

		dataMap.put("SyncKey", wechatSession.getInit().getSyncKeyList());
		dataMap.put("BaseRequest", baseService.createBaseRequestJson(wechatSession));
		dataMap.put("rr", ~(int) System.currentTimeMillis());

		// 生成请求必要数据
		try {
			String msg = httpClientUtils.post(url, jsonParser.toJson(dataMap));
			WxMsg wxMsg = jsonParser.readAsObject(msg, WxMsg.class);
			logger.info("微信响应数据为：{}", wxMsg);
			return wxMsg;
		} catch (Exception e) {
			logger.error("消息解析错误，错误信息为：" + e.toString());
			return null;
		}
	}
}
