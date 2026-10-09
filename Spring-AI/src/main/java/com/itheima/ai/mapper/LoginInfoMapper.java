package com.itheima.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.ai.entity.LoginInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface LoginInfoMapper extends BaseMapper<LoginInfo> {
    @Select("SELECT COUNT(*) FROM login_info WHERE username = #{username}")
    Integer isUsernameExist(String username);

    @Insert("INSERT INTO login_info (id,username, password) VALUES ((SELECT b from(" +
            "SELECT GREATEST((SELECT COALESCE(MAX(id), 0) + 1 FROM login_info " +
            "WHERE id<10*#{range} AND id>=#{range}),#{range}) " +
            "as b )as a),#{username}, #{password})")
    int insert(String username,String password,Integer range);


    @Select("select * from login_info where id=#{id}")
    LoginInfo getProductById(int id);

    @Update("Update login_info set password=#{password} where id=#{id}")
    void updateById(Integer id, String password);

}
