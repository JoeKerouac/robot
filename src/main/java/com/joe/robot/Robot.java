package com.joe.robot;

import com.joe.http.IHttpClientUtil;
import com.joe.http.client.IHttpClient;
import com.joe.robot.comment.Msg;
import com.joe.robot.exception.RobotException;
import com.joe.robot.ext.LoginListener;
import com.joe.robot.ext.MsgListener;
import com.joe.robot.ext.QrCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 机器人启动类
 *
 * @author:joe
 */
public abstract class Robot {
    protected static final Logger logger = LoggerFactory.getLogger(Robot.class);
    private static final MsgListener DEFAULT = new MsgListener() {
        @Override
        public void friendMsg(Msg msg, Robot robot) {
            logger.warn("没有消息监听，忽略好友消息：{}", msg);
        }

        @Override
        public void groupMsg(Msg msg, Robot robot) {
            logger.warn("没有消息监听，忽略群消息：{}", msg);
        }

        @Override
        public void revokeMsg(String msgid, Robot robot) {
            logger.warn("没有消息监听，忽略撤回消息：{}", msgid);
        }

        @Override
        public boolean addMsg(String account, String nikeName, String content, Robot robot) {
            logger.warn("没有消息监听，忽略好友添加");
            return false;
        }

        @Override
        public String remarke(String account, String nikeName, String content, Robot robot) {
            return null;
        }
    };
    protected MsgListener msgListener;
    protected LoginListener loginListener;
    protected QrCallback callback;
    protected IHttpClient client;
    //网络请求客户端
    protected IHttpClientUtil clientUtil;

    protected Robot(MsgListener msgListener, LoginListener loginListener, QrCallback callback, IHttpClient client) {
        //如果两个监听器等于空那么即使机器人启动起来也没用，所以只要有一个为空就不实际创建机器人
        if (loginListener != null && callback != null) {
            client = client == null ? IHttpClient.builder().build() : client;
            this.client = client;
            this.msgListener = msgListener == null ? DEFAULT : msgListener;
            this.loginListener = loginListener;
            this.callback = callback;
            this.clientUtil = new IHttpClientUtil(this.client);
        } else {
            throw new RobotException("缺少必要的监听");
        }
    }

    /**
     * 登录
     */
    public void login() {
        loginByQr();
    }

    /**
     * 子类实现使用二维码登录
     */
    protected abstract void loginByQr();

    /**
     * 发送消息
     *
     * @param to  消息接收人
     * @param msg 消息正文
     * @return 消息发送状态，返回true表示发送成功
     */
    public boolean sendMsg(String to, String msg) {
        return sendMsg0(to, msg);
    }

    /**
     * 子类实现发送消息
     *
     * @param to  消息接收人
     * @param msg 消息正文
     * @return 消息发送状态，返回true表示发送成功
     */
    protected abstract boolean sendMsg0(String to, String msg);

    /**
     * 关闭机器人
     */
    public void shutdown() {
        try {
            client.close();
        } catch (Exception e) {
            logger.warn("机器人关闭中发生了异常", e);
        }
        shutdown0();
    }

    /**
     * 子类实现关闭机器人
     */
    protected abstract void shutdown0();


    public static enum RobotType {
        WX("微信机器人"), QQ("QQ机器人"), TBK("淘宝客机器人");
        private String type;

        RobotType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "RobotType{" +
                    "type='" + type + '\'' +
                    '}';
        }
    }
}
