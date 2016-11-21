package com.joe.main.exception;

public class LoginTimeOutError extends Error{
	private static final long serialVersionUID = 3141184666674695062L;

	public LoginTimeOutError() {
		super("登录超时，请重新获取二维码");
	}
}
