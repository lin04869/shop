<template>
  <div class="page-bg">
    <el-card class="auth-card" shadow="hover">
      <div class="card-head">
        <div class="logo">Lenovo Shop</div>
        <div class="subtitle">账号登录入口</div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="login-form" label-position="top">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="用户名" v-model="form.username"></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="密码" show-password v-model="form.password"></el-input>
        </el-form-item>

        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="请选择身份">
            <el-option label="商家管理员" value="BUSINESS"></el-option>
            <el-option label="普通用户" value="USER"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item class="action-row">
          <el-button type="primary" native-type="button" class="full-btn" @click="onLogin" :loading="loading">登 录</el-button>
        </el-form-item>

        <div class="small-links">
          <router-link to="/register">注册账号</router-link>
          <a @click.prevent="openForgot" style="margin-left:12px; cursor:pointer;">忘记密码</a>
        </div>
      </el-form>
    </el-card>

    <el-dialog title="找回密码" :visible.sync="showForgot" width="460px">
      <div v-if="forgotStep === 'request'">
        <el-form :model="forgotForm" ref="forgotRef" label-position="top">
          <el-form-item prop="email">
            <el-input v-model="forgotForm.email" placeholder="输入注册邮箱"></el-input>
          </el-form-item>
        </el-form>
        <div style="text-align:right;">
          <el-button @click="showForgot=false">取消</el-button>
          <el-button type="primary" :loading="forgotLoading" @click="requestReset">发送验证码</el-button>
        </div>
      </div>

      <div v-else>
        <el-form :model="forgotFormVerify" ref="verifyRef" label-position="top">
          <el-form-item prop="code">
            <el-input v-model="forgotFormVerify.code" placeholder="输入邮箱收到的验证码"></el-input>
          </el-form-item>
          <el-form-item prop="newPassword">
            <el-input v-model="forgotFormVerify.newPassword" placeholder="新密码" show-password></el-input>
          </el-form-item>
          <el-form-item prop="confirm">
            <el-input v-model="forgotFormVerify.confirm" placeholder="确认新密码" show-password></el-input>
          </el-form-item>
        </el-form>
        <div style="text-align:right;">
          <el-button @click="forgotStep='request'">返回</el-button>
          <el-button type="primary" :loading="forgotLoading" @click="submitReset">重置密码</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loading: false,
      form: { username: '', password: '', role: '' },
      rules: {
        username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请填写密码', trigger: 'blur' }],
        role: [{ required: true, message: '请选择用户身份', trigger: 'change' }]
      },

      // 找回密码
      showForgot: false,
      forgotStep: 'request',
      forgotLoading: false,
      forgotForm: { email: '' },
      forgotFormVerify: { code: '', newPassword: '', confirm: '' }
    }
  },
  methods: {
    onLogin() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return;
        this.loading = true;
        this.$request.post('/auth/login', this.form).then(res => {
          this.loading = false;
          if (res.code === '200') {
            const user = res.data;
            localStorage.setItem("xm-user", JSON.stringify(user));
            this.$message.success('登录成功');
            if (user.role === 'USER') this.$router.push('/front/home'); else this.$router.push('/home');
          } else {
            this.$message.error(res.msg || '登录失败');
          }
        }).catch(() => { this.loading = false; });
      });
    },

    // 打开找回密码弹窗
    openForgot() {
      this.showForgot = true;
      this.forgotStep = 'request';
      this.forgotForm.email = '';
      this.forgotFormVerify = { code: '', newPassword: '', confirm: '' };
    },

    // 请求后端发送验证码
    requestReset() {
      if (!this.forgotForm.email) { this.$message.warning('请填写邮箱'); return; }
      this.forgotLoading = true;
      
      this.$request.post('/auth/sendEmailCode?email=' + this.forgotForm.email)
        .then(res => {
          this.forgotLoading = false;
          if (res.code === '200') {
            this.$message.success('验证码已发送，请查看邮箱');
            this.forgotStep = 'verify';
          } else {
            this.$message.error(res.msg || '发送失败');
          }
        }).catch(()=>{ this.forgotLoading = false; });
    },

    // 提交 验证码 + 新密码 去后端重置
    submitReset() {
      const p = this.forgotFormVerify;
      if (!p.code || !p.newPassword) { this.$message.warning('请填写完整信息'); return; }
      if (p.newPassword !== p.confirm) { this.$message.warning('两次密码不一致'); return; }
      
      this.forgotLoading = true;
      const payload = {
        email: this.forgotForm.email,
        code: p.code,
        newPassword: p.newPassword
      };
      
      this.$request.post('/auth/resetPassword', payload)
        .then(res => {
          this.forgotLoading = false;
          if (res.code === '200') {
            this.$message.success('重置成功，请使用新密码登录');
            this.showForgot = false;
            this.forgotStep = 'request';
            this.forgotForm = { email: '' };
            this.forgotFormVerify = { code: '', newPassword: '', confirm: '' };
          } else {
            this.$message.error(res.msg || '重置失败');
          }
        }).catch(()=>{ this.forgotLoading = false; });
    }
  }
}
</script>

<style scoped>
.page-bg {
  height: 100vh;
  background-image: url("@/assets/imgs/back.jpg");
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 7.5%;
  color: #444;
}

.auth-card { width: 420px; border-radius: 8px; padding: 26px; background: rgba(255,255,255,0.95); }
.card-head { margin-bottom: 12px; }
.logo { font-size: 22px; font-weight: 700; color: #2d4a6f; }
.subtitle { font-size: 14px; color: #666; margin-top: 6px; }
.login-form .full-btn { width: 100%; }
.small-links { display:flex; justify-content:flex-end; margin-top:8px; font-size:13px; }
.small-links a { color:#2d4a6f; cursor: pointer; }
</style>