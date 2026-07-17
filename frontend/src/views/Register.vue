<template>
  <div class="register-page">
    <div class="bg-decoration">
      <div class="bg-circle bg-circle--1"></div>
      <div class="bg-circle bg-circle--2"></div>
    </div>

    <div class="register-card">
      <div class="register-brand">
        <div class="brand-content">
          <div class="brand-icon">
            <svg viewBox="0 0 48 48" fill="none"><circle cx="24" cy="18" r="7" stroke="currentColor" stroke-width="2" fill="none"/><path d="M12 40c0-8 5.4-12 12-12s12 4 12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </div>
          <h1 class="brand-title">益陪养老</h1>
          <p class="brand-desc">用陪伴，守护每一段金色年华</p>
          <div class="brand-steps">
            <div class="step-item"><span class="step-num">1</span><span>填写信息创建账号</span></div>
            <div class="step-item"><span class="step-num">2</span><span>选择角色定位身份</span></div>
            <div class="step-item"><span class="step-num">3</span><span>即刻体验陪伴服务</span></div>
          </div>
        </div>
      </div>

      <div class="register-form-wrap">
        <div class="form-header">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">注册后即可发布需求或接单服务</p>
        </div>

        <el-form ref="registerForm" :model="form" :rules="rules" class="register-form" size="large" @submit.native.prevent="handleRegister">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="phone">
                <el-input v-model="form.phone" placeholder="手机号" prefix-icon="el-icon-phone" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="昵称" prefix-icon="el-icon-postcard" clearable />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="密码（不少于6位）" prefix-icon="el-icon-lock" show-password />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="el-icon-lock" show-password />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item prop="role">
            <div class="role-cards">
              <div :class="['role-card', { active: form.role === 'CUSTOMER' }]" @click="form.role = 'CUSTOMER'">
                <i class="el-icon-user"></i>
                <strong>客户</strong>
                <small>为家人找陪护服务</small>
              </div>
              <div :class="['role-card', { active: form.role === 'COMPANION' }]" @click="form.role = 'COMPANION'">
                <i class="el-icon-s-custom"></i>
                <strong>陪诊师</strong>
                <small>提供专业陪伴服务</small>
              </div>
            </div>
          </el-form-item>

          <el-form-item class="form-action">
            <button type="submit" class="btn-register" :disabled="loading" @click.prevent="handleRegister">
              <span v-if="!loading" class="btn-text">注 册</span>
              <i v-else class="el-icon-loading"></i>
            </button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link-login">立即登录 <svg class="link-arrow" viewBox="0 0 16 16" fill="none"><path d="M6 4l4 4-4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        username: '',
        phone: '',
        nickname: '',
        password: '',
        confirmPassword: '',
        role: 'CUSTOMER'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度在3~50个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { max: 100, message: '昵称不超过100个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请设置密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择注册身份', trigger: 'change' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async handleRegister() {
      try {
        await this.$refs.registerForm.validate()
      } catch {
        return
      }

      this.loading = true
      try {
        await register({
          username: this.form.username,
          password: this.form.password,
          phone: this.form.phone,
          nickname: this.form.nickname,
          role: this.form.role
        })
        this.$message.success('注册成功，请登录')
        this.$router.push('/login')
      } catch {
        // 错误已在 request.js 拦截器中统一处理
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register-page { display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 40px; background: linear-gradient(135deg, #F5F0E8 0%, #E8EDE6 30%, #EDF0E8 60%, #F2EEE6 100%); position: relative; overflow: hidden; }
.bg-decoration { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-circle { position: absolute; border-radius: 50%; }
.bg-circle--1 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(122,154,126,0.12), transparent 70%); top: -160px; right: -100px; }
.bg-circle--2 { width: 380px; height: 380px; background: radial-gradient(circle, rgba(196,168,130,0.1), transparent 70%); bottom: -100px; left: -60px; }

.register-card { display: flex; width: 960px; min-height: 580px; background: rgba(255,255,255,0.78); backdrop-filter: blur(28px) saturate(1.4); border-radius: 20px; border: 1px solid rgba(255,255,255,0.5); box-shadow: 0 2px 16px rgba(92,122,96,0.05), 0 8px 40px rgba(92,122,96,0.07), 0 24px 80px rgba(92,122,96,0.06); overflow: hidden; position: relative; z-index: 1; }

.register-brand { flex: 0 0 360px; background: linear-gradient(160deg, #6B8E6E 0%, #5A7A5D 30%, #4A6A50 100%); display: flex; align-items: center; justify-content: center; padding: 48px 36px; position: relative; overflow: hidden; }
.brand-content { color: #fff; position: relative; z-index: 1; text-align: center; }
.brand-icon { width: 56px; height: 56px; margin: 0 auto 24px; color: rgba(255,255,255,0.9); background: rgba(255,255,255,0.1); border-radius: 16px; padding: 10px; border: 1px solid rgba(255,255,255,0.12); }
.brand-icon svg { width: 100%; height: 100%; }
.brand-title { font-family: "PingFang SC", "Microsoft YaHei", sans-serif; font-size: 32px; font-weight: 700; letter-spacing: 0.06em; margin-bottom: 8px; }
.brand-desc { font-size: 14px; opacity: 0.75; margin-bottom: 40px; }
.brand-steps { text-align: left; display: flex; flex-direction: column; gap: 16px; }
.step-item { display: flex; align-items: center; gap: 12px; font-size: 14px; opacity: 0.85; }
.step-num { width: 26px; height: 26px; display: flex; align-items: center; justify-content: center; border-radius: 50%; background: rgba(255,255,255,0.15); font-size: 13px; font-weight: 700; flex-shrink: 0; }

.register-form-wrap { flex: 1; padding: 44px 44px; display: flex; flex-direction: column; justify-content: center; }
.form-header { margin-bottom: 28px; }
.form-title { font-size: 26px; font-weight: 700; color: #1A1A1A; margin: 0 0 6px; }
.form-subtitle { font-size: 14px; color: #999; margin: 0; }

.register-form .el-form-item { margin-bottom: 18px; }
.register-form ::v-deep .el-input__inner { height: 46px; border-radius: 12px; border: 1.5px solid rgba(0,0,0,0.08); background: rgba(0,0,0,0.015); font-size: 14px; padding-left: 42px; transition: all 0.3s ease; }
.register-form ::v-deep .el-input__inner:focus { border-color: #7A9A7E; background: #fff; box-shadow: 0 0 0 4px rgba(122,154,126,0.08); }
.register-form ::v-deep .el-input__prefix { left: 14px; color: #B0B0B0; }

.role-cards { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.role-card { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 18px 12px; border: 2px solid rgba(0,0,0,0.06); border-radius: 14px; cursor: pointer; transition: all 0.25s ease; background: rgba(0,0,0,0.01); }
.role-card i { font-size: 24px; color: #999; transition: color 0.25s; }
.role-card strong { font-size: 14px; color: #333; }
.role-card small { font-size: 12px; color: #999; }
.role-card:hover { border-color: rgba(122,154,126,0.3); background: rgba(122,154,126,0.03); }
.role-card.active { border-color: #7A9A7E; background: rgba(122,154,126,0.06); }
.role-card.active i { color: #5A7A5D; }

.form-action { margin-top: 4px !important; margin-bottom: 0 !important; }
.btn-register { width: 100%; height: 48px; background: #1C1C1C; color: #fff; font-size: 16px; font-weight: 600; border: none; border-radius: 12px; cursor: pointer; letter-spacing: 0.12em; transition: all 0.3s ease; position: relative; overflow: hidden; }
.btn-register::before { content: ''; position: absolute; inset: 0; background: linear-gradient(135deg, #6B8E6E, #5A7A5D); opacity: 0; transition: opacity 0.4s; }
.btn-register:hover:not(:disabled)::before { opacity: 1; }
.btn-register:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(107,142,110,0.3); }
.btn-register:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-text { position: relative; z-index: 1; }

.register-footer { margin-top: 24px; text-align: center; font-size: 14px; color: #999; display: flex; align-items: center; justify-content: center; gap: 4px; }
.link-login { display: inline-flex; align-items: center; gap: 2px; color: #5A7A5D; font-weight: 600; text-decoration: none; }
.link-login:hover { color: #4A6A50; }
.link-arrow { width: 14px; height: 14px; }

@media (max-width: 960px) { .register-card { flex-direction: column; width: 100%; max-width: 440px; } .register-brand { flex: none; padding: 36px 28px; } .brand-title { font-size: 26px; } .brand-steps { display: none; } .register-form-wrap { padding: 32px 28px; } }
</style>
