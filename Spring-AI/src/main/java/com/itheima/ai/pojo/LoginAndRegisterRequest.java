package com.example.demo.pojo;

import lombok.Data;

@Data
public class LoginAndRegisterRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码Key
     */
    private String verifyCodeKey;
    /**
     * 验证码
     */
    private String verifyCode;
    /**
     * 角色选择
     */
    private String radio;
}
