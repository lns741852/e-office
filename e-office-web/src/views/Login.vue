<template>
  <div>
    <el-form :model="ruleForm" ref="ruleForm" :rules="rules" class="loginContainer">
      <h3 class="loginTitle">登入</h3>
      <el-form-item prop="username">
        <el-input type="text" v-model="ruleForm.username" autocomplete="off" placeholder="請輸入帳號"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="ruleForm.password" autocomplete="off" placeholder="請輸入密碼"></el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-input size="normal" type="text" v-model="ruleForm.code"
                  auto-complete="false"
                  placeholder="更換驗證碼" style="width:250px;margin-right: 5px"></el-input>
        <img :src="captchaUrl" @click="updateCaptcha"/>
      </el-form-item>
      <!--<el-checkbox class="loginRemember" v-model="checked">記住我</el-checkbox>-->
      <el-button type="primary" style="width: 100%" @click="submitLogin">登入</el-button>
    </el-form>
  </div>
</template>

<script>
  import {postRequest} from "../utils/api";

  export default {
    name: "Login",
    data() {
      return {
        //圖片可以直接訪問get請求，加上時間防止暫存不更新
        captchaUrl: '/captcha?time=' + new Date(),
        ruleForm: {
          username: 'admin',
          password: '123',
          code: ''
        },
        checked: true,
        //對應標籤屬性prop
        rules: {
          username: [{
            required: true, message: '請輸入帳號', trigger:
              'blur'
          }],
          password: [{
            required: true, message: '請輸入密碼', trigger:
              'blur'
          }],
          code: [{
            required: true, message: '請輸入驗證碼', trigger:
              'blur'
          }]
        }
      }
    },
    methods: {
      //登入
      submitLogin() {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            postRequest('/login', this.ruleForm).then(resp => {
              if (resp) {
                //儲存token
                const tokenStr = resp.obj.tokenHead + resp.obj.token;
                window.sessionStorage.setItem('tokenStr', tokenStr);
                //清空菜單暫存
                this.$store.commit('initRoutes', []);
                //獲取url中redirect參數，登入失敗時會將redirect加入url
                let path = this.$route.query.redirect;
                //頁面跳轉
                this.$router.replace((path == '/' || path == undefined) ? '/home' : path)
              }
            })
          } else {
            this.$message.error('請輸入所有內容!');
            return false;
          }
        })
      },
      updateCaptcha() {
        //驗證碼
        this.captchaUrl = '/captcha?time=' + new Date();
      }
    }
  }
</script>

<style>
  .loginContainer {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 15px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
  .loginTitle {
    margin: 0 auto 40px auto;
    text-align: center;
    color: #505458;
  }

  /*.loginRemember {
    text-align: left;
    margin: 0px 0px 15px 0px;
  }*/

  .el-form-item__content {
    display: flex;
    align-items: center;
  }
</style>
