package com.joe.http;

import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;

/**
 * post请求
 * 
 * @author joe
 *
 */
public class IHttpPost extends HttpRequest {
	private static HttpFactory httpFactory = null;
	private HttpPost post;
	private String content_type;
	private String url;
	private IHttpEntity entity;
	private Map<String , String> headers;

	public IHttpPost(String url) {
		if (httpFactory == null) {
			httpFactory = new HttpFactory();
		}
		this.url = url;
	}
	
	/**
	 * 设置请求体
	 * 
	 * @param entity
	 */
	public void setEntity(IHttpEntity entity) {
		this.entity = entity;
	}

	protected String getHost() {
		return url;
	}
	
	/**
	 * 获取请求对象
	 */
	protected HttpRequestBase getRequest() {
		post = httpFactory.createPost(url);
		post.setEntity(entity.getEntity());
		post.setHeader(HTTP.CONTENT_TYPE, getContent_type());
		if(headers != null && !headers.isEmpty()){
			for(Map.Entry<String, String> entity : this.headers.entrySet()){
				post.setHeader(entity.getKey() , entity.getValue());
			}
		}
		return post;
	}

	protected String getContent_type() {
		return content_type;
	}
	
	protected void setPost(HttpPost post) {
		this.post = post;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	
	public Map<String , String> getHeaders(){
		return this.headers;
	}
	
	public void setHeaders(Map<String , String> headers){
		this.headers = headers;
	}
	
}
