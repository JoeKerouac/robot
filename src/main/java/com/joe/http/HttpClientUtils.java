package com.joe.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HttpClientUtils {
	@Autowired
	private HttpClient client;

	/**
	 * 发起get请求
	 * 
	 * @param url
	 *            请求的URL
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String get(String url) throws ClientProtocolException, IOException {
		return get(url, "UTF8");
	}

	/**
	 * 发起get请求
	 * 
	 * @param url
	 *            请求的URL
	 * @param charset
	 *            请求结果的编码
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String get(String url, String charset) throws ClientProtocolException, IOException {
		IHttpGet get = new IHttpGet(url);
		IHttpResponse response = client.excute(get);
		return response.getResultAsString(charset);
	}
	
	/**
	 * 发起get请求（获取结果的输入流）
	 * 
	 * @param url
	 *            请求的URL
	 * @param charset
	 *            请求结果的编码
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream getAsInputStream(String url) throws ClientProtocolException, IOException {
		IHttpGet get = new IHttpGet(url);
		IHttpResponse response = client.excute(get);
		return response.getResultAsInputStream();
	}

	/**
	 * 发起post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param resultCharset
	 *            请求结果编码
	 * @param data
	 *            数据
	 * @param charset
	 *            数据编码
	 * @param headers
	 *            header集合
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String post(String url, String resultCharset, String data, String charset, String content_type,
			Map<String, String> headers) throws ClientProtocolException, IOException {
		IHttpPost post = new IHttpPost(url);
		IHttpEntity entity = new IHttpEntity();
		entity.setData(data, charset);
		post.setEntity(entity);
		post.setContent_type(content_type);
		post.setHeaders(headers);
		IHttpResponse response = client.excute(post);
		return response.getResultAsString(resultCharset);
	}

	/**
	 * 发起post请求（采用系统默认配置编码）
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String post(String url, String data) throws ClientProtocolException, IOException {
		return post(url, data, null);
	}

	/**
	 * 发起post请求（采用系统默认配置编码）
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @param headers
	 *            header集合
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String post(String url, String data, Map<String, String> headers)
			throws ClientProtocolException, IOException {
		return post(url, "UTF8", data, "UTF8",
				"application/json", headers);
	}

	/**
	 * 发起post请求（获取结果的输入流）
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream postAsInputStream(String url, String data) throws ClientProtocolException, IOException {
		return postAsInputStream(url, data, "UTF8");
	}

	/**
	 * 发起post请求（获取结果的输入流）
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @param charset
	 *            请求数据编码
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream postAsInputStream(String url, String data, String charset)
			throws ClientProtocolException, IOException {
		IHttpPost post = new IHttpPost(url);
		IHttpEntity entity = new IHttpEntity();
		entity.setData(data, charset);
		post.setEntity(entity);
		IHttpResponse response = client.excute(post);
		return response.getResultAsInputStream();
	}
}
