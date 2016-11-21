package com.joe.main.service;

import com.joe.main.entity.SyncKey;
import com.joe.main.entity.WxProperties;
import com.joe.main.session.WechatSession;

public class URLService {
	private static final String LOGIN_BASEURL = "https://login.weixin.qq.com/";
	private static final String BASEURL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/";

	/**
	 * 获取请求QRID的URL
	 * 
	 * @return
	 */
	public static String getQRIdURL() {
		return LOGIN_BASEURL
				+ "jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_="
				+ System.currentTimeMillis();
	}

	/**
	 * 获取登录URL
	 * 
	 * @param 二维码ID
	 * @return
	 */
	public static String getLoginURL(String id) {
		return LOGIN_BASEURL + "cgi-bin/mmwebwx-bin/login?loginicon=true&uuid=" + id + "&tip=1&r=-973800866&_="
				+ System.currentTimeMillis();
	}

	/**
	 * 获取登录初始化URL
	 * 
	 * @param url
	 *            登录接口返回的URL
	 * @return
	 */
	public static String getLoginInitURL(String url) {
		return url += "&fun=new&version=v2&lang=zh_CN";
	}

	/**
	 * 获取初始化URL
	 * 
	 * @param wxPropties
	 *            登录初始化中获取的配置
	 * @return
	 */
	public static String getInitURL(final WxProperties wxPropties) {
		long time = System.currentTimeMillis();
		String url = BASEURL + "webwxinit?r=" + ~(int) time + "&lang=zh_CN&pass_ticket=" + wxPropties.getPass_ticket();
		return url;
	}

	/**
	 * 获取状态通知URL
	 * 
	 * @param wxPropties
	 *            登录初始化中获取的配置
	 * @return
	 */
	public static String getStatusNotifyURL(final WxProperties wxPropties) {
		return BASEURL + "webwxstatusnotify?lang=zh_CN&pass_ticket=" + wxPropties.getPass_ticket();
	}

	/**
	 * 获取消息监听URL
	 * 
	 * @param wechatSession
	 *            微信回话session
	 * 
	 * @return
	 */
	public static String getListenURL(WechatSession wechatSession) {
		String r = System.currentTimeMillis() + "";
		r = r.substring(4) + (int) (Math.random() * 10000);
		String r2 = System.currentTimeMillis() + "";
		r2 = r2.substring(4) + (int) (Math.random() * 10000);
		String url = BASEURL + "synccheck?r=" + r2 + "&skey=" + wechatSession.getWxProperties().getSkey() + "&sid=" + wechatSession.getWxProperties().getWxsid()
				+ "&uin=" + wechatSession.getWxProperties().getWxuin() + "&deviceid=" + "e" + (long) (Math.random() * 1000000000000000L)
				+ "&synckey=";
		if (wechatSession.getInit().getSyncKeyList() != null) {
			for (SyncKey key : wechatSession.getInit().getSyncKeyList().getList()) {
				url += key.getKey() + "_" + key.getVal() + "%7C";
			}
		}
		url = url.substring(0, url.length() - 4);
		url += "&_=" + r;
		return url;
	}
	
	/**
	 * 获取接受消息的URL
	 * @param wechatSession
	 *            微信回话session
	 * @return
	 */
	public static String getReceiveMsg(WechatSession wechatSession){
		String url = BASEURL + "webwxsync?sid=" + wechatSession.getWxProperties().getWxsid() + "&skey="
				+ wechatSession.getWxProperties().getSkey() + "&lang=zh_CN&pass_ticket=" + wechatSession.getWxProperties().getPass_ticket();
		return url;
	}
	
	/**
	 * 获取发送消息URL
	 * @param wechatSession
	 *            微信回话session
	 * @return
	 */
	public static String getSendMsgURL(WechatSession wechatSession){
		return BASEURL + "webwxsendmsg?&lang=zh_CN&pass_ticket="
				+ wechatSession.getWxProperties().getPass_ticket();
	}
}
