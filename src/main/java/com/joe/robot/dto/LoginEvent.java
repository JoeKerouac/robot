package com.joe.robot.dto;

/**
 * 登录状态
 *
 * @author joe
 */
public enum LoginEvent {
    CREATE("等待用户扫描"), TIMEOUT("二维码过期"), WAIT("用户已扫描，等待用户确认"), SUCCESS("用户扫描并确认"),
    LOGIN("登录成功"), LOGINFAIL("登录失败"), LOGOUT("退出登录");
    private String status;

    LoginEvent(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "status='" + status + '\'' +
                '}';
    }
}
