<template>
  <div class="not-found-page bg-warm-gradient">
    <div class="not-found-content">
      <div class="not-found-code">404</div>
      <h1 class="not-found-title">页面未找到</h1>
      <p class="not-found-desc">您访问的页面不存在或已被移除</p>
      <el-button type="primary" size="large" round @click="goHome">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goHome() {
      this.$router.replace('/')
    }
  }
}
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.not-found-content {
  text-align: center;
  padding: 60px 40px;
}

.not-found-code {
  font-family: var(--font-family);
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  color: var(--color-primary);
  opacity: 0.3;
  letter-spacing: -0.03em;
}

.not-found-title {
  font-family: var(--font-family);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 16px 0 12px;
}

.not-found-desc {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
}
</style>
