package com.itheima.ai.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@TableName("login_info")
@Data
public class LoginInfo {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;//用户名
    private String password;//密码
    public LoginInfo() {

    }
    public LoginInfo(int id, String username, String password) {
    }


}

