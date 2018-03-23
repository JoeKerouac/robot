package com.joe.robot.wx.service;

import com.joe.robot.wx.dto.Init;
import com.joe.robot.wx.dto.SyncKey;
import com.joe.robot.wx.dto.WxProperties;

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
                + "jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq" +
                ".com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_="
                + System.currentTimeMillis();
    }

    /**
     * 获取二维码URL
     *
     * @param qrid 二维码的QRID
     * @return 用来获取二维码的URL
     */
    public static String getQrUrl(String qrid) {
        return LOGIN_BASEURL + "qrcode/" + qrid;
    }

    /**
     * 获取添加好友验证URL
     *
     * @param properties 微信配置
     * @return 添加好友验证URL
     */
    public static String getAddFriendVerifyURL(WxProperties properties) {
        return BASEURL + "webwxverifyuser?r=" + System.currentTimeMillis() + "&lang=zh_CN&pass_ticket=" + properties
                .getPass_ticket();
    }

    /**
     * 获取登录URL
     *
     * @param id
     * @return
     */
    public static String getLoginURL(String id) {
        return LOGIN_BASEURL + "cgi-bin/mmwebwx-bin/login?loginicon=true&uuid=" + id + "&tip=1&r=-973800866&_="
                + System.currentTimeMillis();
    }

    /**
     * 获取登录初始化URL
     *
     * @param url 二维码登录成功后返回的初始化url
     * @return 登录初始化URL
     */
    public static String getLoginInitURL(String url) {
        url += "&fun=new&version=v2&lang=zh_CN";
        return url;
    }

    /**
     * 获取初始化URL
     *
     * @param wxPropties 登录初始化中获取的配置
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
     * @param wxPropties 登录初始化中获取的配置
     * @return
     */
    public static String getStatusNotifyURL(final WxProperties wxPropties) {
        return BASEURL + "webwxstatusnotify?lang=zh_CN&pass_ticket=" + wxPropties.getPass_ticket();
    }

    /**
     * 获取同意添加好友URL
     *
     * @param properties 微信配置
     * @return 同意添加好友的URL
     */
    public static String getAllowAddFriedURL(WxProperties properties) {
        return BASEURL + "webwxoplog?pass_ticket=" + properties.getPass_ticket();
    }

    /**
     * 获取消息监听URL
     *
     * @param properties 微信回话session
     * @return
     */
    public static String getListenURL(WxProperties properties, Init init) {
        String r = System.currentTimeMillis() + "";
        r = r.substring(4) + (int) (Math.random() * 10000);
        String r2 = System.currentTimeMillis() + "";
        r2 = r2.substring(4) + (int) (Math.random() * 10000);
        String url = BASEURL + "synccheck?r=" + r2 + "&skey=" + properties.getSkey() + "&sid=" +
                properties.getWxsid()
                + "&uin=" + properties.getWxuin() + "&deviceid=" + "e" + (long) (Math.random() *
                1000000000000000L)
                + "&synckey=";
        if (init.getSyncKeyList() != null) {
            for (SyncKey key : init.getSyncKeyList().getList()) {
                url += key.getKey() + "_" + key.getVal() + "%7C";
            }
        }
        url = url.substring(0, url.length() - 4);
        url += "&_=" + r;
        return url;
    }

    /**
     * 获取接受消息的URL
     *
     * @param properties 微信配置
     * @return
     */
    public static String getReceiveMsg(WxProperties properties) {
        String url = BASEURL + "webwxsync?sid=" + properties.getWxsid() + "&skey="
                + properties.getSkey() + "&lang=zh_CN&pass_ticket=" + properties.getPass_ticket();
        return url;
    }

    /**
     * 获取发送消息URL
     *
     * @param properties 微信配置
     * @return
     */
    public static String getSendMsgURL(WxProperties properties) {
        return BASEURL + "webwxsendmsg?&lang=zh_CN&pass_ticket="
                + properties.getPass_ticket();
    }
}
