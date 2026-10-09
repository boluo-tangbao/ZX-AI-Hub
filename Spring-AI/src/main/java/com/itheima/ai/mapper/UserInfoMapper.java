package com.itheima.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ai.entity.UserInfo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    @Select("select t.id,tn.name from teacher t,teacher_name tn where t.id = tn.id")
    List<UserInfo> findAllUserWithName();
}
