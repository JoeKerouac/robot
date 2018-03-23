package com.joe.robot.tbk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 搜索出来的商品信息
 *
 * @author joe
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Goods {
    //当前销售价格，单位元
    private double zkPrice;
    //佣金，单位为元
    private double tkCommFee;
    //可使用优惠券金额，单位为元
    private double couponAmount;
    //优惠券剩余数量
    private long couponLeftCount;
    //用于后续转换使用
    private long auctionId;
    //月销量
    private int biz30day;
    //标题
    private String title;
    //店名
    private String shopTitle;
}
