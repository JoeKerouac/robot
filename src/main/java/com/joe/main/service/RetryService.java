package com.joe.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.main.exception.ResetException;
import com.joe.main.session.WechatSession;
import com.joe.main.task.RetryTask;

/**
 * 重试任务服务
 * 
 * @author joe
 *
 */
public class RetryService {
	private static final Logger logger = LoggerFactory.getLogger(RetryService.class);

	/**
	 * 重试任务执行（有异常会抛出）
	 * 
	 * @param reset
	 *            重试次数
	 * @param task
	 *            重试任务
	 * @param wechatSession
	 *            微信session
	 * 
	 */
	public static void retry(int reset, RetryTask task, WechatSession wechatSession) {
		retryService(false, reset, task, wechatSession);
	}

	/**
	 * 重试任务执行（忽略异常）
	 * 
	 * @param reset
	 *            重试次数
	 * @param task
	 *            需要执行的任务
	 * @param wechatSession
	 *            微信session
	 */
	public static void retryIgnoreException(int reset, RetryTask task, WechatSession wechatSession) {
		retryService(true, reset, task, wechatSession);
	}

	/**
	 * 最多进行resetCount次重试
	 * 
	 * @param ignoreException
	 *            重试时是否忽略异常，true为忽略
	 * @param resetCount
	 *            重试次数
	 * @param task
	 *            需要重试的任务
	 * @param wechatSession
	 *            微信session
	 */
	private static void retryService(boolean ignoreException, int resetCount, RetryTask task,
			WechatSession wechatSession) {
		int i = 0;
		while (resetCount > 0) {
			try {
				i++;
				logger.info("第{}次重试任务{}", i, task.getTaskName());
				resetCount--;
				if (task.run(wechatSession)) {
					return;
				}
			} catch (Exception e) {
				if (!ignoreException) {
					logger.error("重试任务异常，异常原因：", e);
					throw new ResetException(e);
				} else {
					logger.warn("重试任务异常，异常原因：", e);
				}
			}
		}
	}
}
