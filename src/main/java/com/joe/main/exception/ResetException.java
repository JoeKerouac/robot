package com.joe.main.exception;

/**
 * 重试异常
 * @author joe
 *
 */
public class ResetException extends RuntimeException{
	private static final long serialVersionUID = -8540598887062819556L;

	public ResetException(Throwable cause){
		super("重试异常，异常原因：", cause);
	}
}
