package com.joe.main.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joe.main.entity.Init;
import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 初始化信息任务
 * 
 * @author joe
 *
 */
@Component
public class InitTask extends BaseTask {
	private static final Logger logger = LoggerFactory.getLogger(InitTask.class);
	private boolean complete;

	public boolean run(WechatSession wechatSession) {
		logger.info("开始初始化信息");
		String url = URLService.getInitURL(wechatSession.getWxProperties());
		String request = baseService.createBaseRequestJson(wechatSession);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"BaseRequest\":").append(request).append("}");
		String dataStr = sb.toString();
		try {
			String result = httpClientUtils.post(url, dataStr);
			logger.info("微信服务器响应结果为：{}", result);
			if (result.trim().equals("")) {
				// 响应有可能为空，第一次一般都为空
				complete = false;
				return complete;
			} else {
				wechatSession.setInit(jsonParser.readAsObject(result, Init.class));
				complete = true;
				return complete;
			}
		} catch (Exception e) {
			logger.error("初始化信息出错", e);
			complete = false;
			return complete;
		}
	}

	public String getTaskName() {
		return "初始化信息";
	}

	public boolean isComplete() {
		return complete;
	}
}
