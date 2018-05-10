package com.joe.robot.tbk;

import com.joe.http.client.IHttpClient;
import com.joe.robot.Robot;
import com.joe.robot.dto.LoginEvent;
import com.joe.robot.exception.RobotException;
import com.joe.robot.ext.LoginListener;
import com.joe.robot.ext.MsgListener;
import com.joe.robot.ext.QrCallback;
import com.joe.utils.concurrent.ThreadUtil;
import com.joe.utils.img.IQRCode;
import com.joe.utils.parse.json.JsonParser;
import lombok.Data;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 淘宝客客户端，一个对象代表一个淘宝客，请勿直接创建该对象，应该使用Robot类创建该对象
 * <p>
 * 该对象创建完毕后先通过login方法登录，然后可以使用该对象的convert方法转换链接
 *
 * @author joe
 */
public class TbkClient extends Robot {
    private static final JsonParser parser = JsonParser.getInstance();
    //执行二维码状态查询使用的线程池
    private static final ExecutorService POOL = ThreadUtil.createPool(ThreadUtil.PoolType.IO);
    //匹配获取二维码结果中url和token的正则
    private static Pattern QRURLPATTERN = Pattern.compile(".*\"url\":\"(.*)\".*\"lgToken\":\"([0-9a-zA-Z]*)\".*");
    //获取二维码链接的地址，其中有一个%d参数（时间戳）需要动态设置
    private static final String GETQRURL = "https://qrlogin.taobao.com/qrcodelogin/generateQRCode4Login" +
            ".do?from=alimama&_ksTS=%d_30&callback=jsonp31";
    //检查二维码链接的地址，其中有一个token需要替换为获取二维码地址时
    private static final String CHECKQR = "https://qrlogin.taobao.com/qrcodelogin/qrcodeLoginCheck" +
            ".do?lgToken=token&defaulturl=http%3A%2F%2Fwww.alimama.com";
    //地址转换链接，其中有两个%d参数，第一个%d参数是对应的auctionid，该参数从搜索商品中获取，第二个%d对应的是时间戳
    private static final String CONVERT = "https://pub.alimama.com/common/code/getAuctionCode" +
            ".json?auctionid=%d&adzoneid=68372906&siteid=19878387&scenes=1&t=%d";
    //搜索链接，其中有三个参数，第一个%s是要搜索的链接，第二个和第三个%d都是时间戳
    private static final String SEARCH = "https://pub.alimama.com/items/search" +
            ".json?q=%s&_t=%d&auctionTag=&perPageSize=40&shopTag=yxjh&t=%d";
    //登录状态，-1表示上次登录过程中发生异常，0表示未开始登录，1表示已经登录（二维码扫描成功并确认），2表示等待扫描二维码，3表示二维码扫描成功未确认，4表示二维码失效
    private volatile LoginEvent event;

    public TbkClient(MsgListener msgListener, LoginListener loginListener, QrCallback callback, IHttpClient client) {
        super(msgListener, loginListener, callback, client);
    }

    @Override
    protected boolean sendMsg0(String to, String msg) {
        logger.warn("淘宝客没有发送消息的接口");
        return false;
    }

    @Override
    protected void shutdown0() {
    }

    /**
     * 使用二维码登录
     *
     * @throws Exception 一般不会抛出异常
     */
    @Override
    protected synchronized void loginByQr() {
        try {
            //必须使用独立的IHttpClient，因为同一个client只能维持一个淘宝账号
            //同时只有在登录时才创建client
            logger.debug("准备使用二维码登录");
            //获取二维码链接的URL
            String getQrUrl = String.format(GETQRURL, System.currentTimeMillis());

            logger.debug("初始化cookie2");
            //请求首页，请求的时候回设置一个cookie2，用于后续认证
            clientUtil.executeGet("https://www.alimama.com/index.htm");
            logger.debug("cookie2初始化完毕，准备获取二维码链接");

            String qrResult = clientUtil.executeGet(getQrUrl);
            logger.debug("二维码链接获取结果为：{}", qrResult);
            //提取URL
            Matcher matcher = QRURLPATTERN.matcher(qrResult);

            String imgUrl;
            String lgToken;
            if (matcher.matches()) {
                imgUrl = "http:" + matcher.group(1);
                lgToken = matcher.group(2);
            } else {
                throw new RuntimeException("未获取到二维码");
            }

            logger.debug("获取到的二维码路径为：{}；lgToken为：{}", imgUrl, lgToken);

            //读取二维码信息
            String qrinfo = IQRCode.read(new URL(imgUrl));
            super.callback.call(qrinfo);
            //检查二维码状态
            String checkQr = CHECKQR.replace("token", String.valueOf(lgToken));

            //异步检查用户登录状态，该线程池的数量限制了并发登录的用户数，同时登陆的用户不能超过该线程池的最大线程数
            POOL.submit(() -> {
                try {
                    CheckQrResult checkQrResult;
                    while (true) {
                        logger.debug("检查二维码状态");
                        String checkQrResultStr = clientUtil.executeGet(checkQr);
                        logger.debug("二维码状态为：{}", checkQrResultStr);
                        checkQrResult = parser.readAsObject(checkQrResultStr, CheckQrResult.class);
                        switch (checkQrResult.getCode()) {
                            case "10000":
                                //未扫描二维码，并且二维码未过期
                                logger.debug("请扫描二维码");
                                updateStatus(LoginEvent.CREATE);
                                continue;
                            case "10001":
                                logger.debug("请在手机上确认");
                                updateStatus(LoginEvent.WAIT);
                                continue;
                            case "10004":
                                logger.warn("二维码已经失效，请重新获取");
                                updateStatus(LoginEvent.TIMEOUT);
                                return;
                            case "10006":
                                logger.debug("登录成功");
                                updateStatus(LoginEvent.SUCCESS);
                                //刷新cookie
                                String url = checkQrResult.getUrl();
                                logger.debug("刷新cookie2");
                                String refreshResult = clientUtil.executeGet(url);
                                logger.debug("cookie刷新结果是：{}", refreshResult);
                                updateStatus(LoginEvent.LOGIN);
                                return;
                        }
                    }
                } catch (Exception e) {
                    updateStatus(LoginEvent.LOGINFAIL);
                    logger.error("登录过程中发生异常，登录失败", e);
                }

            });
        } catch (Exception e) {
            logger.debug("淘宝客机器人登录时发生异常", e);
            throw new RobotException(e);
        }
    }

    /**
     * 更新登录状态，更新的同时会调用
     *
     * @param event 要更新的登录状态
     */
    private void updateStatus(LoginEvent event) {
        if (this.event != event) {
            super.loginListener.listen(event);
            this.event = event;
        }
    }

    /**
     * 获取当前登录状态
     *
     * @return 当前的登录状态，有可能不是实时的（用户已经退出登陆但是该客户端并不知道）
     */
    public LoginEvent getStatus() {
        return this.event;
    }

    /**
     * 转换链接
     *
     * @param url 要搜索的链接
     * @return 搜索结果，如果搜索异常（例如当前用户登录信息丢失，目前暂时只有这一种情况），那么直接抛出异常，如果搜索
     * 为空那么返回的结果中success字段为false，否则为true
     */
    public SearchResult convert(String url) throws Exception {
        logger.info("开始搜索链接：{}", url);
        String search = String.format(SEARCH, URLEncoder.encode(url, "UTF8"), System.currentTimeMillis(), System
                .currentTimeMillis());
        String searchResultStr = clientUtil.executeGet(search);
        logger.debug("链接[{}]的搜索结果为：{}", url, searchResultStr);
        Map searchMap = (Map) parser.readAsObject(searchResultStr, Map.class).get("data");
        SearchResult searchResult = new SearchResult();

        List<Map> goodsList;

        if (searchMap.get("pageList") == null || (goodsList = (List<Map>) searchMap.get("pageList")).isEmpty()) {
            logger.warn("搜索[{}]的结果为空，本次没有搜索到内容", url);
            searchResult.setSuccess(false);
        } else {
            logger.debug("本次搜索到了商品：{}", goodsList);
            //转换商品
            Map goodsMap = goodsList.get(0);
            Goods goods = parser.readAsObject(parser.toJson(goodsMap), Goods.class);
            searchResult.setGoods(goods);

            if (goods.getAuctionId() == 0) {
                logger.warn("搜索到的商品{}中不包含auctionid", goods);
                searchResult.setSuccess(false);
            } else {
                logger.debug("开始获取商品的链接");
                searchResult.setSuccess(true);
                searchResult.setLink(convert(goods.getAuctionId()));
                logger.debug("获取到的链接为：{}", searchResult.getLink());
            }
        }
        return searchResult;
    }

    /**
     * 根据auctionid获取淘客链接
     *
     * @param auctionid auctionid，搜索转换链接时得到的
     * @return 淘客链接
     * @throws Exception 未登录时将抛出异常
     */
    private Link convert(long auctionid) throws Exception {
        logger.debug("开始转换{}", auctionid);
        String convert = String.format(CONVERT, auctionid, System.currentTimeMillis());
        String convertResult = clientUtil.executeGet(convert);
        logger.debug("转换结果为：{}", convertResult);
        Link link = parser.readAsObject(parser.toJson(parser.readAsObject(convertResult, Map.class).get("data")),
                Link.class);
        logger.debug("得到的链接为：{}", link);
        return link;
    }

    /**
     * 二维码状态检查结果
     */
    @Data
    private static class CheckQrResult {
        private String message;
        private String code;
        private boolean success;
        private String url;
    }
}
