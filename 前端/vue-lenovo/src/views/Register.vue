<template>
  <div class="page-bg">
    <el-card class="auth-card" shadow="hover">
      <div class="card-head">
        <div class="logo">创建账号</div>
        <div class="subtitle">欢迎加入 Lenovo Shop</div>
      </div>

      <el-form :model="form" :rules="rules" ref="regRef" label-position="top">
        <el-form-item prop="username">
          <el-input placeholder="用户名" v-model="form.username"></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input placeholder="密码" show-password v-model="form.password"></el-input>
        </el-form-item>

        <el-form-item prop="confirm">
          <el-input placeholder="确认密码" show-password v-model="form.confirm"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" native-type="button" class="full-btn" @click="onRegister" :loading="loading">注 册</el-button>
        </el-form-item>

        <div class="small-links">
          已有账号？ <router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    return {
      loading: false,
      form: { username: '', password: '', confirm: '', role: 'USER' },
      rules: {
        username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请填写密码', trigger: 'blur' }],
        confirm: [{ validator: (rule, value, callback) => {
          if (value !== this.form.password) callback(new Error('两次密码不一致'));
          else callback();
        }, trigger: 'blur' }],
        role: [{ required: true, message: '请选择身份', trigger: 'change' }]
      }
    }
  },
  methods: {
    onRegister() {
      this.$refs.regRef.validate(valid => {
        if (!valid) return;
        this.loading = true;
        this.$request.post('/register', this.form).then(res => {
          this.loading = false;
          if (res.code === '200') {
            this.$message.success('注册成功，请登录');
            this.$router.push('/login');
          } else {
            this.$message.error(res.msg || '注册失败');
          }
        }).catch(() => { this.loading = false; });
      });
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
}

.auth-card {
  width: 420px;
  border-radius: 8px;
  padding: 26px;
  background: rgba(255,255,255,0.95);
}

.card-head { margin-bottom: 12px; }
.logo { font-size: 22px; font-weight:700; color:#2d4a6f; }
.subtitle { font-size: 13px; color:#666; margin-top:6px; }

.full-btn { width:100%; }
.small-links { margin-top:8px; text-align:right; font-size:13px; }
.small-links a { color:#2d4a6f; }
</style>