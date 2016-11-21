package com.joe.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * get请求
 * 
 * @author joe
 *
 */
public class IHttpGet extends HttpRequest{
	private HttpGet get;
	private static HttpFactory httpFactory = null;
	private String content_type;
	private String url;

	/**
	 * 主机URL
	 * 
	 * @param url
	 */
	public IHttpGet(String url) {
		if(httpFactory == null){
			httpFactory = new HttpFactory();
		}
		this.url = url;
	}

	protected HttpRequestBase getRequest() {
		get = httpFactory.createGet(url);
		return get;
	}

	protected void setGet(HttpGet get) {
		this.get = get;
	}
	protected void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	
	protected String getContent_type() {
		return content_type;
	}
	
	protected String getHost() {
		return url;
	}
}
