package com.joe.main.task;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joe.main.session.WechatSession;
import com.joe.tools.IOUtils;

/**
 * 获取二维码任务
 * @author joe
 *
 */
@Component
public class QRTask extends BaseTask {
	private static final Logger logger = LoggerFactory.getLogger(QRTask.class);
	/*
	 * 二维码是否获取成功
	 */
	private boolean complete = false;


	/**
	 * 第二步： 根据第一步获取的UUID获取一个二维码
	 */
	public boolean run(WechatSession wechatSession) {
		logger.info("开始构建请求............");
		String url = "https://login.weixin.qq.com/qrcode/" + wechatSession.getId();
		InputStream input = null;
		try {
			input = httpClientUtils.getAsInputStream(url);
			IOUtils.saveAsFile(wechatSession.getPath(), wechatSession.getName(), input);
			this.complete = true;
			return true;
		} catch (Exception e) {
			logger.error("获取二维码失败，失败原因：", e);
			throw new RuntimeException("获取二维码失败", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("输入流关闭失败", e);
				}
			}
		}
	}

	public String getTaskName() {
		return "获取二维码任务";
	}

	public boolean isComplete() {
		return this.complete;
	}
}
