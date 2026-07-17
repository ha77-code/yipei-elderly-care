<template>
  <div
    class="login-page"
    @mousemove="onMouseMove"
    @mouseleave="onMouseLeave"
    ref="page"
  >
    <!-- 背景装饰（随鼠标微微移动） -->
    <div class="bg-decoration" :style="bgParallaxStyle">
      <div class="bg-circle bg-circle--1"></div>
      <div class="bg-circle bg-circle--2"></div>
      <div class="bg-circle bg-circle--3"></div>
    </div>

    <!-- 光标跟随光斑 -->
    <div class="cursor-glow" :style="cursorGlowStyle"></div>

    <div class="login-card" :style="cardTransformStyle">
      <!-- 卡片液态玻璃光泽层 -->
      <div class="card-sheen" :style="sheenStyle"></div>
      <div class="card-border-glow"></div>

      <!-- 左侧品牌区 -->
      <div class="login-brand">
        <!-- 液态光晕层 -->
        <div class="brand-liquid">
          <div class="brand-liquid-blob brand-liquid-blob--1" :style="liquidBlob1Style"></div>
          <div class="brand-liquid-blob brand-liquid-blob--2" :style="liquidBlob2Style"></div>
        </div>
        <!-- 装饰图案 -->
        <div class="brand-pattern" :style="brandPatternStyle">
          <div class="pattern-circle pattern-circle--lg"></div>
          <div class="pattern-circle pattern-circle--md"></div>
          <div class="pattern-circle pattern-circle--sm"></div>
          <div class="pattern-arc"></div>
        </div>
        <div class="brand-content">
          <div class="brand-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="24" cy="18" r="7" stroke="currentColor" stroke-width="2" fill="none"/>
              <path d="M12 40c0-8 5.4-12 12-12s12 4 12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="20" cy="16" r="1.5" fill="currentColor"/>
              <circle cx="28" cy="16" r="1.5" fill="currentColor"/>
              <path d="M19 21c1.5 1.5 3.5 2 5 2s3.5-.5 5-2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <h1 class="brand-title">益陪养老</h1>
          <p class="brand-desc">用陪伴，守护每一段金色年华</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>持证陪诊师，身份可查</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>全程陪同，安心无忧</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>服务可追溯，评价透明</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrap">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账号，继续守护家人</p>
        </div>

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
              placeholder="用户名"
              prefix-icon="el-icon-user"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="el-icon-lock"
              show-password
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>

          <el-form-item class="form-action">
            <button
              type="submit"
              class="btn-login"
              :disabled="loading"
              @click.prevent="handleLogin"
            >
              <span class="btn-shimmer"></span>
              <span v-if="!loading" class="btn-text">登 录</span>
              <i v-else class="el-icon-loading"></i>
            </button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <router-link to="/register" class="link-register">
            立即注册
            <svg class="link-arrow" viewBox="0 0 16 16" fill="none">
              <path d="M6 4l4 4-4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </router-link>
        </div>

        <!-- 开发环境：快速测试入口 -->
        <div class="dev-quick-login">
          <div class="dev-divider">
            <span class="dev-divider-text">快速体验</span>
          </div>
          <div class="dev-accounts">
            <div
              v-for="acc in testAccounts"
              :key="acc.role"
              class="dev-account-card"
              @click="quickLogin(acc.role)"
            >
              <div class="dev-acc-avatar" :class="`dev-acc-avatar--${acc.role.toLowerCase()}`">
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
import { setUser } from '@/utils/auth'
import { quickLogin, getTestAccounts } from '@/utils/testAccounts'

export default {
  name: 'Login',
  data() {
    return {
      testAccounts: getTestAccounts(),
      form: { username: '', password: '' },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ]
      },
      loading: false,
      /* 光标追踪 */
      mouseX: 0.5,
      mouseY: 0.5
    }
  },
  computed: {
    /* 光标跟随光斑 */
    cursorGlowStyle() {
      return {
        left: `${this.mouseX * 100}%`,
        top: `${this.mouseY * 100}%`
      }
    },
    /* 卡片 3D 微倾斜 */
    cardTransformStyle() {
      const rx = (this.mouseY - 0.5) * 2
      const ry = (this.mouseX - 0.5) * -2
      return {
        transform: `perspective(1200px) rotateX(${rx}deg) rotateY(${ry}deg)`
      }
    },
    /* 卡片光泽层位置 */
    sheenStyle() {
      return {
        background: `radial-gradient(
          circle at ${this.mouseX * 100}% ${this.mouseY * 100}%,
          rgba(255,255,255,0.25) 0%,
          rgba(255,255,255,0.06) 30%,
          transparent 60%
        )`
      }
    },
    /* 背景装饰微视差 */
    bgParallaxStyle() {
      const tx = (this.mouseX - 0.5) * 16
      const ty = (this.mouseY - 0.5) * 16
      return { transform: `translate(${tx}px, ${ty}px)` }
    },
    /* 品牌区液态光斑 */
    liquidBlob1Style() {
      const x = 40 + (this.mouseX - 0.5) * 30
      const y = 30 + (this.mouseY - 0.5) * 25
      return { left: `${x}%`, top: `${y}%` }
    },
    liquidBlob2Style() {
      const x = 65 + (this.mouseX - 0.5) * -20
      const y = 60 + (this.mouseY - 0.5) * -20
      return { left: `${x}%`, top: `${y}%` }
    },
    /* 品牌区装饰微动 */
    brandPatternStyle() {
      const tx = (this.mouseY - 0.5) * -6
      const ty = (this.mouseX - 0.5) * -6
      return { transform: `translate(${tx}px, ${ty}px)` }
    }
  },
  mounted() {
    this.onMouseMove = this.onMouseMove.bind(this)
  },
  methods: {
    quickLogin,
    onMouseMove(e) {
      const rect = this.$refs.page.getBoundingClientRect()
      this.mouseX = (e.clientX - rect.left) / rect.width
      this.mouseY = (e.clientY - rect.top) / rect.height
    },
    onMouseLeave() {
      this.mouseX = 0.5
      this.mouseY = 0.5
    },
    async handleLogin() {
      try { await this.$refs.loginForm.validate() } catch { return }
      this.loading = true
      try {
        const res = await login({ username: this.form.username, password: this.form.password })
        setUser(res.data)
        this.$message.success('登录成功')
        const redirect = this.$route.query.redirect || '/'
        this.$router.replace(redirect)
      } catch { /* 错误已在拦截器统一处理 */ } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
/* ===== 页面容器 ===== */
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px;
  background: linear-gradient(135deg, #F5F0E8 0%, #E8EDE6 30%, #EDF0E8 60%, #F2EEE6 100%);
  position: relative;
  overflow: hidden;
}

/* ===== 光标跟随光斑 ===== */
.cursor-glow {
  position: fixed;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(122,154,126,0.07) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 0;
  transition: opacity 0.6s ease;
}

/* ===== 背景装饰 ===== */
.bg-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
}

.bg-circle--1 {
  width: 640px; height: 640px;
  background: radial-gradient(circle at 40% 40%, rgba(122,154,126,0.14), rgba(122,154,126,0.04));
  top: -200px; right: -200px;
}

.bg-circle--2 {
  width: 400px; height: 400px;
  background: radial-gradient(circle at 60% 60%, rgba(196,168,130,0.12), rgba(196,168,130,0.02));
  bottom: -120px; left: -80px;
}

.bg-circle--3 {
  width: 340px; height: 340px;
  background: radial-gradient(circle at 50% 50%, rgba(143,175,148,0.08), transparent);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
}

/* ===== 卡片容器 ===== */
.login-card {
  display: flex;
  width: 960px;
  min-height: 560px;
  background: rgba(255,255,255,0.78);
  backdrop-filter: blur(28px) saturate(1.4);
  -webkit-backdrop-filter: blur(28px) saturate(1.4);
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.5);
  box-shadow:
    0 1px 0 rgba(255,255,255,0.5) inset,
    0 2px 16px rgba(92,122,96,0.05),
    0 8px 40px rgba(92,122,96,0.07),
    0 24px 80px rgba(92,122,96,0.06);
  overflow: hidden;
  position: relative;
  z-index: 1;
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  will-change: transform;
}

/* 液态玻璃光泽层 */
.card-sheen {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  border-radius: 20px;
}

/* 边框彩虹光泽 */
.card-border-glow {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  pointer-events: none;
  z-index: 0;
  background: linear-gradient(
    135deg,
    rgba(122,154,126,0.2) 0%,
    rgba(255,255,255,0.4) 30%,
    rgba(196,168,130,0.15) 50%,
    rgba(255,255,255,0.4) 70%,
    rgba(122,154,126,0.2) 100%
  );
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
}

/* ===== 左侧品牌区 ===== */
.login-brand {
  flex: 0 0 440px;
  background: linear-gradient(160deg, #6B8E6E 0%, #5A7A5D 30%, #4A6A50 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
  position: relative;
  overflow: hidden;
}

/* 液态光晕 */
.brand-liquid {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.brand-liquid-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  transition: left 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94),
              top 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.brand-liquid-blob--1 {
  width: 200px; height: 200px;
  background: rgba(174,199,150,0.35);
}

.brand-liquid-blob--2 {
  width: 160px; height: 160px;
  background: rgba(196,168,130,0.25);
}

/* 品牌区装饰图案 */
.brand-pattern {
  position: absolute;
  inset: 0;
  pointer-events: none;
  transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.pattern-circle {
  position: absolute;
  border-radius: 50%;
}

.pattern-circle--lg {
  width: 360px; height: 360px;
  border: 1px solid rgba(255,255,255,0.06);
  top: -80px; right: -120px;
}

.pattern-circle--md {
  width: 240px; height: 240px;
  border: 1px solid rgba(255,255,255,0.08);
  bottom: -60px; left: -40px;
}

.pattern-circle--sm {
  width: 80px; height: 80px;
  border: 1px solid rgba(255,255,255,0.1);
  top: 40%; right: 40px;
}

.pattern-arc {
  position: absolute;
  width: 200px; height: 100px;
  border: 1px solid rgba(255,255,255,0.05);
  border-radius: 100px 100px 0 0;
  border-bottom: none;
  bottom: 120px; left: 50%;
  transform: translateX(-50%) rotate(-10deg);
}

.brand-content {
  color: #FFFFFF;
  position: relative;
  z-index: 1;
  text-align: center;
}

.brand-icon {
  width: 64px; height: 64px;
  margin: 0 auto 28px;
  color: rgba(255,255,255,0.9);
  background: rgba(255,255,255,0.1);
  border-radius: 18px;
  padding: 12px;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.12);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

.brand-icon svg {
  width: 100%; height: 100%;
}

.brand-title {
  font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  font-size: 38px;
  font-weight: 700;
  letter-spacing: 0.06em;
  margin-bottom: 10px;
  line-height: 1.2;
}

.brand-desc {
  font-size: 15px;
  font-weight: 300;
  opacity: 0.75;
  margin-bottom: 44px;
  letter-spacing: 0.04em;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  text-align: left;
  max-width: 260px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 400;
  opacity: 0.82;
  letter-spacing: 0.02em;
}

.feature-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  flex-shrink: 0;
}

/* ===== 右侧表单 ===== */
.login-form-wrap {
  flex: 1;
  padding: 56px 52px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.form-header {
  margin-bottom: 36px;
}

.form-title {
  font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1A1A1A;
  margin: 0 0 6px;
  letter-spacing: 0.02em;
}

.form-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
  letter-spacing: 0.02em;
}

/* ===== 表单 ===== */
.login-form {
  width: 100%;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form ::v-deep .el-input__inner {
  height: 50px;
  border-radius: 12px;
  border: 1.5px solid rgba(0,0,0,0.08);
  background: rgba(0,0,0,0.015);
  font-size: 15px;
  color: #1A1A1A;
  padding-left: 44px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-form ::v-deep .el-input__inner:hover {
  border-color: rgba(122,154,126,0.25);
  background: rgba(122,154,126,0.025);
}

.login-form ::v-deep .el-input__inner:focus,
.login-form ::v-deep .el-input.is-focus .el-input__inner {
  border-color: #7A9A7E;
  background: #FFFFFF;
  box-shadow:
    0 0 0 4px rgba(122,154,126,0.08),
    0 2px 8px rgba(122,154,126,0.06);
}

.login-form ::v-deep .el-input__prefix {
  left: 16px;
  color: #B0B0B0;
  font-size: 18px;
  transition: color 0.3s ease;
}

.login-form ::v-deep .el-input.is-focus .el-input__prefix {
  color: #7A9A7E;
}

.login-form ::v-deep .el-input__suffix {
  right: 12px;
}

.login-form ::v-deep .el-input__clear {
  color: #C0C0C0;
}

.form-action {
  margin-top: 8px !important;
  margin-bottom: 0 !important;
}

/* ===== 登录按钮 ===== */
.btn-login {
  width: 100%;
  height: 50px;
  background: #1C1C1C;
  color: #FFFFFF;
  font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 0.12em;
  position: relative;
  overflow: hidden;
}

/* 按钮扫光动画 */
.btn-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    105deg,
    transparent 35%,
    rgba(255,255,255,0.06) 45%,
    rgba(255,255,255,0.12) 50%,
    rgba(255,255,255,0.06) 55%,
    transparent 65%
  );
  transform: translateX(-100%);
  transition: none;
}

.btn-login:hover:not(:disabled) .btn-shimmer {
  animation: shimmer-sweep 1.6s ease-in-out infinite;
}

@keyframes shimmer-sweep {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.btn-login::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #6B8E6E 0%, #5A7A5D 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.btn-login:hover:not(:disabled)::before {
  opacity: 1;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow:
    0 8px 24px rgba(107,142,110,0.3),
    0 0 40px rgba(107,142,110,0.08);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(107,142,110,0.2);
}

.btn-login:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-text {
  position: relative;
  z-index: 1;
}

/* ===== 底部链接 ===== */
.login-footer {
  margin-top: 28px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.footer-text {
  font-size: 14px;
  color: #999;
}

.link-register {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  color: #5A7A5D;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s;
}

.link-register:hover {
  color: #4A6A50;
}

.link-arrow {
  width: 14px; height: 14px;
  transition: transform 0.2s ease;
}

.link-register:hover .link-arrow {
  transform: translateX(2px);
}

/* ===== 开发环境快速测试 ===== */
.dev-quick-login {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.dev-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.dev-divider::before,
.dev-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: rgba(0,0,0,0.06);
}

.dev-divider-text {
  padding: 0 14px;
  font-size: 12px;
  color: #B0B0B0;
  letter-spacing: 0.06em;
}

.dev-accounts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 8px;
}

.dev-account-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: rgba(0,0,0,0.015);
  border: 1.5px solid rgba(0,0,0,0.05);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.dev-account-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at var(--mx, 50%) var(--my, 50%),
    rgba(122,154,126,0.06) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.dev-account-card:hover {
  border-color: rgba(122,154,126,0.2);
  background: rgba(122,154,126,0.03);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(122,154,126,0.08);
}

.dev-account-card:hover::after {
  opacity: 1;
}

.dev-acc-avatar {
  width: 36px; height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 14px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.dev-acc-avatar::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg,
    rgba(255,255,255,0.25) 0%,
    transparent 50%);
  border-radius: inherit;
}

.dev-acc-avatar--customer {
  background: linear-gradient(135deg, #7A9A7E, #6B8E6E);
}

.dev-acc-avatar--companion {
  background: linear-gradient(135deg, #C4A882, #B8956E);
}

.dev-acc-avatar--admin {
  background: linear-gradient(135deg, #4A4A4A, #333333);
}

.dev-acc-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  flex: 1; min-width: 0;
}

.dev-acc-name {
  font-size: 13px;
  font-weight: 600;
  color: #1A1A1A;
}

.dev-acc-role {
  font-size: 11px;
  color: #AAA;
}

.dev-acc-hint {
  font-size: 11px;
  color: #7A9A7E;
  font-weight: 500;
  flex-shrink: 0;
  opacity: 0;
  transition: all 0.3s ease;
  transform: translateX(-4px);
}

.dev-account-card:hover .dev-acc-hint {
  opacity: 1;
  transform: translateX(0);
}

/* ===== 响应式 ===== */
@media (max-width: 960px) {
  .login-page {
    padding: 20px;
  }
  .login-card {
    flex-direction: column;
    width: 100%;
    max-width: 440px;
    min-height: auto;
    /* 移动端关闭倾斜效果 */
    transform: none !important;
  }
  .card-sheen,
  .card-border-glow,
  .brand-liquid { display: none; }
  .cursor-glow { display: none; }
  .login-brand {
    flex: none;
    padding: 48px 36px 36px;
  }
  .brand-title { font-size: 30px; }
  .brand-desc { margin-bottom: 28px; }
  .brand-features {
    flex-direction: row; flex-wrap: wrap;
    gap: 16px; max-width: none; justify-content: center;
  }
  .brand-icon {
    width: 48px; height: 48px;
    border-radius: 14px; padding: 10px; margin-bottom: 20px;
  }
  .login-form-wrap {
    padding: 36px 32px 40px;
  }
  .dev-accounts {
    grid-template-columns: 1fr;
  }
}
</style>
