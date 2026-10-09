package com.itheima.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@TableName("teacher")
@Data
public class Teacher {
    @Id
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String realName;
    private String sex;
    private String phone;
}
