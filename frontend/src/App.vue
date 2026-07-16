<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default { name: 'App' }
</script>

<style>
/* ===== Design Tokens ===== */
:root {
  /* Brand */
  --color-primary: #7A9A7E;
  --color-primary-light: #A3BFA6;
  --color-primary-dark: #5C7A60;
  --color-primary-dim: #E8F0E8;

  /* Warm accent */
  --color-warm: #C4A882;
  --color-warm-dark: #8B7355;
  --color-warm-dim: #F5ECE0;

  /* Neutrals */
  --color-text-primary: #1A1A1A;
  --color-text-regular: #3D3D3D;
  --color-text-secondary: #6B6B6B;
  --color-text-placeholder: #A0A0A0;

  /* Backgrounds */
  --color-bg-page: #FBF9F5;
  --color-bg-card: #FFFFFF;
  --color-bg-hover: #F6F8F5;

  /* Borders */
  --color-border: #E8E4DC;
  --color-border-light: #F0EDE6;

  /* Radii */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 18px;

  /* Shadows - layered realism */
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.03);
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.03);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.04), 0 2px 4px rgba(0,0,0,0.03);
  --shadow-lg: 0 8px 30px rgba(0,0,0,0.05), 0 2px 8px rgba(0,0,0,0.03);

  /* Typography */
  --font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-mono: 'SF Mono', 'Fira Code', 'Consolas', monospace;
}

/* ===== Reset ===== */
*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

html, body {
  height: 100%;
  font-family: var(--font-family);
  font-size: 15px;
  line-height: 1.65;
  color: var(--color-text-primary);
  background: var(--color-bg-page);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app { height: 100%; }

/* ===== ElementUI Global Overrides ===== */

/* Buttons */
.el-button { font-weight: 500; letter-spacing: 0.01em; transition: all 0.2s cubic-bezier(0.4,0,0.2,1); }
.el-button--primary { background: var(--color-primary-dark); border-color: var(--color-primary-dark); }
.el-button--primary:hover, .el-button--primary:focus { background: var(--color-primary); border-color: var(--color-primary); }
.el-button--primary.is-plain { color: var(--color-primary-dark); background: var(--color-primary-dim); border-color: transparent; }
.el-button--primary.is-plain:hover { background: var(--color-primary); color: #fff; }
.el-button--success { background: #5C7A60; border-color: #5C7A60; }
.el-button--danger { background: #D4786E; border-color: #D4786E; }
.el-button--danger:hover { background: #C06058; border-color: #C06058; }
.el-button--warning { background: #D4A86E; border-color: #D4A86E; }
.el-button--small { font-size: 13px; padding: 8px 18px; border-radius: var(--radius-sm); }
.el-button--medium { font-size: 14px; padding: 10px 22px; border-radius: var(--radius-sm); }
.el-button--round { border-radius: 50px; }

/* Tables */
.el-table { font-size: 14px; }
.el-table th { background: #F9F8F5; color: var(--color-text-secondary); font-weight: 600; font-size: 13px; padding: 14px 0; border-bottom: 1px solid var(--color-border-light); }
.el-table td { padding: 14px 0; color: var(--color-text-regular); }
.el-table--striped .el-table__body tr.el-table__row--striped td { background: #FAFAF8; }
.el-table__body tr:hover > td { background: #F5F7F3 !important; }

/* Forms */
.el-input__inner { border-radius: var(--radius-sm); border-color: var(--color-border); transition: all 0.2s ease; }
.el-input__inner:hover { border-color: #C8D0C4; }
.el-input__inner:focus, .el-input.is-focus .el-input__inner { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(122,154,126,0.08); }
.el-textarea__inner { border-radius: var(--radius-sm); border-color: var(--color-border); font-size: 14px; }

/* Select */
.el-select .el-input__inner:focus { border-color: var(--color-primary); }

/* Dialogs */
.el-dialog { border-radius: var(--radius-lg); box-shadow: var(--shadow-lg); }
.el-dialog__header { padding: 24px 28px 0; font-weight: 700; }
.el-dialog__body { padding: 20px 28px; }
.el-dialog__footer { padding: 0 28px 24px; }

/* Cards */
.el-card { border-radius: var(--radius-md); border: 1px solid var(--color-border-light); box-shadow: var(--shadow-xs); }
.el-card__header { padding: 16px 20px; border-bottom: 1px solid var(--color-border-light); font-weight: 600; }

/* Pagination */
.el-pagination .el-pager li { border-radius: var(--radius-sm); font-weight: 500; }
.el-pagination .el-pager li.active { background: var(--color-primary-dark); }
.el-pagination button:hover { color: var(--color-primary); }

/* Tags */
.el-tag { border-radius: 6px; }
.el-tag--small { padding: 0 8px; height: 24px; line-height: 24px; }

/* Messages */
.el-message { border-radius: var(--radius-md); box-shadow: var(--shadow-md); }

/* Loading */
.el-loading-spinner .path { stroke: var(--color-primary-dark); }

/* Menu */
.el-menu-item { transition: all 0.2s ease; }
.el-menu-item.is-active { color: var(--color-primary-dark); }

/* Dropdown */
.el-dropdown-menu { border-radius: var(--radius-sm); box-shadow: var(--shadow-md); border: 1px solid var(--color-border-light); }
.el-dropdown-menu__item { line-height: 36px; font-size: 14px; }

/* Date Picker */
.el-date-editor .el-input__inner { border-radius: var(--radius-sm); }

/* Switch */
.el-switch.is-checked .el-switch__core { background: var(--color-primary); border-color: var(--color-primary); }

/* Checkbox & Radio */
.el-checkbox__input.is-checked .el-checkbox__inner,
.el-radio__input.is-checked .el-radio__inner { background: var(--color-primary); border-color: var(--color-primary); }
.el-checkbox__input.is-checked + .el-checkbox__label,
.el-radio__input.is-checked + .el-radio__label { color: var(--color-primary-dark); }
</style>
