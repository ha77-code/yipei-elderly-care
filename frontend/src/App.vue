<template>
  <div id="app">
    <header class="app-header">
      <div class="header-container">
        <div class="logo-section">
          <span class="logo-icon">🏥</span>
          <span class="logo-text">益陪</span>
          <span class="logo-subtitle">智慧养老陪诊平台</span>
        </div>
        <nav class="nav-links">
          <router-link to="/">
            <i class="el-icon-s-home"></i> 首页
          </router-link>
          <router-link to="/about">
            <i class="el-icon-info"></i> 关于
          </router-link>
          <router-link to="/admin/audit">
            <i class="el-icon-s-check"></i> 陪诊师审核
          </router-link>
        </nav>
      </div>
    </header>
    <main class="main-container">
      <router-view />
    </main>
    <footer class="app-footer">
      <p>© 2026 益陪 — 智慧养老陪诊平台 · 让关爱零距离</p>
    </footer>
  </div>
</template>

<style>
/* ===== 全局重置与基础样式 ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', 'Helvetica Neue', Helvetica, Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #f0f4f8;
  color: #2d3748;
  min-height: 100vh;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* ===== 头部导航 ===== */
.app-header {
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 50%, #3b82f6 100%);
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 2px;
}

.logo-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  font-weight: 400;
  margin-left: 6px;
  padding-left: 12px;
  border-left: 1px solid rgba(255, 255, 255, 0.3);
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-links a {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-links a:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.nav-links a.router-link-exact-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.22);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* ===== 主体内容区 ===== */
.main-container {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 32px auto;
  padding: 0 24px;
}

/* ===== 页脚 ===== */
.app-footer {
  background: #1e293b;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
  padding: 20px 24px;
  font-size: 13px;
  letter-spacing: 0.5px;
  margin-top: auto;
}

/* ===== Element UI 全局覆盖美化 ===== */
.el-table {
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.el-table th {
  background: #f8fafc !important;
  color: #475569 !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  border-bottom: 2px solid #e2e8f0 !important;
}

.el-table td {
  font-size: 14px !important;
  color: #334155 !important;
}

.el-table--border td,
.el-table--border th {
  border-color: #f1f5f9 !important;
}

.el-table .el-button--mini {
  border-radius: 6px !important;
  padding: 6px 16px !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

.el-table .el-button--mini:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.el-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
}

.el-dialog__header {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  padding: 20px 24px !important;
}

.el-dialog__title {
  color: #ffffff !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

.el-dialog__headerbtn .el-dialog__close {
  color: #ffffff !important;
  font-size: 20px !important;
}

.el-dialog__body {
  padding: 28px 24px !important;
}

.el-dialog__footer {
  padding: 16px 24px !important;
  border-top: 1px solid #f1f5f9;
  background: #fafbfc;
}

.el-button--primary {
  background: linear-gradient(135deg, #2563eb, #3b82f6) !important;
  border: none !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  letter-spacing: 0.3px !important;
  transition: all 0.2s ease !important;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.25) !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #1d4ed8, #2563eb) !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35) !important;
}

.el-button--default {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

.el-button--default:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

.el-form-item__label {
  font-weight: 600 !important;
  color: #475569 !important;
}

.el-input__inner,
.el-textarea__inner {
  border-radius: 8px !important;
  border-color: #e2e8f0 !important;
  transition: all 0.2s ease !important;
}

.el-input__inner:focus,
.el-textarea__inner:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
}

.el-select .el-input__inner {
  border-radius: 8px !important;
}

.el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

.el-checkbox__inner:hover {
  border-color: #3b82f6 !important;
}

.el-message {
  border-radius: 10px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.el-message--success {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5) !important;
  border-color: #6ee7b7 !important;
}

.el-message__icon {
  color: #10b981 !important;
}
</style>
