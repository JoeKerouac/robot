package com.joe.main.exception;

/**
 * 微信初始化失败，需要重新启动
 * @author joe
 *
 */
public class InitError extends Error{
	private static final long serialVersionUID = -1661435051629397457L;
	public InitError(String message){
		super(String.format("微信  %s 失败", message));
	}
}
