<template>
  <div :style="bg" class="login-page">
    <div class="login-card">
      <div class="login-header">AI预习系统</div>

      <!-- 原生表单，@submit.prevent 防止回车刷新页面 -->
      <form @submit.prevent="login">

        <!-- 用户名输入框 -->
        <div class="form-item">
          <div class="input-wrapper">
            <span class="input-prefix">👤</span>
            <input
                type="text"
                v-model="form.username"
                placeholder="请输入用户名"
                class="custom-input"
                required
            />
          </div>
        </div>

        <!-- 密码输入框 -->
        <div class="form-item">
          <div class="input-wrapper">
            <span class="input-prefix">🔒</span>
            <input
                :type="showPassword ? 'text' : 'password'"
                v-model="form.password"
                placeholder="请输入密码"
                class="custom-input"
                required
            />
            <!-- 简单的显隐切换图标 -->
            <span class="input-suffix" @click="showPassword = !showPassword">
              {{ showPassword ? '🙈' : '👁️' }}
            </span>
          </div>
        </div>

        <!-- 验证码输入框 -->
        <div class="form-item captcha-row">
          <div class="input-wrapper captcha-input-wrapper">
            <input
                type="text"
                v-model="form.verifyCode"
                placeholder="请输入验证码"
                class="custom-input"
                required
            />
          </div>
          <img :src="captchaImage" @click="refreshCaptcha" class="captcha-img"/>
        </div>

        <!-- 登录按钮 -->
        <button type="button" class="login-btn" @click="login">登 录</button>
      </form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "Login",
  data() {
    return {
      showPassword: false, // 用于控制密码显隐
      bg: {
        backgroundImage: "url(" + require("../assets/classroom.jpg") + ")",
        backgroundRepeat: 'no-repeat',
        backgroundSize: "cover",
        minHeight: "100vh",
        position: "relative",
        backgroundPosition: "center center",
      },
      form: {
        username: "",
        password: "",
        verifyCode: "",
        verifyCodeKey: "",
      },
      captchaImage: "",
    }
  },
  mounted() {
    this.refreshCaptcha();
  },
  methods: {
    login() {
      // 原生表单验证逻辑
      if (!this.form.username) {
        alert("请输入用户名"); // 或者使用 this.$message，如果你全局引入了
        return;
      }
      if (!this.form.password) {
        alert("请输入密码");
        return;
      }

      // 原有的登录逻辑保持不变
      request.post("/captcha/login", {
        username: this.form.username,
        password: this.form.password,
        verifyCodeKey: this.form.verifyCodeKey,
        verifyCode: this.form.verifyCode,
      }).then(() => {
        console.log("验证码正确，开始注册");
        request.post("/user/login", {
          username: this.form.username,
          password: this.form.password
        }).then(res => {
          console.log(res.code);
          if (res.code === '0') {
            this.$message({
              type: "success",
              message: "登录成功"
            });
            // 存储登录信息到sessionStorage
            sessionStorage.setItem("currentId", res.id);
            console.log(res.id);
            // 根据返回的id值继续登录
            if (res.id >= 10000 && res.id <= 99999) { // 老师登录
              sessionStorage.setItem("currentDep", "Doctor");
              sessionStorage.setItem("currentUsername",this.form.username);
              sessionStorage.setItem("currentName",this.form.username+"医生");
              this.$router.push("/doctor");
            } else if (res.id>=1000000&&res.id<=9999999) { // 学生登录
              sessionStorage.setItem("currentPatientID", res.id);
              sessionStorage.setItem("currentDep", "Patient");
              sessionStorage.setItem("currentUsername",this.form.username);
              sessionStorage.setItem("currentName","患者"+this.form.username);
              this.$router.push("/patient");
            } else {
              if (res.id >= 100 && res.id <= 999) { //管理员登录
                sessionStorage.setItem("currentDep", "Admin");
                sessionStorage.setItem("currentUsername",this.form.username);
                sessionStorage.setItem("currentName",this.form.username+"管理员");
                this.$router.push("/admin");
              } // 登录成功之后进行页面跳转
              else {
                this.$message({
                  type: "error",
                  message: "用户名不合法"
                });
              }
            }
          } else if (res.code === '1') {
            this.$message({
              type: "error",
              message: "密码输入错误"
            });
          } else if (res.code === '2') {
            this.$message({
              type: "error",
              message: "用户名不存在，请注册！"
            });
            this.$router.push("/register");
          } else {
            this.$message({
              type: "error",
              message: "未知错误，请重试"
            });
          }
        }).catch(error => {
          console.error('Error:', error);
          this.$message({
            type: "error",
            message: "登录失败，请稍后再试"
          });
        });
      }).catch(error => {
        console.error('Error:', error);
        this.$message({
          type: "error",
          message: "验证码错误，请重新输入"
        });
      });

      this.refreshCaptcha();
    },
    refreshCaptcha() {
      this.generateCaptcha();
    },
    generateCaptcha() {
      request
          .get("/captcha/verifycode")
          .then((response) => {
            console.log(response.image);
            this.form.verifyCodeKey = response.key;
            this.form.verifyCode = ""; // 清空之前的验证码

            // 检查response.image是否以"data:"开头
            this.captchaImage = response.image.startsWith("data:")
                ? response.image
                : `https://${response.image}`;
          })
          .catch((error) => {
            console.error("Error:", error);
            this.$message.error("获取验证码失败，请稍后重试");
          });
    },
  }
}
</script>

<style scoped>
/* 页面容器 */
.login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

/* 登录卡片主体 */
.login-card {
  background-color: rgba(255, 255, 255, 0.9);
  width: 30%;
  /* 高度设为 auto 以适应内容，或者固定高度 */
  min-height: 350px;
  border-radius: 30px;
  /* 设置box-shadow使其有立体感 */
  box-shadow: 5px 5px 0 0 rgba(0, 0, 0, 1);
  padding: 0 50px;
  box-sizing: border-box;
  overflow: hidden;
}

/* 标题样式 */
.login-header {
  color: black;
  font-size: 30px;
  text-align: center;
  padding: 40px 0;
  font-weight: bold;
}

/* 表单项间距 */
.form-item {
  margin-bottom: 20px;
}

/* 输入框外层容器 */
.input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 0 10px;
  background-color: #fff;
  transition: border-color 0.2s;
}

.input-wrapper:focus-within {
  border-color: #409eff;
}

/* 输入框前缀图标 */
.input-prefix {
  color: #c0c4cc;
  font-size: 18px;
  margin-right: 10px;
}

/* 输入框后缀（密码显隐） */
.input-suffix {
  cursor: pointer;
  font-size: 18px;
  color: #c0c4cc;
  margin-left: 10px;
}

/* 原生输入框样式重置 */
.custom-input {
  flex: 1;
  border: none;
  outline: none;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  color: #606266;
  background: transparent;
}

.custom-input::placeholder {
  color: #c0c4cc;
}

/* 验证码行布局 */
.captcha-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.captcha-input-wrapper {
  flex: 1;
  margin-right: 10px;
}

/* 验证码图片 */
.captcha-img {
  height: 40px;
  width: 100px;
  cursor: pointer;
  border-radius: 4px;
  background-color: #f0f0f0;
  border: 1px solid #dcdfe6;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  color: white;
  background-color: #409eff;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  outline: none;
}

.login-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.login-btn:active {
  background-color: #3a8ee6;
  border-color: #3a8ee6;
}
</style>