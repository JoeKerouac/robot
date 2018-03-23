package com.joe.robot.tbk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 淘宝转换链接的结果
 *
 * @author joe
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
    //淘口令，示例：￥sbHt04smg07￥
    private String taoToken;
    //优惠券短链，示例：https://s.click.taobao.com/WHq9Uaw
    private String couponShortLinkUrl;
    //二维码链接（暂不需要）
    private String qrCodeUrl;
    //长链接，示例：https://s.click.taobao
    // .com/t?e=m%3D2%26s%3DH2l5Nx8m510cQipKwQzePOeEDrYVVa64LKpWJ%2Bin0XLjf2vlNIV67tSlx54sdJaq0e71iVTN2Rx5IeT
    // %2BfGLYpgdgC04DK4r9khCfxNfzXiDdIln8J6cqEQF%2Br00gq%2Fgr8v5XPGCNToHd8Ofi5gGsQokWnOVeAEhwomfkDJRs%2BhU%3D
    private String clickUrl;
    //优惠券淘口令，示例：￥gWCI04smgOn￥
    private String couponLinkTaoToken;
    //优惠券长链接，示例：https://uland.taobao.com/coupon/edetail?e=g0CGUrzentma2P%2BN2ppgB%2B0P3j1xglC0lc7h9G
    // %2FmDimQdsukec%2BPzmzbfL9YYAq%2BkitrWO9wjnswjaGmZ7jem8nFXC3XWYmcrWFtGDBhQ58%3D&pid
    // =mm_55617479_19878387_68372906&af=1
    private String couponLink;
    //类型，暂时未知
    private String type;
    //短连接，有效期300天
    private String shortLinkUrl;
}
