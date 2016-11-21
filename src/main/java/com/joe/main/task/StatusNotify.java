package com.joe.main.task;


import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 开启微信状态通知
 * 
 * @author joe
 *
 */
public class StatusNotify extends BaseTask {
	private boolean complete;

	public boolean run(WechatSession wechatSession) {
		logger.info("开启状态通知");

		String id = wechatSession.getInit().getUser().getUserName();
		String url = URLService.getStatusNotifyURL(wechatSession.getWxProperties());

		StringBuilder sb = new StringBuilder();
		String dataStr = baseService.createBaseRequestJson(wechatSession);

		sb.append(",\"Code\":3").append(",\"FromUserName\":\"").append(id).append("\",\"ToUserName\":\"").append(id)
				.append("\",\"ClientMsgId\":").append(System.currentTimeMillis()).append("}");
		dataStr = "{\"BaseRequest\":" + dataStr + sb.toString();

		try {
			httpClientUtils.post(url, dataStr);
			logger.info("开启状态通知成功");
			this.complete = true;
		} catch (Exception e) {
			logger.error("开启状态通知失败", e);
			this.complete = false;
		}
		return complete;

	}

	public String getTaskName() {
		return "开启微信状态通知";
	}

	public boolean isComplete() {
		return complete;
	}

}
