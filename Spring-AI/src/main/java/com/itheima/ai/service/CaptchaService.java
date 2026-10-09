package com.itheima.ai.service;

import com.itheima.ai.pojo.LoginAndRegisterRequest;
import com.itheima.ai.pojo.VerifyCodeCaptcha;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    public VerifyCodeCaptcha generateVerifyCode() throws IOException {
        // 创建验证码对象
        Captcha captcha = new ArithmeticCaptcha();
        // 生成验证码编号
        String verifyCodeKey = UUID.randomUUID().toString();
        String verifyCode = captcha.text();
        // 获取验证码图片，构造响应结果
        VerifyCodeCaptcha verifyCodeEntity = new VerifyCodeCaptcha(verifyCodeKey, captcha.toBase64(), verifyCode);

        return verifyCodeEntity;
    }

    public ResponseEntity<?> login(LoginAndRegisterRequest param, Integer condition) {
        try {
            // 校验验证码
            // 获取用户输入的验证码
            String actual = param.getVerifyCode();
            // 比较用户输入的验证码和缓存中的验证码是否一致，不一致则抛错
            if (!StringUtils.hasText(expect) || !StringUtils.hasText(actual) || !actual.equalsIgnoreCase(expect)) {
                System.out.println("验证码错误");
                throw new RuntimeException("验证码错误");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 如果发生异常，返回错误的 ResponseEntity 对象
            System.out.println("出现错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}


