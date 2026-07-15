<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* ===== 全局 CSS 自定义属性（暖绿+米黄养老主题） ===== */
:root {
  /* 主色调 */
  --color-primary: #7A9A7E;
  --color-primary-light: #A3BFA6;
  --color-primary-dark: #5C7A60;

  /* 辅助色 */
  --color-warm-beige: #F5F0E8;
  --color-warm-beige-dark: #EBE3D5;
  --color-light-coffee: #C4A882;
  --color-deep-coffee: #8B7355;

  /* 文字色 */
  --color-text-primary: #1A1A1A;
  --color-text-regular: #333333;
  --color-text-secondary: #666666;
  --color-text-placeholder: #999999;

  /* 背景色 */
  --color-bg-page: #FDFBF7;
  --color-bg-card: #FFFFFF;

  /* 边框 */
  --color-border: #E8E1D5;
  --color-border-light: #F0EBE0;

  /* 圆角 */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;

  /* 阴影（轻柔，无厚重阴影） */
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 2px 8px rgba(0, 0, 0, 0.06);

  /* 字体 */
  --font-family: 'Inter', 'Noto Sans SC', '思源黑体', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ===== 全局重置 ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: var(--font-family);
  font-size: 16px;
  line-height: 1.7;
  color: var(--color-text-primary);
  background-color: var(--color-bg-page);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
}

/* ===== 全局 ElementUI 主题覆写 ===== */
/* 主按钮改为暖绿色 */
.el-button--primary {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}
.el-button--primary:hover,
.el-button--primary:focus {
  background-color: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
}

/* 链接色 */
.el-link.el-link--primary {
  color: var(--color-primary);
}
.el-link.el-link--primary:hover {
  color: var(--color-primary-dark);
}

/* 输入框聚焦 */
.el-input__inner:focus {
  border-color: var(--color-primary);
}

/* 菜单选中 */
.el-menu-item.is-active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}

/* 标签 */
.el-tag--primary {
  background-color: rgba(122, 154, 126, 0.1);
  border-color: rgba(122, 154, 126, 0.2);
  color: var(--color-primary-dark);
}

/* 分页 active */
.el-pagination .el-pager li.active {
  background-color: var(--color-primary);
}

/* 开关 */
.el-switch.is-checked .el-switch__core {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}

/* 复选框 */
.el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}
.el-checkbox__input.is-checked + .el-checkbox__label {
  color: var(--color-primary);
}

/* 单选框 */
.el-radio__input.is-checked .el-radio__inner {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}
.el-radio__input.is-checked + .el-radio__label {
  color: var(--color-primary);
}

/* 加载动画 */
.el-loading-spinner .path {
  stroke: var(--color-primary);
}
</style>
