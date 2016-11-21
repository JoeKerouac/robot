package com.joe.main.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.http.HttpClientUtils;
import com.joe.main.service.BaseService;
import com.joe.main.service.MsgService;
import com.joe.tools.JsonParser;
import com.joe.tools.XmlParser;

/**
 * 基础重试任务
 * 
 * @author joe
 *
 */
public abstract class BaseTask implements RetryTask {
	protected Logger logger;
	protected HttpClientUtils httpClientUtils;
	protected BaseService baseService;
	protected JsonParser jsonParser;
	protected XmlParser xmlParser;
	protected MsgService msgService;

	public void init(HttpClientUtils httpClientUtils, BaseService baseService, JsonParser jsonParser,
			XmlParser xmlParser, MsgService msgService) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.httpClientUtils = httpClientUtils;
		this.baseService = baseService;
		this.jsonParser = jsonParser;
		this.xmlParser = xmlParser;
		this.msgService = msgService;
	}

}
