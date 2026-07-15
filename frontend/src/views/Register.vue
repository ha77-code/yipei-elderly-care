<template>
  <div class="register-page bg-warm-gradient">
    <div class="register-card">
      <div class="register-header">
        <h1 class="register-title">创建账号</h1>
        <p class="register-subtitle">注册益陪养老，开启安心陪护之旅</p>
      </div>

      <el-form
        ref="registerForm"
        :model="form"
        :rules="rules"
        class="register-form"
        size="large"
        @submit.native.prevent="handleRegister"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="username" label="用户名">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="phone" label="手机号">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号"
                prefix-icon="el-icon-phone"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="nickname" label="昵称">
          <el-input
            v-model="form.nickname"
            placeholder="请输入您的昵称"
            prefix-icon="el-icon-postcard"
            clearable
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="password" label="密码">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请设置密码（不少于6位）"
                prefix-icon="el-icon-lock"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirmPassword" label="确认密码">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="el-icon-lock"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="role" label="注册身份">
          <el-radio-group v-model="form.role" class="role-group">
            <el-radio-button label="CUSTOMER">
              <i class="el-icon-user"></i> 客户（找陪护）
            </el-radio-button>
            <el-radio-button label="COMPANION">
              <i class="el-icon-s-custom"></i> 陪诊师（提供服务）
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <button
            type="submit"
            class="btn-register"
            :disabled="loading"
            @click.prevent="handleRegister"
          >
            <span v-if="!loading">注 册</span>
            <i v-else class="el-icon-loading"></i>
          </button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="link-login">
          立即登录
          <i class="el-icon-arrow-right"></i>
        </router-link>
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
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px;
}

/* ===== 卡片容器 ===== */
.register-card {
  width: 680px;
  background: #FFFFFF;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: 48px 48px 40px;
}

/* ===== 头部 ===== */
.register-header {
  text-align: center;
  margin-bottom: 36px;
}

.register-title {
  font-family: var(--font-family);
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: var(--color-text-placeholder);
}

/* ===== 表单 ===== */
.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-form-item__label {
  font-weight: 500;
  color: var(--color-text-regular);
}

.register-form .el-input__inner {
  height: 44px;
  border-radius: var(--radius-sm);
  border-color: var(--color-border);
  font-size: 15px;
}

.register-form .el-input__inner:focus {
  border-color: var(--color-primary);
}

/* ===== 角色选择 ===== */
.role-group {
  width: 100%;
}

.role-group .el-radio-button {
  width: 50%;
}

.role-group .el-radio-button__inner {
  width: 100%;
  height: 44px;
  line-height: 44px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.role-group .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: rgba(122, 154, 126, 0.1);
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
  box-shadow: none;
}

/* ===== 注册按钮（黑色 CTA） ===== */
.btn-register {
  width: 100%;
  height: 48px;
  background: #1A1A1A;
  color: #FFFFFF;
  font-family: var(--font-family);
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.08em;
  margin-top: 8px;
}

.btn-register:hover:not(:disabled) {
  background: #333333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-register:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* ===== 底部链接 ===== */
.register-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.link-login {
  color: var(--color-primary-dark);
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.2s;
}

.link-login:hover {
  color: var(--color-primary);
}

.link-login i {
  font-size: 12px;
  margin-left: 2px;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .register-card {
    width: 100%;
    padding: 32px 24px;
  }
}
</style>
