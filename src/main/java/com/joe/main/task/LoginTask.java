package com.joe.main.task;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.main.exception.LoginTimeOutError;
import com.joe.main.service.URLService;
import com.joe.main.session.WechatSession;

/**
 * 登录任务，登录任务不会失败，只会超时，如果超时将会抛出LoginTimeOutException异常
 * @author joe
 *
 */
public class LoginTask extends BaseTask  {
	private static final Logger logger = LoggerFactory.getLogger(LoginTask.class);
	private boolean complete = false;
	
	
	public boolean run(WechatSession wechatSession) {
		logger.info("开始构建请求............");
		boolean flag = true;
		logger.info("请求构建完毕，开始发送请求..............");
		String url = URLService.getLoginURL(wechatSession.getId());
		Pattern patternLogin = Pattern.compile("window\\.code\\=200;\nwindow.redirect_uri=\"(.*)\";");
		Pattern patternWaitLogin = Pattern.compile("window\\.code\\=201.*");
		while (flag) {
			try {
				String params = httpClientUtils.get(url);
				Matcher matcher1 = patternLogin.matcher(params);
				Matcher matcher2 = patternWaitLogin.matcher(params);
				if (matcher1.find()) {
					flag = false;
					logger.info("登陆成功....................");
					wechatSession.setUrl(matcher1.group(1));
				} else if (matcher2.find()) {
					logger.info("请在手机微信端点击登录....................");
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				} else {
					//登录超时，请重新获取二维码
					logger.error("登录超时，返回结果：{}" , params);
					throw new LoginTimeOutError();
				}
			} catch (IOException e) {
				logger.info("网络请求，异常原因：" , e);
			}
		}
		complete = true;
		return complete;
	}

	public String getTaskName() {
		return "登录任务";
	}

	public boolean isComplete() {
		return complete;
	}
}
