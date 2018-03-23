package com.joe.robot.ext;

/**
 * 二维码回调，获取到二维码后会回调该函数，同时将二维码信息传入（注意，不是二维码的URL，而是二维码包含的信息）
 *
 * @author joe
 */
public interface QrCallback extends Callback<String> {
}
