package com.itheima.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ai.constants.Result;
import com.itheima.ai.mapper.UserInfoMapper;
import com.itheima.ai.entity.LoginInfo;
import com.itheima.ai.mapper.LoginInfoMapper;
import com.itheima.ai.pojo.LoginResponse;
import com.itheima.ai.pojo.LoginAndRegisterRequest;
import com.itheima.ai.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    LoginInfoMapper loginInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;

    @Autowired
    private UserRepository userRepository;


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id) {
        loginInfoMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping
    public Result<?> updatePwd(@RequestBody LoginInfo loginInfo) {
        // 前端传过来的数据password字段为null。
        // 下列操作可以保证插入数据库的数据中password字段值不为null，这一步可以不做
        // 后来发现包含null字段的实体插入数据库时，null不会覆盖原有数据，或许是框架自身功能，因此这一步可以不做
        // Teacher fromDb = teacherMapper.selectOne(Wrappers.<Teacher>lambdaQuery().eq(Teacher::getId, teacher.getId()));
        // teacher.setPassword(fromDb.getPassword());

        loginInfoMapper.updateById(loginInfo.getId(), loginInfo.getPassword());
        return Result.success("0");
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer selectDep) {
        LambdaQueryWrapper<LoginInfo> wrapper = Wrappers.<LoginInfo>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(LoginInfo::getId, search);
        }
/*        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Teacher::getName, search);
        }*/
        // SystemAdministrator字段不需要在前端展示，删去
        wrapper.ne(LoginInfo::getId, 100);
        Page<LoginInfo> teacherPage = loginInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(teacherPage);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginAndRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 如果Redis中不存在数据或密码不匹配，则去数据库中验证用户名和密码
        LoginInfo usernameInfo = userRepository.findByUsername(username);
        LoginInfo usernameAndPasswordInfo = userRepository.findByUsernameAndPassword(username, password);

        if (usernameInfo != null) {
            if (usernameAndPasswordInfo != null && usernameAndPasswordInfo.getPassword().equals(password)) {

                return ResponseEntity.ok().body(new LoginResponse("0", usernameAndPasswordInfo.getId()));
            } else {
                // 用户名存在但密码错误
                return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse("1", null));
            }
        } else {
            // 用户名不存在
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse("2", null));
        }
    }

    @GetMapping("/withName")
    public Result<?> findAllWithName() {
        return Result.success(userInfoMapper.findAllUserWithName());
    }
}
