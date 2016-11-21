package com.joe.main.session;


import com.joe.main.entity.Init;
import com.joe.main.entity.SyncKey;
import com.joe.main.entity.WxProperties;

import lombok.Data;

@Data
public class WechatSession {
	/*
	 * 初始化信息
	 */
	private Init init;
	/*
	 * 微信请求配置信息
	 */
	private WxProperties wxProperties;
	/*
	 * 二维码ID
	 */
	private String id = "";
	/*
	 * 登陆成功后的跳转URL
	 */
	private String url = "";
	/*
	 * session的ID（唯一标示）
	 */
	private String sessionId;
	/*
	 * 二维码保存路径
	 */
	private String path;
	/*
	 * 二维码文件信息
	 */
	private String name;

	public WechatSession(String path, String name, String sessionId) {
		this.path = path;
		this.name = name;
		this.sessionId = sessionId;
	}

	/**
	 * 将指定消息（index位置的）的数目加上指定的value值
	 * 
	 * @param index
	 *            消息数目存放位置
	 * @param value
	 *            指定的value值
	 */
	public void addSyncKeyList(int index, int value) {
		if (this.init == null || this.init.getSyncKeyList() == null || this.init.getSyncKeyList().getList() == null
				|| this.init.getSyncKeyList().getList().get(index) == null) {
			return;
		}
		SyncKey syncKey = this.init.getSyncKeyList().getList().get(index);
		syncKey.add(value);
	}
}
