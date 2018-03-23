package com.joe.robot;

import com.joe.http.client.IHttpClient;
import com.joe.robot.exception.RobotException;
import com.joe.robot.ext.LoginListener;
import com.joe.robot.ext.MsgListener;
import com.joe.robot.ext.QrCallback;
import com.joe.robot.tbk.TbkClient;
import com.joe.robot.wx.WxRobot;

/**
 * 机器人工厂
 *
 * @author joe
 */
public class RobotFactory {
    public static Robot create(MsgListener msgListener, LoginListener loginListener, QrCallback callback, IHttpClient
            client,
                               Robot.RobotType type) {
        switch (type) {
            case WX:
                return new WxRobot(msgListener, loginListener, callback, client);
            case TBK:
                return new TbkClient(msgListener, loginListener, callback, client);
            default:
                throw new RobotException("当前没有指定机器人：" + type);
        }
    }
}
