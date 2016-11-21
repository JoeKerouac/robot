package com.joe.http;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * HttpClient
 * 
 * @author joe
 *
 */
@Component
public class HttpClient {
	private static CloseableHttpClient httpClient;
	private static final Logger logger = LoggerFactory.getLogger(IHttpResponse.class);

	public HttpClient(@Autowired HttpFactory httpFactory) {
		httpClient = httpFactory.getHttpClient();
	}

	/**
	 * 执行请求
	 * 
	 * @param request
	 *            请求
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public IHttpResponse excute(HttpRequest request) throws ClientProtocolException, IOException {
		logger.debug("开始往主机：" + request.getHost() + "发送请求");
		IHttpResponse response = new IHttpResponse(httpClient.execute(request.getRequest()));
		logger.debug("请求发送完毕");
		return response;
	}

	/**
	 * 关闭HttpClient，请在关闭服务器时调用
	 */
	public static void close() {
		if (httpClient != null) {
			try {
				httpClient.close();
				httpClient = null;
				logger.debug("HttpClient已经关闭");
			} catch (IOException e) {
				logger.error("HttpClient关闭失败", e);
			}
		}
	}

	protected CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	protected void setHttpClient(CloseableHttpClient httpClient) {
		HttpClient.httpClient = httpClient;
	}

}
