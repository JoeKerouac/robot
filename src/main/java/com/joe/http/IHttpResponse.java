package com.joe.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpResponse
 * 
 * @author joe
 *
 */
public class IHttpResponse {
	private CloseableHttpResponse closeableHttpResponse;
	private static final Logger log = LoggerFactory.getLogger(IHttpResponse.class);
	private int code;

	public IHttpResponse(CloseableHttpResponse closeableHttpResponse) {
		this.closeableHttpResponse = closeableHttpResponse;
		this.code = closeableHttpResponse.getStatusLine().getStatusCode();
	}

	/**
	 * 获取请求状态
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 以字符串的形式获取响应数据
	 * 
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getResultAsString() throws ParseException, IOException {
		return getResultAsString(Charset.defaultCharset());
	}

	/**
	 * 以字符串的形式获取响应数据
	 * 
	 * @param charsetName
	 *            编码名称
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getResultAsString(String charsetName) throws ParseException, IOException {
		return getResultAsString(charsetName == null ? null : Charset.forName(charsetName));
	}

	/**
	 * 以字符串的形式获取响应数据
	 * 
	 * @param charset
	 *            编码
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getResultAsString(Charset charset) throws ParseException, IOException {
		HttpEntity entity = closeableHttpResponse.getEntity();
		String result = EntityUtils.toString(entity, charset);
		return result;
	}

	/**
	 * 以流的方式获取响应数据
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public InputStream getResultAsInputStream() throws UnsupportedOperationException, IOException {
		HttpEntity entity = closeableHttpResponse.getEntity();
		InputStream in = entity.getContent();
		return in;
	}

	public void close() {
		if (closeableHttpResponse != null) {
			try {
				closeableHttpResponse.close();
			} catch (Exception e) {
				log.error("Response关闭失败", e);
			}
		}
	}
}
