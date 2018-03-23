package com.joe.robot.ext;

/**
 * 回调函数
 *
 * @author joe
 */
public interface Callback<T> {
    /**
     * 回调
     *
     * @param data 回调数据
     */
    void call(T data);
}
