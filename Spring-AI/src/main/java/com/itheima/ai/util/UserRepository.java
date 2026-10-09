package com.itheima.ai.utils;

import com.itheima.ai.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository接口
@Repository
public interface UserRepository extends JpaRepository<LoginInfo, Integer> {
    LoginInfo findByUsernameAndPassword(String username, String password);

    LoginInfo findByUsername(String username);
}