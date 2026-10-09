package com.itheima.ai.pojo;

import lombok.Data;

@Data
public class LoginResponse {
    private String code;
    private Integer id;

    public LoginResponse(String code, Integer id) {
        this.code= code;
        this.id = id;
    }
}
