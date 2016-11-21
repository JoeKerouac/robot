package com.joe.main.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joe.main.exception.InitError;
import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 监听任务
 * 
 * @author joe
 *
 */
@Component
public class ListenerTask extends BaseTask {
	private static final Logger logger = LoggerFactory.getLogger(ListenerTask.class);
	/**
	 * 标志是否启动，如果为false则为关闭
	 */
	private volatile boolean start = true;

	/**
	 * 监听是否有消息
	 * @throws InitError
	 * 
	 */
	public boolean run(WechatSession wechatSession) throws InitError{
		logger.info("正在检查是否有新消息.....................");
		try {
			String url = URLService.getListenURL(wechatSession);
			logger.info("发送同步消息请求....................");
			String result = httpClientUtils.get(url);
			
			Pattern pattern = Pattern.compile("window\\.synccheck\\=\\{retcode:\"([0-9]+)\",selector:\"([0-9])\"\\}");
			Matcher matcher = pattern.matcher(result);
			logger.info("微信响应消息为：{}", result);
			if (matcher.find()) {
				String retcode = matcher.group(1);
				if(!retcode.equals("0")){
					return false;
				}
				String selector = matcher.group(2);
				// msgService.read(selector);
				// 无论是什么图片消息还是文字还是其他，selector都应该是2
				if (selector.equals("2")) {
					logger.info("有新消息了，开始处理新消息..................");
					msgService.proccess(wechatSession);
					// 表明有新消息
				} else if (selector.equals("7")) {
					logger.info("notify消息................");
					// 继续监听
				} else {
					logger.info("selector is " + selector);
				}
			}
			// statusNotify.run();
			logger.info("消息同步完成.........................");
		} catch (Exception e) {
			logger.error("消息同步失败，原因：" + e.toString());
		}
		if (this.start) {
			// 继续监听
			return run(wechatSession);
		} else {
			return false;
		}
	}

	public void shutdown() {
		this.start = false;
	}

	public String getTaskName() {
		return "监听消息";
	}

	public boolean isComplete() {
		return !start;
	}
}
