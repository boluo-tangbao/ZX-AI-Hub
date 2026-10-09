package com.itheima.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@TableName("user_info")
@Data
public class UserInfo {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String realName;
    //真名
    private String sex;
    //性别
    private Date birthDate;
    //生日
    private String tel;
    //电话
    private String bornPlace;
    //出生地
    private String livingPlace;
    //居住地
}
