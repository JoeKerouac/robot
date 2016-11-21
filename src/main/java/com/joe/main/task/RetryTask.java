package com.joe.main.task;

import com.joe.http.HttpClientUtils;
import com.joe.main.service.BaseService;
import com.joe.main.service.MsgService;
import com.joe.main.session.WechatSession;
import com.joe.tools.JsonParser;
import com.joe.tools.XmlParser;

/**
 * 可以重试的任务
 * 
 * @author joe
 *
 */
public interface RetryTask {
	/**
	 * 
	 * 任务
	 * 
	 * @param wechatSession
	 *            微信session
	 * @return
	 *         <li>true：任务执行成功，直接退出</li>
	 *         <li>false：任务执行失败，重试</li>
	 * @throws Error
	 *             任务有可能抛出Error
	 */
	public boolean run(WechatSession wechatSession) throws Error;

	/**
	 * 获取任务名字
	 * 
	 * @return
	 */
	public String getTaskName();

	/**
	 * 任务是否完成
	 * 
	 * @return
	 *         <li>true：完成</li>
	 *         <li>false：未完成</li>
	 */
	public boolean isComplete();

	/**
	 * 
	 * @param httpClientUtils
	 * @param baseService
	 * @param jsonParser
	 * @param xmlParser
	 */
	public void init(HttpClientUtils httpClientUtils, BaseService baseService, JsonParser jsonParser,
			XmlParser xmlParser, MsgService msgService);
}
