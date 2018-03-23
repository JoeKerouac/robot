package com.joe.robot.tbk;

/**
 * 登录监听
 *
 * @author joe
 */
public interface OldLoginListener {
    /**
     * 登录监听，当状态发生改变时执行
     *
     * @param tbkClient 当前客户端对象
     * @param status 当前登录状态，-1表示上次登录过程中发生异常，0表示未开始登录，1表示已经登录（二维码扫描成功并确认），2表示等待扫描二维码，3表示二维码扫描成功未确认，4表示二维码失效
     */
    void listen(TbkClient tbkClient, byte status);
}
