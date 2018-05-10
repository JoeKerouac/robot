package com.joe.robot.wx;

import com.joe.http.client.IHttpClient;
import com.joe.robot.Robot;
import com.joe.robot.comment.Msg;
import com.joe.robot.dto.LoginEvent;
import com.joe.robot.exception.RobotException;
import com.joe.robot.ext.LoginListener;
import com.joe.robot.ext.MsgListener;
import com.joe.robot.ext.QrCallback;
import com.joe.robot.wx.dto.*;
import com.joe.robot.wx.service.URLService;
import com.joe.utils.img.IQRCode;
import com.joe.utils.parse.json.JsonParser;
import com.joe.utils.parse.xml.XmlParser;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信机器人
 * <p>
 * 该机器人创建出来后不需要做任何操作（除非是关闭），只需要在注册的监听中处理相应事件即可
 *
 * @author joe
 */
public class WxRobot extends Robot {
    private static final XmlParser xmlParser = XmlParser.getInstance();
    private static final JsonParser parser = JsonParser.getInstance();
    //登录成功正则
    private static final Pattern LOGINPATTERN = Pattern.compile("window\\.code=200;\nwindow.redirect_uri=\"(.*)\";");
    //等待用户确认正则
    private static final Pattern LOGINWAITPATTERN = Pattern.compile("window\\.code=201.*");
    //获取消息正则
    Pattern MSGPATTERN = Pattern.compile("window\\.synccheck=\\{retcode:\"([0-9]+)\",selector:\"([0-9])\"}");
    //当前的登录状态（最后一次变更的登录状态）
    private volatile LoginEvent last;
    //微信配置
    private WxProperties properties;
    //初始化信息
    private Init init;

    public WxRobot(MsgListener msgListener, LoginListener loginListener, QrCallback callback, IHttpClient client) {
        super(msgListener, loginListener, callback, client);
    }

    protected void loginByQr() {
        getQr(getQRid());
    }

    protected synchronized void shutdown0() {
        updateLoginStatus(LoginEvent.LOGOUT);
        logger.warn("用户关闭机器人");
    }

    /**
     * 获取二维码ID
     *
     * @return 返回二维码ID
     */
    private String getQRid() {
        logger.info("开始构建获取二维码ID请求............");
        String url = URLService.getQRIdURL();
        String params;
        try {
            params = clientUtil.executeGet(url);
            logger.info("获取二维码请求响应为：{}", params);
            Pattern pattern = Pattern.compile("window.QRLogin.code = [0-9]{3}; window.QRLogin.uuid = \"(.*)\";");
            Matcher matcher = pattern.matcher(params);
            if (matcher.find()) {
                String qrid = matcher.group(1);
                logger.info("获取到二维码的ID，ID为" + qrid);
                return qrid;
            } else {
                throw new RobotException("获取QRID失败，未找到QRID");
            }
        } catch (Exception e) {
            logger.error("获取QRID失败", e);
            throw e instanceof RobotException ? (RobotException) e : new RobotException("获取QRID失败", e);
        }
    }

    /**
     * 获取二维码
     *
     * @param qrid 前边获取到的qrid
     * @return 二维码解析出来的内容
     */
    private void getQr(String qrid) {
        logger.info("开始构建请求............");
        String url = URLService.getQrUrl(qrid);
        logger.debug("准备请求二维码{}", qrid);
        try {
            String qr = IQRCode.read(new URL(url));
            logger.debug("{}对应的二维码内容为：{}", qrid, qr);
            callback.call(qr);
            updateLoginStatus(LoginEvent.CREATE);
            loginListen(qrid);
            return;
        } catch (Exception e) {
            logger.error("获取二维码失败，失败原因：", e);
            throw e instanceof RobotException ? (RobotException) e : new RobotException("获取二维码失败", e);
        }
    }

    /**
     * 二维码状态监听
     *
     * @param qrid 前边获取到的二维码ID
     */
    private void loginListen(String qrid) {
        logger.info("开始监听用户二维码{}状态............", qrid);
        String url = URLService.getLoginURL(qrid);

        while (last != LoginEvent.SUCCESS) {
            try {
                logger.debug("开始请求获取二维码{}的状态", qrid);
                String params = clientUtil.executeGet(url);
                Matcher matcher1 = LOGINPATTERN.matcher(params);
                Matcher matcher2 = LOGINWAITPATTERN.matcher(params);
                logger.debug("获取二维码{}的状态为：{}", qrid, params);

                if (matcher1.find()) {
                    updateLoginStatus(LoginEvent.SUCCESS);
                    String initUrl = matcher1.group(1);
                    logger.info("二维码ID[{}]对应的用户登陆成功....................", qrid);
                    //
                    properties = loginInit(initUrl);
                    init = initUserInfo(5);
                    openNotify();
                    //开始消息监听，必须一个用户一个线程，因为该线程是堵塞的线程
                    new Thread(this::listenMsg, init.getUser().getNickName() + "的消息监听线程").start();
                    break;
                } else if (matcher2.find()) {
                    //等待300毫秒继续
                    updateLoginStatus(LoginEvent.WAIT);
                    logger.debug("请在手机微信端点击登录....................");
                } else {
                    updateLoginStatus(LoginEvent.TIMEOUT);
                    //登录超时，请重新获取二维码
                    logger.warn("登录超时，返回结果：{}，二维码{}过期", params, qrid);
                    break;
                }
            } catch (Exception e) {
                if (e instanceof RobotException) {
                    logger.debug("登陆过程中有异常停止二维码状态监听");
                    updateLoginStatus(LoginEvent.LOGINFAIL);
                    throw (RobotException) e;
                }
                logger.warn("微信二维码{}监听网络请求；微信机器人将继续监听", qrid, e);
            }
        }
    }

    /**
     * 登录初始化
     *
     * @param initUrl 二维码状态监听获取到的初始化URL
     * @return 获取到的配置信息
     */
    private WxProperties loginInit(String initUrl) {
        logger.info("开始初始化登录信息，初始化URL为：{}............", initUrl);
        try {
            String result = clientUtil.executeGet(URLService.getLoginInitURL(initUrl));
            logger.debug("获取到的登录信息为：{}", result);
            WxProperties wxProperties = xmlParser.parse(result, WxProperties.class);
            logger.debug("解析后的对象为：{}", wxProperties);
            return wxProperties;
        } catch (Exception e) {
            logger.info("登录信息初始化异常，异常原因：", e);
            throw new RobotException("登录初始化出错", e);
        }
    }

    /**
     * 初始化用户信息
     *
     * @param retry 重试次数，建议大于等于2
     * @return 用户信息，如果返回null说明没有获取到用户信息
     */
    private Init initUserInfo(int retry) {
        logger.info("开始初始化信息，总共尝试{}次", retry);
        String url = URLService.getInitURL(properties);
        String request = parser.toJson(createBaseRequest());
        String dataStr = "{\"BaseRequest\":" + request + "}";
        logger.debug("构建的初始化用户信息需要的数据为：{}", dataStr);
        try {
            while (retry > 0) {
                retry--;
                logger.debug("开始第{}次尝试初始化用户信息", retry);
                String result = clientUtil.executePost(url, dataStr);
                logger.info("微信服务器响应结果为：{}", result);
                if (result.trim().equals("")) {
                    // 响应有可能为空，第一次一般都为空
                    continue;
                } else {
                    Init init = parser.readAsObject(result, Init.class);
                    if ("0".equals(String.valueOf(init.getBaseResponse().getRet()))) {
                        //如果获取到了消息说明此时真正登录成功，否则还有可能是登录失败
                        updateLoginStatus(LoginEvent.LOGIN);
                        return init;
                    } else {
                        continue;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("初始化用户信息出错", e);
            throw new RobotException("初始化用户信息出错", e);
        }
    }


    /**
     * 开启微信状态通知
     */
    private void openNotify() {
        logger.info("准备开启状态通知");

        String id = init.getUser().getUserName();
        String url = URLService.getStatusNotifyURL(properties);

        StringBuilder sb = new StringBuilder();
        String dataStr = parser.toJson(createBaseRequest());

        //构建请求数据
        sb.append(",\"Code\":3").append(",\"FromUserName\":\"").append(id).append("\",\"ToUserName\":\"").append(id)
                .append("\",\"ClientMsgId\":").append(System.currentTimeMillis()).append("}");
        dataStr = "{\"BaseRequest\":" + dataStr + sb.toString();
        logger.debug("开启状态通知需要的数据为：{}，构建完毕", dataStr);

        try {
            clientUtil.executePost(url, dataStr);
            logger.info("开启状态通知成功");
        } catch (Exception e) {
            logger.error("开启状态通知失败", e);
            throw new RobotException("状态通知开启失败", e);
        }
    }

    /**
     * 监听消息
     */
    private void listenMsg() {
        while (last == LoginEvent.LOGIN) {
            logger.info("检查是否有新消息.....................");
            try {
                String url = URLService.getListenURL(properties, init);
                logger.info("发送同步消息请求....................");
                String result = clientUtil.executeGet(url);


                Matcher matcher = MSGPATTERN.matcher(result);
                logger.info("微信响应消息为：{}", result);
                if (matcher.find()) {
                    String retcode = matcher.group(1);

                    if ("0".equals(retcode)) {
                        String selector = matcher.group(2);
                        // 无论是什么图片消息还是文字还是其他，selector都应该是2
                        //暂时不知道4是什么，6是添加好友后推送的消息，7是notify消息
                        switch (selector) {
                            case "2":
                            case "4":
                            case "6":
                            case "7":
                                // 表明有新消息
                                logger.info("有新消息了，开始处理新消息");
                                //处理新消息
                                proccess();
                                break;
                            case "0":
                                logger.debug("本次没有新消息");
                                break;
                            default:
                                logger.error("selector 是 ：{}，没有这种selector", selector);
                                break;
                        }
                    } else if ("1101".equals(retcode)) {
                        //只有等于0时才是成功，某些时候登录后获取消息会返回1101，表示登录失败，此时要退出登录
                        updateLoginStatus(LoginEvent.LOGOUT);
                        logger.debug("退出登录");
                        return;
                    } else {
                        logger.debug("收到的retcode为：{}，该状态码未知", retcode);
                        updateLoginStatus(LoginEvent.LOGINFAIL);
                        return;
                    }
                }
                logger.info("消息同步完成.........................");
            } catch (Exception e) {
                logger.error("消息同步失败，原因：{}；继续同步", e);
            }
        }
    }

    /**
     * 处理消息，当有新消息时调用该方法处理
     */
    private void proccess() {
        logger.info("有新消息，开始处理新消息");
        WxMsg wxMsg = receive();
        if (wxMsg == null) {
            logger.warn("本次没有读取到新消息");
            return;
        }

        if (wxMsg.getDelContactList() != null && !wxMsg.getDelContactList().isEmpty()) {
            logger.warn("手机端删除了好友:{}", wxMsg.getDelContactList());
        }

        init.setSyncKeyList(wxMsg.getSyncKeyList());
        List<WxMsgBody> msgs = wxMsg.getAddMsgList();
        if (msgs != null && !msgs.isEmpty()) {
            for (WxMsgBody msg : msgs) {
                if (msg.getFromUserName().equals(init.getUser().getUserName())) {
                    logger.debug("消息[{}]是从手机端发往其他用户的，不处理", msg);
                    continue;
                }
                proccessMsg(msg);
            }
        }
    }

    /**
     * 监听消息，收到的所有消息由该方法负责处理
     *
     * @param wxMsg
     */
    private void proccessMsg(WxMsgBody wxMsg) {
        logger.debug("开始使用监听器处理消息：{}", wxMsg);
        if (wxMsg.getMsgType() == 1) {
            //文本消息
            Msg msg = new Msg();
            String from = wxMsg.getFromUserName();
            String to = wxMsg.getToUserName().substring(1);
            msg.setMsgid(wxMsg.getMsgId());
            msg.setTo(to);
            msg.setContent(wxMsg.getContent().replaceAll("&amp;", "&").replaceAll("&gt;", ">").replaceAll("&lt;",
                    "<").replaceAll("&quot;", "\"").replaceAll("<br/>", "\n"));
            msg.setFrom(from);
            if (from.startsWith("@@")) {
                logger.debug("消息是群消息");
                msgListener.groupMsg(msg, this);
            } else {
                logger.debug("消息是好友消息");
                msgListener.friendMsg(msg, this);
            }
        } else if (wxMsg.getMsgType() == 37) {
            logger.debug("消息是好友添加消息");
            //首先解析好友消息
            String[] params = wxMsg.getContent().replaceAll("\"", "").split("\\s{1,}");
            Map<String, String> map = new HashMap<>();
            for (String param : params) {
                String[] entity = param.split("=");
                if (entity.length > 1) {
                    map.put(entity[0], entity[1]);
                }
            }
            //添加好友时填写的附加信息
            String content = map.get("content");
            //此人的昵称
            String fromnikeName = map.get("fromnickname");
            //此人的账号
            String account = map.get("alias");
            //微信ID，用于同意好友
            String username = wxMsg.getRecommendInfo().getUserName();

            if (msgListener.addMsg(account, fromnikeName, content, this)) {
                logger.info("用户同意添加{}为好友", account);
                allowAddFriend(username, msgListener.remarke(account, fromnikeName, content, this), wxMsg
                        .getRecommendInfo
                                ().getTicket());
            }
        } else if (wxMsg.getMsgType() == 10002) {
            logger.debug("有人撤回消息了");
            String content = wxMsg.getContent();
            logger.debug("撤回的消息是：{}", content);
            //解析content
            String xml = content.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("<br/>" , "");
            Map<String, Object> map = xmlParser.parse(xml);
            String msgid = (String) ((Map<String, Object>) map.get("revokemsg")).get("msgid");
            logger.debug("调用撤回消息处理");
            msgListener.revokeMsg(msgid, this);
        } else {
            logger.debug("消息{}暂时不能处理", wxMsg);
        }
    }

    /**
     * 接收消息
     *
     * @return 本次接收到的消息，返回null时说明接受消息时出错
     */
    private WxMsg receive() {
        logger.info("开始接收消息");
        String url = URLService.getReceiveMsg(properties);

        //获取消息需要的数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("SyncKey", init.getSyncKeyList());
        dataMap.put("BaseRequest", createBaseRequest());
        dataMap.put("rr", ~(int) System.currentTimeMillis());
        logger.debug("获取消息需要的数据为：{}", dataMap);

        // 生成请求必要数据
        try {
            String msg = clientUtil.executePost(url, parser.toJson(dataMap));
            WxMsg wxMsg = parser.readAsObject(msg, WxMsg.class);
            logger.info("微信响应数据为：{}", wxMsg);
            return wxMsg;
        } catch (Exception e) {
            logger.error("消息解析错误，错误信息为：{}", e);
            return null;
        }
    }

    /**
     * 同意添加好友
     *
     * @param username   同意添加的好友的微信ID
     * @param remarkName 备注
     */
    private void allowAddFriend(String username, String remarkName, String ticket) {
        logger.debug("同意添加{}为好友", username);
        Map<String, Object> params = new HashMap<>();
        params.put("UserName", username);
        params.put("RemarkName", remarkName);
        params.put("CmdId", 2);
        BaseRequest baseRequest = createBaseRequest();
        params.put("BaseRequest", baseRequest);
        logger.debug("同意添加好友信息构建完成，准备发送");
        String url = URLService.getAllowAddFriedURL(properties);
        try {
            String result = clientUtil.executePost(url, parser.toJson(params));
            logger.info("添加好友{}的结果为：{}", username, result);

            logger.debug("开始添加好友验证数据");
            //发送完这个后需要再发送一个验证请求
            Map<String, Object> verifyData = new HashMap<>();
            verifyData.put("BaseRequest", baseRequest);
            verifyData.put("Opcode", 3);
            verifyData.put("VerifyUserListSize", 1);
            Map m = new HashMap();
            m.put("Value", username);
            m.put("VerifyUserTicket", ticket);
            verifyData.put("VerifyUserList", Collections.singleton(m));

            verifyData.put("VerifyContent", "我就试一下");//该值看起来像是能发送给对方的，但实际测试并没有什么卵用
            verifyData.put("SceneListCount", 1);
            verifyData.put("SceneList", Collections.singleton(33));
            verifyData.put("skey", baseRequest.getSkey());
            logger.debug("添加好友验证数据为：{}，准备发送验证", verifyData);
            String verifyResult = clientUtil.executePost(URLService.getAddFriendVerifyURL(properties), parser.toJson
                    (verifyData));
            logger.debug("添加好友验证结果为：{}", verifyResult);
        } catch (IOException e) {
            logger.error("添加好友{}失败", username);
        }
    }

    /**
     * 获取baserequest请求体
     *
     * @return BaseRequest
     */
    private BaseRequest createBaseRequest() {
        BaseRequest request = new BaseRequest();
        request.setDeviceId("e" + (long) (Math.random() * 10000000000000000L));
        request.setSid(properties.getWxsid());
        request.setSkey(properties.getSkey());
        request.setUin(Long.parseLong(properties.getWxuin()));
        return request;
    }

    /**
     * 更新登录状态
     *
     * @param event 当前登录状态
     */
    private synchronized void updateLoginStatus(LoginEvent event) {
        if (last != event) {
            this.last = event;
            loginListener.listen(event);
        }
    }

    /**
     * 发送消息
     *
     * @param to  接收人
     * @param msg 要发送的消息
     * @return 返回true表示消息发送成功，返回false表示消息发送失败
     */
    protected boolean sendMsg0(String to, String msg) {
        logger.info("准备发送消息.........................................................");
        String url = URLService.getSendMsgURL(properties);
        try {
            Send send = createSendMsgBody(to, msg, 1);
            logger.debug("要发送的消息是{}：", send);
            String result = clientUtil.executePost(url, parser.toJson(send));
            logger.info("微信响应消息：{}", result);
            SendStatus status = parser.readAsObject(result, SendStatus.class);
            if (status.getBaseResponse().getRet() != 0) {
                logger.warn("消息{}发送失败", send);
                return false;
            }
            logger.info("消息发送完毕");
            return true;
        } catch (Exception e) {
            logger.error("网络连接错误，发送消息失败", e);
            return false;
        }
    }


    /**
     * 生成要发送的消息
     *
     * @param to      接收人
     * @param content 内容
     * @param type    类型，文本是1，其他未知
     * @return 发送的消息对象
     */
    private Send createSendMsgBody(String to, String content, int type) {
        Send send = new Send();

        BaseRequest request = createBaseRequest();

        SendMsg msg = new SendMsg();
        msg.setFrom(init.getUser().getUserName());
        msg.setContent(content);
        msg.setTo(to);
        msg.setType(type);
        String id = System.currentTimeMillis() + "" + (int) (Math.random() * 9000 + 1000);
        msg.setClientMsgId(id);
        msg.setLocalId(id);

        send.setRequest(request);
        send.setMsg(msg);
        send.setScene(0);

        return send;
    }
}
