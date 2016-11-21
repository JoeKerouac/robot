package com.joe.main.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 获取QRID任务
 * 
 * @author joe
 *
 */
@Component
public class QRIdTask extends BaseTask {
	private static final Logger logger = LoggerFactory.getLogger(QRIdTask.class);
	private boolean complete = false;

	public boolean run(WechatSession wechatSession) {
		logger.info("开始构建获取二维码ID请求............");
		String url = URLService.getQRIdURL();
		String params;
		try {
			params = httpClientUtils.get(url);
			logger.info("获取二维码请求响应为：{}", params);
			Pattern pattern = Pattern.compile("window.QRLogin.code = [0-9]{3}; window.QRLogin.uuid = \"(.*)\";");
			Matcher matcher = pattern.matcher(params);
			if (matcher.find()) {
				wechatSession.setId(matcher.group(1));
				logger.info("获取到二维码的ID，ID为" + wechatSession.getId());
				complete = true;
			}
		} catch (Exception e) {
			logger.error("获取QRID失败" , e);
			complete = false;
		}
		return complete;
	}

	public String getTaskName() {
		return "获取QRId";
	}

	public boolean isComplete() {
		return complete;
	}
}
