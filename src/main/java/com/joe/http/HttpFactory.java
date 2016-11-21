package com.joe.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * http工厂
 * 
 * @author joe
 *
 */
@Component
public class HttpFactory {
	private static final Logger logger = LoggerFactory.getLogger(HttpFactory.class);
	private static CloseableHttpClient httpclient = null;
	private static CookieStore cookieStore = null;
	private Object lock = new Object();

	/**
	 * 获取httpClient
	 * 
	 * @return
	 */
	protected final CloseableHttpClient getHttpClient() {
		synchronized (lock) {
			if (httpclient == null) {
				init();
			}
		}
		return httpclient;
	}

	/**
	 * 获取所有cookie
	 * 
	 * @return 当cookie不存在时返回空的列表
	 */
	protected final List<Cookie> getCookie() {
		if (cookieStore == null) {
			init();
		}
		return cookieStore.getCookies();
	}

	/**
	 * 获取指定cookie的值
	 * 
	 * @param key
	 * @return 当指定cookie不存在时返回null
	 */
	protected final String getCookie(String key) {
		if (cookieStore == null) {
			init();
		}
		List<Cookie> cookies = cookieStore.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			Cookie cookie = cookies.get(i);
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * 构建默认的get请求
	 * 
	 * @param url
	 *            url
	 * @return
	 */
	protected final HttpGet createGet(String url) {
		HttpGet get = createGet(url, 100000, 10000, 10000);
		return get;
	}

	/**
	 * 构建自定义get请求
	 * 
	 * @param url
	 *            url
	 * @param socketTimeout
	 *            数据传输超时时间，单位：毫秒
	 * @param connectTimeout
	 *            连接超时时间，单位：毫秒
	 * @param connectionRequestTimeout
	 *            请求超时时间，单位：毫秒
	 * @return
	 */
	protected final HttpGet createGet(String url, int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
		HttpGet get = new HttpGet(url);
		get.setConfig(buildRequestConfig(socketTimeout, connectTimeout, connectionRequestTimeout));
		return get;
	}

	/**
	 * 构建默认的post请求
	 * 
	 * @param url
	 *            url
	 * @return
	 */
	protected final HttpPost createPost(String url) {
		HttpPost post = createPost(url, 100000, 10000, 10000);
		return post;
	}

	/**
	 * 构建自定义post请求
	 * 
	 * @param url
	 *            url
	 * @param socketTimeout
	 *            数据传输超时时间
	 * @param connectTimeout
	 *            连接超时时间
	 * @param connectionRequestTimeout
	 *            请求超时时间
	 * @return
	 */
	protected final HttpPost createPost(String url, int socketTimeout, int connectTimeout,
			int connectionRequestTimeout) {
		HttpPost post = new HttpPost(url);
		post.setConfig(buildRequestConfig(socketTimeout, connectTimeout, connectionRequestTimeout));
		return post;
	}

	/**
	 * 构建请求配置
	 * 
	 * @param socketTimeout
	 *            传输超时（单位：毫秒）
	 * @param connectTimeout
	 *            连接超时（单位：毫秒）
	 * @param connectionRequestTimeout
	 *            请求超时（单位：毫秒）
	 * @return
	 */
	protected RequestConfig buildRequestConfig(int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
		logger.info("构建请求配置");
		logger.info("请求socket超时时间为：" + socketTimeout + "ms；connect超时时间为：" + connectTimeout
				+ "ms；connectionRequest超时时间为：" + connectionRequestTimeout + "ms");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build();
		logger.info("请求配置构建成功");
		return requestConfig;
	}

	/**
	 * 初始化httpClient和CookieStore
	 */
	private void init() {
		logger.info("正在初始化HttpClient");
		// 自定义解析，选择默认解析
		HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory();
		HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

		// 利用ParserFactory创建连接工厂
		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
				requestWriterFactory, responseParserFactory);

		// SSL配置，获取默认SSL
		SSLContext sslcontext = SSLContexts.createSystemDefault();

		// 注册协议
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext)).build();

		// 自定义DNS
		DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

			@Override
			public InetAddress[] resolve(final String host) throws UnknownHostException {
				if (host.equalsIgnoreCase("myhost") || host.equalsIgnoreCase("localhost")) {
					return new InetAddress[] { InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }) };
				} else {
					return super.resolve(host);
				}
			}

		};

		// 连接池管理
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
				connFactory, dnsResolver);

		// socket配置，不延迟发送
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		// 将socket配置设置为连接池默认配置
		connManager.setDefaultSocketConfig(socketConfig);
		// 暂停活动1S后验证连接
		connManager.setValidateAfterInactivity(1000);

		// 消息容器，初始化消息容器以及消息容器的配置
		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
				.setMaxLineLength(2000).build();
		Charset charset = null;
		charset = Charset.forName("UTF8");

		logger.info("ConnectionConfig-charset为：" + charset);

		// Create connection configuration
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(charset)
				.setMessageConstraints(messageConstraints).build();
		// Configure the connection manager to use connection configuration
		// either
		// by default or for a specific host.
		connManager.setDefaultConnectionConfig(connectionConfig);

		// Configure total max or per route limits for persistent connections
		// that can be kept in the pool or leased by the connection manager.
		connManager.setMaxTotal(100);
		connManager.setDefaultMaxPerRoute(10);

		// Use custom cookie store if necessary.
		cookieStore = new BasicCookieStore();
		// Create global request configuration
		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
				.setExpectContinueEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		httpclient = HttpClients.custom().setConnectionManager(connManager).setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(defaultRequestConfig).build();
		logger.info("HttpClient初始化完毕");
	}
}
