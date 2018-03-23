package com.joe.ext;

import com.joe.http.IHttpClientUtil;
import com.joe.utils.parse.json.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图灵机器人
 *
 * @author joe
 */
public class TlMsgService {
    private static final Logger logger = LoggerFactory.getLogger(TlMsgService.class);
    private IHttpClientUtil clientUtil = new IHttpClientUtil();
    private static final JsonParser parser = JsonParser.getInstance();
    private static final String apiUrl = "http://www.tuling123.com/openapi/api";

    /**
     * 聊天
     *
     * @param content 对话信息
     * @param userid  用户id
     * @return 回应信息
     */
    public String talk(String content, String userid) {
        TuLingMsg msg = new TuLingMsg();
        msg.setInfo(content);
        msg.setKey("424fbda2a6ec4267adb0908a0a1c2852");
        msg.setUserid(userid);
        try {
            String result = clientUtil.executePost(apiUrl, parser.toJson(msg));
            TuLingResponseMsg response = parser.readAsObject(result, TuLingResponseMsg.class);
            logger.info("图灵机器人响应信息为：{}", result);
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
