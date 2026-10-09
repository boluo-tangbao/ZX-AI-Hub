package com.itheima.ai.controller;

import com.itheima.ai.pojo.LoginAndRegisterRequest;
import com.itheima.ai.pojo.VerifyCodeCaptcha;
import com.itheima.ai.service.CaptchaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    /**
     * 生成验证码
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 获取验证码
     */
    @GetMapping("/verifycode")
    public VerifyCodeCaptcha generateVerifyCode() throws IOException {
        return captchaService.generateVerifyCode();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginAndRegisterRequest param, Integer condition) {
        return captchaService.login(param,condition);
    }

}

