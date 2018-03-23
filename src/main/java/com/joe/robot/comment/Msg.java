package com.joe.robot.comment;

import lombok.Data;

/**
 * 通用消息
 *
 * @author joe
 */
@Data
public class Msg {
    //消息ID
    private String msgid;
    //消息发送人（接受消息时有该值）
    private String from;
    //消息内容
    private String content;
    //消息接收人（发送消息时有该值）
    private String to;
}
