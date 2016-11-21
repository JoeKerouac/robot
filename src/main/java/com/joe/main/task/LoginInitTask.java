package com.joe.main.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joe.main.entity.WxProperties;
import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 登陆成功后初始化任务（同时会初始化cookie）
 * @author joe
 *
 */
@Component
public class LoginInitTask extends BaseTask  {
	private static final Logger logger = LoggerFactory.getLogger(LoginInitTask.class);
	private boolean complete;
	


	public boolean run(WechatSession wechatSession) {
		logger.info("开始初始化登录信息............");
		try {
			String result = httpClientUtils.get(URLService.getLoginInitURL(wechatSession.getUrl()));
			wechatSession.setWxProperties(xmlParser.parse(result, WxProperties.class));
			complete = true;
		} catch (Exception e) {
			logger.info("登录信息初始化异常，异常原因：", e);
			complete = false;
		}
		return complete;
	}

	public String getTaskName() {
		return "登录成功后初始化任务";
	}

	public boolean isComplete() {
		return complete;
	}
}
