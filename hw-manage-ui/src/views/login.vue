<template>
  <div class="login">
    <div class="main">
      <div class="title">
        <div></div>
        <p>山东省XXX天桥区分局平台(若依管理系统)</p>
      </div>
      <div class="main-content">
        <div class="login-left-image">

        </div>
        <div class="login-right">
          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
            <p>用户登陆</p>
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号">
                <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码"
                @keyup.enter.native="handleLogin">
                <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>
            <el-form-item prop="code" v-if="captchaEnabled">
              <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%"
                @keyup.enter.native="handleLogin">
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" />
              </div>
            </el-form-item>
            <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
            <el-form-item style="width:100%;">
              <el-button :loading="loading" size="medium" type="primary" style="width:100%;"
                @click.native.prevent="handleLogin">
                <span v-if="!loading">登 录</span>
                <span v-else>登 录 中...</span>
              </el-button>
              <div style="float: right;" v-if="register">
                <router-link class="link-type" :to="'/register'">立即注册</router-link>
              </div>
            </el-form-item>
          </el-form>
        </div>

      </div>



    </div>


    <!--  底部  -->
    <div class="el-login-footer">
      <span>版权所有：天津市xxxx分局</span>
      <span>建议浏览器：IE11及以上、360、谷歌、Firefox v22</span>
      <span>Copyright © 2018-2022 ruoyi.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
  import {
    getCodeImg
  } from "@/api/login";
  import Cookies from "js-cookie";
  import {
    encrypt,
    decrypt
  } from '@/utils/jsencrypt'

  export default {
    name: "Login",
    data() {
      return {
        codeUrl: "",
        loginForm: {
          username: "admin",
          password: "admin123",
          rememberMe: false,
          code: "",
          uuid: ""
        },
        loginRules: {
          username: [{
            required: true,
            trigger: "blur",
            message: "请输入您的账号"
          }],
          password: [{
            required: true,
            trigger: "blur",
            message: "请输入您的密码"
          }],
          code: [{
            required: true,
            trigger: "change",
            message: "请输入验证码"
          }]
        },
        loading: false,
        // 验证码开关
        captchaEnabled: true,
        // 注册开关
        register: false,
        redirect: undefined
      };
    },
    watch: {
      $route: {
        handler: function(route) {
          this.redirect = route.query && route.query.redirect;
        },
        immediate: true
      }
    },
    created() {
      this.getCode();
      this.getCookie();
    },
    methods: {
      getCode() {
        getCodeImg().then(res => {
          this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
          if (this.captchaEnabled) {
            this.codeUrl = "data:image/gif;base64," + res.img;
            this.loginForm.uuid = res.uuid;
          }
        });
      },
      getCookie() {
        const username = Cookies.get("username");
        const password = Cookies.get("password");
        const rememberMe = Cookies.get('rememberMe')
        this.loginForm = {
          username: username === undefined ? this.loginForm.username : username,
          password: password === undefined ? this.loginForm.password : decrypt(password),
          rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
        };
      },
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            this.loading = true;
            if (this.loginForm.rememberMe) {
              Cookies.set("username", this.loginForm.username, {
                expires: 30
              });
              Cookies.set("password", encrypt(this.loginForm.password), {
                expires: 30
              });
              Cookies.set('rememberMe', this.loginForm.rememberMe, {
                expires: 30
              });
            } else {
              Cookies.remove("username");
              Cookies.remove("password");
              Cookies.remove('rememberMe');
            }

            this.loginForm.password = encrypt(this.loginForm.password);
            this.$store.dispatch("Login", this.loginForm).then(() => {
              this.$router.push({
                path: this.redirect || "/"
              }).catch(() => {});
            }).catch(() => {
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
          }
        });
      }
    }
  };
</script>

<style rel="stylesheet/scss" lang="scss">
  .login {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    // background-image: url("../assets/images/login-background.jpg");
    // background-size: cover;
    background-color: #0064c8;
    min-width: 900px;

    .title {
      width: 100%;
      p{
        color: #fff;
        font-size: 40px;
        font-weight: 600;
        letter-spacing: 5px;
        padding-left: 10px;
      }
      display: flex;
      flex:1;
      text-align: center;
      align-items: center;
      div{
        background-image: url('../assets/images/jinghui.png');
        background-size: 100% 100%;
        width: 60px;
        height: 60px;
      }
    }


    .main {}

    .main-content {
      display: flex;
      flex-direction: row;
      height: 365px;
    }

    .login-left-image {
      padding: 0;
      height: 363px;
      background: url("../assets/images/login_left.png") no-repeat center;
      background-size: 100% 100%;
      display: flex;
      flex: 1;
      border-radius: 6px 0 0 6px;
    }
  }





  .login-form {
    border-radius: 0 6px 6px 0;
    background: #ffffff;
    width: 400px;
    height: 363px;
    padding: 25px 25px 5px 25px;

    p {
      color: #251E5F;
      font-size: 20px;
      font-weight: 700;
      text-align: center;
    }

    .el-input {
      height: 38px;

      input {
        height: 38px;
      }
    }

    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 2px;
    }
  }

  .login-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
  }

  .login-code {
    width: 33%;
    height: 38px;
    float: right;

    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }

  .el-login-footer {
    position: fixed;
    bottom: 10px;
    width: 100%;
    text-align: center;
    color: #fff;
    font-family: Arial;
    font-size: 12px;
    // letter-spacing: 1px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;

    spa {

      margin: 3px;
    }
  }

  .login-code-img {
    height: 38px;
  }
</style>
