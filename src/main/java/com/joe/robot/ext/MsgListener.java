package com.joe.robot.ext;

import com.joe.robot.Robot;
import com.joe.robot.comment.Msg;

/**
 * 消息监听
 *
 * @author joe
 */
public interface MsgListener {
    /**
     * 好友消息
     *
     * @param msg   消息
     * @param robot 对应的机器人
     */
    void friendMsg(final Msg msg, final Robot robot);

    /**
     * 群消息
     *
     * @param msg   消息
     * @param robot 对应的机器人
     */
    void groupMsg(final Msg msg, final Robot robot);

    /**
     * 撤回消息
     *
     * @param msgid 被撤回的消息的ID（不包含自己撤回的消息）
     * @param robot 对应的机器人
     */
    void revokeMsg(final String msgid, final Robot robot);

    /**
     * 好友添加消息
     *
     * @param account  待添加的好友的账号
     * @param nikeName 昵称
     * @param content  附加消息
     * @param robot    对应的机器人
     * @return 返回true表示通过好友添加，返回false表示拒绝好友添加
     */
    boolean addMsg(final String account, final String nikeName, final String content, final Robot robot);

    /**
     * 为新加好友添加备注
     *
     * @param account  待添加的好友的账号
     * @param nikeName 昵称
     * @param content  附加消息
     * @param robot    对应的机器人
     * @return 备注
     */
    String remarke(final String account, final String nikeName, final String content, final Robot robot);
}
