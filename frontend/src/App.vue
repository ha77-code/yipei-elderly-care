<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default { name: 'App' }
</script>

<style>
/* ═══ 主题变量（浅色=默认） ═══ */
:root {
  /* 背景 */
  --bg-root: #eef2e6;
  --bg-panel: rgba(255,255,255,0.45);
  --bg-panel-gradient: linear-gradient(180deg, rgba(255,255,255,0.45), rgba(248,250,240,0.28));
  --bg-card: rgba(255,255,255,0.58);
  --bg-card-solid: rgba(254,255,251,0.98);
  --bg-input: rgba(255,255,255,0.72);
  --bg-hover: rgba(108,140,80,0.07);
  --bg-table-stripe: rgba(248,250,240,0.3);
  --bg-thread-other: rgba(255,255,255,0.8);
  /* 文字 */
  --text-primary: rgba(46,60,38,0.9);
  --text-heading: rgba(78,106,56,0.92);
  --text-secondary: rgba(96,110,82,0.8);
  --text-muted: rgba(130,140,116,0.7);
  /* 边框 */
  --border-card: rgba(255,255,255,0.7);
  --border-soft: rgba(150,140,110,0.16);
  --border-input: rgba(150,140,110,0.28);
  /* 阴影 */
  --shadow-card: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6);
  --shadow-sm: 0 8px 22px -12px rgba(60,70,40,0.28);
  --shadow-dropdown: 0 20px 50px -14px rgba(50,60,30,0.44);
  /* 顶栏 */
  --topbar-bg: linear-gradient(180deg, rgba(255,255,255,0.72), rgba(248,250,240,0.6));
  --topbar-border: rgba(150,140,110,0.2);
  --topbar-shadow: 0 2px 20px -8px rgba(60,70,40,0.12);
  /* 品牌色（两主题共用） */
  --brand-accent: 108,140,80;
  --brand-deep: 78,106,56;
  --brand-gold: 240,210,130;
  --brand-amber: 170,130,60;
  /* 按钮-危险 */
  --btn-danger-bg: #c0392b;
  --btn-danger-hover: #e74c3c;
  /* 旧版兼容 */
  --brand-forest-950: #0e180d;
  --brand-cream-100: rgba(46,60,38,0.9);
  --color-text-primary: rgba(46,60,38,0.9);
  --color-text-secondary: rgba(96,110,82,0.8);
  --color-text-placeholder: rgba(130,140,116,0.7);
  --color-primary: rgba(108,140,80,0.85);
  --color-primary-dark: rgba(78,106,56,0.88);
  --color-primary-light: rgba(160,210,110,0.6);
  --color-primary-dim: rgba(108,140,80,0.08);
  --color-warm: rgba(245,215,140,0.5);
  --color-warm-dark: rgba(170,130,60,0.9);
  --color-warm-dim: rgba(245,215,140,0.15);
  --color-bg-page: rgba(248,250,240,0.5);
  --color-bg-card: rgba(255,255,255,0.62);
  --color-border-light: rgba(150,140,110,0.14);
  --color-border: rgba(150,140,110,0.2);
  --shadow-xs: 0 4px 12px -6px rgba(60,70,40,0.12);
  --shadow-sm: 0 8px 22px -12px rgba(60,70,40,0.2);
  --shadow-md: 0 12px 36px -16px rgba(50,60,30,0.25);
  --shadow-lg: 0 20px 50px -14px rgba(50,60,30,0.3);
  --ease-standard: cubic-bezier(0.4, 0, 0.2, 1);
  --ease-smooth: cubic-bezier(0.22, 0.61, 0.36, 1);
  --font-sans: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-serif: 'Noto Serif SC', 'STSong', 'Songti SC', serif;
  --font-family: var(--font-sans);
  --radius-sm: 8px; --radius-md: 10px; --radius-lg: 14px; --radius-xl: 20px;
}

*, *::before, *::after { box-sizing: border-box; }
* { margin: 0; padding: 0; }

html, body, #app { min-height: 100%; }
html, body {
  font-family: var(--font-sans);
  font-size: 16px;
  line-height: 1.7;
  color: var(--text-primary);
  background: var(--bg-root);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition: background 0.4s ease, color 0.4s ease;
}

/* 暗色主题背景纹理 */
body::before {
  content: '';
  position: fixed; inset: 0; z-index: -2; pointer-events: none;
  background:
    linear-gradient(90deg, transparent 0 11%, rgba(255,210,140,0.04) 11.2% 11.4%, transparent 11.7% 25%, rgba(255,210,140,0.035) 25.2% 25.4%, transparent 25.8% 43%, rgba(255,210,140,0.03) 43.2% 43.35%, transparent 43.7%),
    linear-gradient(160deg, rgba(10,18,8,0.55), rgba(18,32,15,0.82) 48%, rgba(10,18,8,0.92)),
    radial-gradient(ellipse at 18% 12%, rgba(240,210,175,0.22), transparent 38%),
    radial-gradient(ellipse at 85% 18%, rgba(55,90,48,0.48), transparent 46%),
    #0e180d;
  opacity: 0;
}

[data-theme="light"] body::before, :root 

body::after {
  content: '';
  position: fixed; inset: 0; z-index: -1;
  opacity: 0.18;
  background-image:
    linear-gradient(115deg, transparent 0 46%, rgba(245,240,232,0.12) 47%, transparent 48%),
    linear-gradient(70deg, transparent 0 54%, rgba(245,240,232,0.1) 55%, transparent 56%);
  background-size: 180px 280px, 240px 320px;
  pointer-events: none;
}

button, input, textarea, select { font-family: inherit; }
a { color: inherit; }

.el-button { min-height: 42px; padding-left: 18px; padding-right: 18px; border-radius: 8px; font-weight: 600; transition: all 0.24s cubic-bezier(0.4,0,0.2,1); }
.el-button--primary { background: rgba(225,195,160,0.92); border-color: rgba(225,195,160,0.92); color: #0e0c06; }
.el-button--primary:hover, .el-button--primary:focus { background: rgba(240,210,175,0.96); border-color: rgba(240,210,175,0.96); color: #0e0c06; box-shadow: 0 0 0 4px rgba(255,210,140,0.14); }
.el-button--success { background: #2d5a3a; border-color: #2d5a3a; }
.el-button--warning { background: #b99163; border-color: #b99163; }
.el-button--danger { background: #c0392b; border-color: #c0392b; color: #fff; }
.el-button--danger:hover { background: #e74c3c; border-color: #e74c3c; }

.el-input__inner, .el-textarea__inner { font-size: 16px; min-height: 44px; border-radius: 8px; border-color: rgba(210,195,175,0.24); background: rgba(255,255,255,0.96); color: #162414; transition: all 0.24s ease; }
.el-form-item__label { font-size: 16px; color: #1a1a1a !important; font-weight: 600; }
.el-input__inner:hover, .el-textarea__inner:hover { border-color: rgba(225,195,160,0.55); }
.el-input__inner:focus, .el-input.is-focus .el-input__inner, .el-textarea__inner:focus { border-color: rgba(225,195,160,0.84); box-shadow: 0 0 0 4px rgba(225,195,160,0.15); }



/* Light card defaults */
.el-card, .el-table, .el-dialog { border-radius: 10px; }
.el-card { background: rgba(255,248,238,0.96); color: #162414; }

.el-table { color: #22301f; }





.el-menu { border-right: none; }
.el-message, .el-dropdown-menu { border-radius: 10px; }

.el-tag { border-radius: 8px; }
.el-pagination .el-pager li.active { background: #35542e; color: #fff; }
.el-loading-spinner .path { stroke: #e1c3a0; }

/* ===== 页面切换过渡 ===== */
.page-fade-enter-active { animation: page-in 0.35s var(--ease-smooth) both; }
.page-fade-leave-active { animation: page-out 0.2s ease both; }
@keyframes page-in { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
@keyframes page-out { from { opacity: 1; } to { opacity: 0; } }

/* ===== 通用入场动画 ===== */
@keyframes reveal-up { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
@keyframes reveal-scale { from { opacity: 0; transform: scale(0.96); } to { opacity: 1; transform: scale(1); } }
</style>
