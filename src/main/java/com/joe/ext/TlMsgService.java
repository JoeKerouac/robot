package com.joe.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.http.HttpClientUtils;
import com.joe.tools.JsonParser;

/**
 * 图灵机器人
 * @author joe
 *
 */
@Service
public class TlMsgService {
	private static final Logger logger = LoggerFactory.getLogger(TlMsgService.class);
	@Autowired
	private HttpClientUtils httpClientUtils;
	@Autowired
	private JsonParser jsonParser;
	private static final String apiUrl = "http://www.tuling123.com/openapi/api";

	/**
	 * 聊天
	 * 
	 * @param content
	 *            对话信息
	 * @param userid
	 *            用户id
	 * @return 回应信息
	 */
	public String talk(String content, String userid) {
		TuLingMsg msg = new TuLingMsg();
		msg.setInfo(content);
		msg.setKey("424fbda2a6ec4267adb0908a0a1c2852");
		msg.setUserid(userid);
		try {
			String result = httpClientUtils.post(apiUrl, jsonParser.toJson(msg));
			TuLingResponseMsg response = jsonParser.readAsObject(result, TuLingResponseMsg.class);
			logger.info("图灵机器人响应信息为：{}" , result);
			if (response.getCode() == 100000) {
				return response.getText();
			} else {
				return "";
			}
		} catch (Exception e) {
			logger.error("图灵机器人出错了", e);
			return "";
		}
	}
}
