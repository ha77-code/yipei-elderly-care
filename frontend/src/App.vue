<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default { name: 'App' }
</script>

<style>
:root {
  --brand-forest-950: #0e180d;
  --brand-forest-900: #152413;
  --brand-forest-820: #1b2e18;
  --brand-forest-760: #243a20;
  --brand-forest-680: #35542e;
  --brand-gold-500: #e1c3a0;
  --brand-gold-420: #f0d2af;
  --brand-gold-300: #ffd28c;
  --brand-cream-100: #fff8ee;
  --brand-cream-180: #f5f0e8;
  --text-primary: #faf7f2;
  --text-regular: rgba(250, 247, 242, 0.94);
  --text-secondary: rgba(245, 238, 225, 0.84);
  --text-muted: rgba(220, 200, 170, 0.65);
  --panel-dark: rgba(10, 20, 8, 0.82);
  --panel-soft: rgba(18, 34, 16, 0.72);
  --panel-card: rgba(255, 248, 238, 0.94);
  --input-dark: rgba(30, 44, 22, 0.42);
  --border-soft: rgba(255, 248, 238, 0.1);
  --border-gold: rgba(230, 200, 160, 0.35);
  --shadow-soft: 0 18px 60px rgba(0, 0, 0, 0.28);
  --shadow-card: 0 14px 36px rgba(7, 16, 7, 0.18);
  --radius-sm: 8px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --radius-xl: 20px;
  --ease-standard: cubic-bezier(0.4, 0, 0.2, 1);
  --ease-smooth: cubic-bezier(0.22, 0.61, 0.36, 1);
  --font-sans: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-serif: 'Noto Serif SC', 'STSong', 'Songti SC', serif;
  --color-primary: #7a9a7e;
  --color-primary-light: #a3bfa6;
  --color-primary-dark: #5c7a60;
  --color-primary-dim: #e8f0e8;
  --color-warm: #c4a882;
  --color-warm-dark: #8b7355;
  --color-warm-dim: #f5ece0;
  --color-text-primary: #1a1a1a;
  --color-text-regular: #3d3d3d;
  --color-text-secondary: #6b6b6b;
  --color-text-placeholder: #a0a0a0;
  --color-bg-page: #fbf9f5;
  --color-bg-card: #ffffff;
  --color-bg-hover: #f6f8f5;
  --color-border: #e8e4dc;
  --color-border-light: #f0ede6;
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.03);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.03);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.04), 0 2px 4px rgba(0,0,0,0.03);
  --shadow-lg: 0 8px 30px rgba(0,0,0,0.05), 0 2px 8px rgba(0,0,0,0.03);
  --font-family: var(--font-sans);
}

*, *::before, *::after { box-sizing: border-box; }
* { margin: 0; padding: 0; }

html, body, #app { min-height: 100%; }
html, body {
  font-family: var(--font-sans);
  font-size: 15px;
  line-height: 1.65;
  color: var(--text-primary);
  background: var(--brand-forest-950);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -2;
  background:
    linear-gradient(90deg, transparent 0 11%, rgba(255, 210, 140, 0.04) 11.2% 11.4%, transparent 11.7% 25%, rgba(255, 210, 140, 0.035) 25.2% 25.4%, transparent 25.8% 43%, rgba(255, 210, 140, 0.03) 43.2% 43.35%, transparent 43.7%),
    linear-gradient(160deg, rgba(10, 18, 8, 0.55), rgba(18, 32, 15, 0.82) 48%, rgba(10, 18, 8, 0.92)),
    radial-gradient(ellipse at 18% 12%, rgba(240, 210, 175, 0.22), transparent 38%),
    radial-gradient(ellipse at 85% 18%, rgba(55, 90, 48, 0.48), transparent 46%),
    #0e180d;
}

body::after {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  opacity: 0.18;
  background-image:
    linear-gradient(115deg, transparent 0 46%, rgba(245, 240, 232, 0.12) 47%, transparent 48%),
    linear-gradient(70deg, transparent 0 54%, rgba(245, 240, 232, 0.1) 55%, transparent 56%);
  background-size: 180px 280px, 240px 320px;
  pointer-events: none;
}

button, input, textarea, select { font-family: inherit; }
a { color: inherit; }

.el-button { border-radius: var(--radius-sm); font-weight: 600; letter-spacing: 0; transition: all 0.24s var(--ease-standard); }
.el-button--primary { background: rgba(225, 195, 160, 0.92); border-color: rgba(225, 195, 160, 0.92); color: #0e0c06; }
.el-button--primary:hover, .el-button--primary:focus { background: rgba(240, 210, 175, 0.96); border-color: rgba(240, 210, 175, 0.96); color: #0e0c06; box-shadow: 0 0 0 4px rgba(255, 210, 140, 0.14); }
.el-button--success { background: #2d5a3a; border-color: #2d5a3a; }
.el-button--warning { background: #b99163; border-color: #b99163; }
.el-button--danger { background: #b86658; border-color: #b86658; }

.el-input__inner, .el-textarea__inner {
  border-radius: var(--radius-sm);
  border-color: rgba(210, 195, 175, 0.24);
  background: rgba(255, 255, 255, 0.96);
  color: #162414;
  transition: all 0.24s var(--ease-standard);
}
.el-input__inner:hover, .el-textarea__inner:hover { border-color: rgba(225, 195, 160, 0.55); }
.el-input__inner:focus, .el-input.is-focus .el-input__inner, .el-textarea__inner:focus { border-color: rgba(225, 195, 160, 0.84); box-shadow: 0 0 0 4px rgba(225, 195, 160, 0.15); }

.el-card, .el-table, .el-dialog {
  border: 1px solid rgba(230, 200, 160, 0.18);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
}
.el-card { background: rgba(255, 248, 238, 0.96); color: #162414; }
.el-table { color: #22301f; }
.el-table th { background: rgba(239, 230, 214, 0.8); color: #42513d; font-weight: 700; }
.el-table td { color: #243421; }
.el-table__body tr:hover > td { background: rgba(225, 195, 160, 0.14) !important; }

.el-menu { border-right: none; }
.el-message, .el-dropdown-menu { border-radius: var(--radius-md); box-shadow: var(--shadow-card); }
.el-tag { border-radius: 8px; }
.el-pagination .el-pager li.active { background: var(--brand-forest-680); color: #fff; }
.el-loading-spinner .path { stroke: var(--brand-gold-500); }
</style>
