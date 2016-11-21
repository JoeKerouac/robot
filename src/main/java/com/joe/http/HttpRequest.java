package com.joe.http;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class HttpRequest {
	protected abstract HttpRequestBase getRequest();
	protected abstract String getHost();
	protected abstract void setContent_type(String content_type);
}
