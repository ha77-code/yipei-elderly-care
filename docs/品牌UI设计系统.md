# 益陪养老 — 品牌 UI 设计系统

## 一、设计理念

**关键词**：温暖疗愈 · 静谧高级 · 森林呼吸感 · 克制极简 · 东方留白

**一句话概括**：以竹林为背景、暖金为点缀、毛玻璃为介质的暗调沉浸式适老化 UI 体系。

---

## 二、色彩体系

### 2.1 文字色

| Token | 色值 | 用途 |
|--------|------|------|
| 主要文字 | `#F5F0E8` / `#FFF8EE` | 大标题、Hero文字 |
| 次要文字 | `rgba(240,225,200,0.78)` | 副标题、正文 |
| 辅助文字 | `rgba(210,190,160,0.5)` | 标签、说明、placeholder |
| 弱化文字 | `rgba(200,185,165,0.35)` | 快速体验、底部链接 |

### 2.2 品牌色

| Token | 色值 | 用途 |
|--------|------|------|
| 暖金主色 | `rgba(225,195,160,0.9)` | 按钮实心填充 |
| 暖金亮色 | `rgba(240,210,175,0.95)` | 按钮 hover |
| 暖金描边 | `rgba(230,200,160,0.35)` | 按钮边框、聚焦描边 |
| 暖金发光 | `rgba(255,200,120,0.7)` | 光标发光、聚焦环 |

### 2.3 背景色

| Token | 色值 | 用途 |
|--------|------|------|
| 页面底色 | `#0D1A0C` | body 背景 |
| 面板底色 | `rgba(10,20,8,0.85)` | 毛玻璃面板 |
| 深色遮罩 | `rgba(6,10,4,0.35~0.55)` | 背景图片蒙版 |
| 输入框底 | `rgba(30,44,22,0.35)` | 表单字段背景 |

### 2.4 功能色

| Token | 色值 | 用途 |
|--------|------|------|
| 错误红 | `rgba(235,160,140,0.8)` | 表单错误提示 |
| 错误边框 | `rgba(220,140,120,0.5)` | 错误输入框 |

---

## 三、字体体系

| 层级 | 字体 | 字号 | 字重 | 用途 |
|------|------|------|------|------|
| Display | `Noto Serif SC` / `STSong` 衬线 | 52-96px | 700-800 | 品牌大标题 |
| Heading | `Noto Serif SC` 衬线 | 28-48px | 600-700 | 章节标题 |
| Body | `PingFang SC` / `Microsoft YaHei` 无衬线 | 15-17px | 400-500 | 正文 |
| Label | `PingFang SC` 无衬线 | 12-13px | 500 | 表单标签 |
| Caption | `PingFang SC` 无衬线 | 11-12px | 400 | 辅助说明 |

**字距**：标题 `0.04-0.06em`，正文 `0.03em`，标签 `0.06-0.1em`

---

## 四、动效系统

### 4.1 缓动曲线

| 名称 | 曲线 | 用途 |
|------|------|------|
| 弹性缓出 | `cubic-bezier(0.34,1.56,0.64,1)` | 卡片弹出、字符放大 |
| 平滑缓动 | `cubic-bezier(0.22,0.61,0.36,1)` | 面板滑入、按钮变换 |
| 标准缓动 | `cubic-bezier(0.4,0,0.2,1)` | hover 过渡 |
| 减速缓出 | `cubic-bezier(0.16,0.67,0.29,1)` | 大标题浮现 |

### 4.2 滚动动画（Intersection Observer）

| 版块 | 动画 | 时长 | 延迟 |
|------|------|------|------|
| 章节标签 | `reveal-soft` 纯淡入 | 0.9s | 0s |
| 大标题 | `reveal-heading` 从下方60px浮起 | 1.1s | 0.1s |
| 正文 | `reveal-up` 从下方50px上移 | 0.9s | 0.2s |
| 品牌要点卡片 | `reveal-card` 弹性弹出(scale 0.94→1) | 0.8s | 交错0.1s |
| 角色卡片 | `reveal-card` | 0.8s | 交错0.1s |
| 数据数字 | `reveal-scale` 缩放淡入 | 0.9s | 交错0.1s |
| 服务卡片 | `reveal-left/right` 交替左右滑入 | 0.9s | 交错0.1s |
| CTA按钮 | `reveal-card` 弹性弹出 | 0.8s | 0.4s |

### 4.3 光标交互

| 元素 | 效果 | 参数 |
|------|------|------|
| 光标圆点 | 暖金发光跟随 | 10px `rgba(255,210,140,0.95)` + 三层 box-shadow(20/50/90px) |
| 光标圆环 | 描边延迟跟随 | 50px `rgba(255,200,130,0.35)` 边框 |
| 粒子拖尾 | 5 个渐小粒子粘滞跟随 | 18→5px，延迟 0.06→0.33s，距离越远越透明 |
| Hover 放大 | 光标 + 圆环放大 | dot 40px / ring 90px，mix-blend-mode: overlay |
| 悬浮栏感应 | 光标靠近栏位40px | 栏位 1.02x 微放大 + 暖金外发光 60px |
| 卡片靠近 | 光标靠近卡片60px | 卡片整体暖光增强 |
| Hero 字符 | 光标靠近字符160px | 弹性放大 1.25x + 暖金发光 text-shadow |

### 4.4 登录页交互

| 状态 | 效果 | 过渡 |
|------|------|------|
| 初始 Minimal | 表单漂浮，输入框仅底部细线，按钮描边 | - |
| 聚焦 → V2 | 深色面板从右侧滑入，输入框→圆角全包边，按钮→实心 | 0.7s cubic-bezier(0.22,0.61,0.36,1) |
| 离开聚焦 | 600ms后面板滑回右侧 | 同上 |

### 4.5 聚光灯

| 参数 | 值 |
|------|-----|
| 光圈大小 | 420px 径向渐变 |
| 暗区透明度 | `rgba(6,12,4,0.32)` |
| 过渡 | 中心透明 → 22%透明 → 58%半暗 → 100%全黑 |
| 跟随方式 | requestAnimationFrame，无延迟 |

---

## 五、材质与质感

### 5.1 毛玻璃（Glassmorphism）

| 参数 | 值 |
|------|------|
| 背景色 | `rgba(0,0,0,0.28)` ~ `rgba(10,20,8,0.85)` |
| 模糊度 | `blur(16-32px)` |
| 饱和度 | `saturate(1.2-1.4)` |
| 边框 | `1px solid rgba(255,255,255,0.08-0.1)` |
| 内发光 | `inset 0 1px 0 rgba(255,255,255,0.05-0.06)` |

### 5.2 阴影层级

| 层级 | 值 |
|------|-----|
| xs | `0 1px 2px rgba(44,36,24,0.02)` |
| sm | `0 1px 3px rgba(44,36,24,0.03), 0 1px 2px rgba(44,36,24,0.02)` |
| md | `0 4px 16px rgba(44,36,24,0.04), 0 2px 4px rgba(44,36,24,0.02)` |
| lg | `0 12px 36px rgba(44,36,24,0.05), 0 4px 8px rgba(44,36,24,0.02)` |

### 5.3 圆角系统

| 层级 | 值 | 用途 |
|------|-----|------|
| sm | 8px | 标签、小按钮 |
| md | 10-12px | 输入框、卡片 |
| lg | 14-18px | 大卡片 |
| xl | 20-22px | 登录卡片 |
| full | 50px | 按钮、悬浮栏 |

---

## 六、背景处理

| 层级 | 内容 |
|------|------|
| 底层 | 竹林实景照片 `bg-bamboo.jpg`，`center/cover` |
| 暗化层 | `linear-gradient(160deg, rgba(6,10,4,0.35~0.55)...)` 多层叠加 |
| 左区蒙版 | `radial-gradient(ellipse at 35% 50%)` + `linear-gradient(90deg)` 左暗右渐透 |
| 过渡带 | 200px 四段 `linear-gradient`，从左透明到右面板色 |
| 聚光灯 | CSS mask-image 径向渐变，光标处透明，边缘暗 |

---

## 七、组件规范

### 7.1 按钮

| 变体 | 背景 | 边框 | 字色 | 高度 | 圆角 |
|------|------|------|------|------|------|
| 实心暖金 | `rgba(225,195,160,0.9)` | 无 | `#0E0C06` | 52px | 11px |
| 描边暖金 | 透明 | `rgba(230,200,160,0.35)` | `rgba(245,220,180,0.88)` | 52px | 50px |

### 7.2 输入框

| 状态 | 边框 | 背景 | 高度 | 字号 |
|------|------|------|------|------|
| 默认 | `rgba(210,195,175,0.2)` | `rgba(30,44,22,0.35)` | 52px | 17px |
| Hover | `rgba(225,210,185,0.35)` | `rgba(35,50,26,0.4)` | - | - |
| Focus | `rgba(240,210,170,0.6)` + 4px发光环 | `rgba(35,50,26,0.45)` | - | - |

### 7.3 Tab 切换

| 状态 | 字色 | 下划线 |
|------|------|--------|
| 默认 | `rgba(240,225,205,0.58)` | 无 |
| Active | `rgba(250,225,190,0.95)` | 2px `rgba(240,210,170,0.65)` |

---

## 八、AI 绘图提示词（English）

```
A dark serene bamboo forest background for a premium elderly care platform login page. 
Deep muted forest green color palette with warm golden amber accents (#C8956B, #FFD28C). 
Soft frosted glass panels with subtle blur. Left-right split layout: left side shows real 
bamboo forest photography with dark green radial gradient overlay, right side is a dark 
translucent form panel. Minimal elegant Chinese typography using serif fonts. 
Warm golden cursor glow effect like sunlight through leaves. No bright saturated colors. 
Mood: calm, healing, premium, natural, zen-like tranquility. Style: modern organic minimalism 
with Japanese wabi-sabi influence. Dark mode, low contrast, soft atmospheric lighting.
```

## 九、AI 绘图提示词（中文）

```
暗调静谧竹林背景，适老化高端服务平台登录页。
深墨绿色系搭配暖金琥珀点缀(#C8956B)。柔和毛玻璃质感，轻微模糊。
左右分割布局：左侧真实竹林摄影叠加墨绿径向渐变蒙版，右侧深色半透表单面板。
极简东方衬线字体排版。暖金色光标发光效果如阳光穿过树叶。
无高饱和亮色。氛围：治愈、静谧、高级、自然、禅意。
风格：现代有机极简主义融合日式侘寂美学。暗色模式，低对比度柔和光影。
```
