package com.joe.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重要的logger
 * @author joe
 *
 */
public class LoggerService {
	private static final Logger logger = LoggerFactory.getLogger("Message");
	public static void info(String msg , Object... objs){
		logger.info(msg , objs);
	}
	
	public static void info(String msg){
		logger.info(msg);
	}
}
