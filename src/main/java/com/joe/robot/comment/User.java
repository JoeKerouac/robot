package com.joe.robot.comment;

import lombok.Data;

import java.util.List;

/**
 * 通用用户
 *
 * @author joe
 */
@Data
public class User {
    //用户ID，如果是群的话就是群ID
    private String uid;
    //用户昵称，如果是群的话就是群名
    private String nikename;
    //好友列表
    private List<User> friends;
    //群成员，当是群的时候有该值
    private List<User> members;
    //签名
    private String signature;
    //用户类型
    private UserType userType;

    public static enum UserType {
        USER("用户"), GROUP("群");
        private String type;

        UserType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "UserType{" +
                    "type='" + type + '\'' +
                    '}';
        }
    }
}
