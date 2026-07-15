<template>
  <div class="login-page bg-warm-gradient">
    <div class="login-card">
      <!-- 左侧品牌区 -->
      <div class="login-brand">
        <div class="brand-content">
          <h1 class="brand-title">益陪养老</h1>
          <p class="brand-desc">专业养老陪护服务平台</p>
          <div class="brand-features">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>持证护工，安心陪护</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>24小时全天候服务</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>正规代理资质保障</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrap">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">登录您的账号以继续</p>

        <el-form
          ref="loginForm"
          :model="form"
          :rules="rules"
          class="login-form"
          size="large"
          @submit.native.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              show-password
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <button
              type="submit"
              class="btn-login"
              :disabled="loading"
              @click.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <i v-else class="el-icon-loading"></i>
            </button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link-register">
            立即注册
            <i class="el-icon-arrow-right"></i>
          </router-link>
        </div>

        <!-- 开发环境：快速测试入口 -->
        <div class="dev-quick-login">
          <div class="dev-divider"><span>快速体验（开发测试）</span></div>
          <div class="dev-accounts">
            <div
              v-for="acc in testAccounts"
              :key="acc.role"
              class="dev-account-card"
              @click="quickLogin(acc.role)"
            >
              <div class="dev-acc-avatar">
                <i class="el-icon-user"></i>
              </div>
              <div class="dev-acc-info">
                <span class="dev-acc-name">{{ acc.label }}</span>
                <span class="dev-acc-role">{{ acc.roleName }}</span>
              </div>
              <span class="dev-acc-hint">点击登录</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'
import { setUser, setToken } from '@/utils/auth'
import { quickLogin, getTestAccounts } from '@/utils/testAccounts'

export default {
  name: 'Login',
  data() {
    return {
      testAccounts: getTestAccounts(),
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    quickLogin,
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
      } catch {
        return
      }

      this.loading = true
      try {
        const res = await login({
          username: this.form.username,
          password: this.form.password
        })
        // 保存登录状态
        setToken(res.data?.token || '')
        setUser(res.data?.user || res.data)

        this.$message.success('登录成功')

        // 跳转到 redirect 参数指定的页面，或首页
        const redirect = this.$route.query.redirect || '/'
        this.$router.replace(redirect)
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
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px;
}

/* ===== 卡片容器 ===== */
.login-card {
  display: flex;
  width: 900px;
  min-height: 520px;
  background: #FFFFFF;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

/* ===== 左侧品牌区 ===== */
.login-brand {
  flex: 1;
  background: radial-gradient(ellipse at 30% 20%, #7A9A7E 0%, #5C7A60 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
}

.brand-content {
  color: #FFFFFF;
}

.brand-title {
  font-family: var(--font-family);
  font-size: 36px;
  font-weight: 800;
  letter-spacing: 0.04em;
  margin-bottom: 8px;
}

.brand-desc {
  font-size: 15px;
  font-weight: 300;
  opacity: 0.85;
  margin-bottom: 40px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 400;
  opacity: 0.9;
}

.feature-item i {
  font-size: 16px;
  color: #C4E0C8;
}

/* ===== 右侧表单 ===== */
.login-form-wrap {
  flex: 1;
  padding: 60px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-title {
  font-family: var(--font-family);
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.form-subtitle {
  font-size: 14px;
  color: var(--color-text-placeholder);
  margin-bottom: 36px;
}

.login-form {
  width: 100%;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input__inner {
  height: 48px;
  border-radius: var(--radius-sm);
  border-color: var(--color-border);
  font-size: 15px;
}

.login-form .el-input__inner:focus {
  border-color: var(--color-primary);
}

/* ===== 登录按钮（黑色 CTA） ===== */
.btn-login {
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
}

.btn-login:hover:not(:disabled) {
  background: #333333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* ===== 底部链接 ===== */
.login-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.link-register {
  color: var(--color-primary-dark);
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.2s;
}

.link-register:hover {
  color: var(--color-primary);
}

.link-register i {
  font-size: 12px;
  margin-left: 2px;
}

/* ===== 开发环境快速测试 ===== */
.dev-quick-login {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px dashed var(--color-border);
}

.dev-divider {
  text-align: center;
  margin-bottom: 16px;
}

.dev-divider span {
  font-size: 12px;
  color: var(--color-text-placeholder);
  background: #FFFFFF;
  padding: 0 12px;
}

.dev-accounts {
  display: flex;
  gap: 10px;
}

.dev-account-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: var(--color-bg-page);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
}

.dev-account-card:hover {
  border-color: var(--color-primary);
  background: rgba(122, 154, 126, 0.04);
}

.dev-acc-avatar {
  width: 32px;
  height: 32px;
  background: var(--color-primary-light);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 14px;
  flex-shrink: 0;
}

.dev-acc-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  flex: 1;
}

.dev-acc-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.dev-acc-role {
  font-size: 11px;
  color: var(--color-text-placeholder);
}

.dev-acc-hint {
  font-size: 11px;
  color: var(--color-primary);
  font-weight: 500;
  flex-shrink: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
  }
  .login-brand {
    padding: 40px 32px;
  }
  .login-form-wrap {
    padding: 40px 32px;
  }
}
</style>
