package com.joe.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

public class IHttpEntity {
	private HttpEntity entity;
	/**
	 * 要发送的数据
	 */
	private String data;
	/**
	 * 数据编码
	 */
	private String charset;
	
	public IHttpEntity(){
	}
	
	public void setData(String data , String charset) {
		this.charset = charset;
		this.data = data;
	}

	protected HttpEntity getEntity(){
		if(entity == null){
			entity = new StringEntity(data, charset);
		}
		return entity;
	}
}
