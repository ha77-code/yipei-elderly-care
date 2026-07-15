<template>
  <div class="hello">
    <div class="hero-card">
      <h1 class="hero-title">{{ msg }}</h1>
      <p class="hero-desc">
        专业的智慧养老陪诊平台，连接您与贴心的陪诊师
      </p>
      <div class="hero-features">
        <div class="feature-item">
          <span class="feature-icon">👨‍⚕️</span>
          <span class="feature-label">专业陪诊</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🏥</span>
          <span class="feature-label">医院对接</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">💖</span>
          <span class="feature-label">老人关爱</span>
        </div>
      </div>
    </div>

    <div class="info-section">
      <h3 class="section-title">平台技术栈</h3>
      <div class="tech-tags">
        <span class="tag tag-vue">Vue.js</span>
        <span class="tag tag-router">Vue Router</span>
        <span class="tag tag-vuex">Vuex</span>
        <span class="tag tag-ui">Element UI</span>
      </div>
    </div>

    <div class="info-section">
      <h3 class="section-title">快速入口</h3>
      <ul class="quick-links">
        <li>
          <router-link to="/admin/audit" class="quick-link-card">
            <span class="quick-link-icon">✅</span>
            <div>
              <strong>陪诊师审核</strong>
              <small>管理陪诊师入驻申请</small>
            </div>
          </router-link>
        </li>
        <li>
          <a href="https://vuejs.org" target="_blank" rel="noopener" class="quick-link-card">
            <span class="quick-link-icon">📖</span>
            <div>
              <strong>Vue 文档</strong>
              <small>渐进式框架核心文档</small>
            </div>
          </a>
        </li>
        <li>
          <a href="https://element.eleme.io" target="_blank" rel="noopener" class="quick-link-card">
            <span class="quick-link-icon">🎨</span>
            <div>
              <strong>Element UI</strong>
              <small>桌面端组件库</small>
            </div>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
};
</script>

<style scoped>
/* ===== Hero 区域 ===== */
.hero-card {
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%);
  border-radius: 20px;
  padding: 56px 40px;
  text-align: center;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(30, 58, 95, 0.2);
  position: relative;
  overflow: hidden;
}

.hero-card::before {
  content: "";
  position: absolute;
  top: -60%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
  pointer-events: none;
}

.hero-card::after {
  content: "";
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 50%;
  pointer-events: none;
}

.hero-title {
  color: #ffffff;
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.hero-desc {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
  font-weight: 400;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.hero-features {
  display: flex;
  justify-content: center;
  gap: 32px;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px 24px;
  transition: transform 0.2s ease;
}

.feature-item:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.18);
}

.feature-icon {
  font-size: 24px;
}

.feature-label {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
}

/* ===== 信息区块 ===== */
.info-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
  position: relative;
  padding-left: 16px;
}

.section-title::before {
  content: "";
  position: absolute;
  left: 0;
  top: 2px;
  bottom: 2px;
  width: 4px;
  background: linear-gradient(180deg, #2563eb, #3b82f6);
  border-radius: 2px;
}

/* ===== 技术标签 ===== */
.tech-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.tag:hover {
  transform: translateY(-2px);
}

.tag-vue {
  background: linear-gradient(135deg, #42b883, #35495e);
  box-shadow: 0 4px 12px rgba(66, 184, 131, 0.3);
}

.tag-router {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tag-vuex {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
}

.tag-ui {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

/* ===== 快速链接 ===== */
.quick-links {
  list-style: none;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.quick-link-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  text-decoration: none;
  color: #1e293b;
  transition: all 0.25s ease;
}

.quick-link-card:hover {
  background: #eff6ff;
  border-color: #93c5fd;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.1);
}

.quick-link-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.quick-link-card div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.quick-link-card strong {
  font-size: 15px;
  font-weight: 600;
}

.quick-link-card small {
  font-size: 13px;
  color: #64748b;
  font-weight: 400;
}
</style>
