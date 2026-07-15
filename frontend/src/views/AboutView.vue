<template>
  <div class="about">
    <div class="about-card">
      <div class="about-header">
        <span class="about-icon">ℹ️</span>
        <h1>关于益陪</h1>
      </div>
      <p class="about-desc">
        益陪是一个专业的智慧养老陪诊平台，致力于为老年人提供优质、贴心的陪诊服务。我们通过严格的审核机制，确保每一位陪诊师都具备专业素养和真诚关怀之心。
      </p>
      <div class="about-values">
        <div class="value-item">
          <span class="value-icon">💚</span>
          <h4>关爱</h4>
          <p>以爱心对待每一位长者</p>
        </div>
        <div class="value-item">
          <span class="value-icon">🛡️</span>
          <h4>可靠</h4>
          <p>严格审核，值得信赖</p>
        </div>
        <div class="value-item">
          <span class="value-icon">🤝</span>
          <h4>专业</h4>
          <p>持证上岗，技能过硬</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about {
  max-width: 800px;
  margin: 0 auto;
}

.about-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 48px 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
}

.about-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.about-icon {
  font-size: 36px;
}

.about-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.about-desc {
  font-size: 16px;
  line-height: 1.8;
  color: #475569;
  margin-bottom: 36px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f1f5f9;
}

.about-values {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.value-item {
  text-align: center;
  padding: 24px 16px;
  background: #f8fafc;
  border-radius: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.value-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.value-icon {
  font-size: 36px;
  display: block;
  margin-bottom: 12px;
}

.value-item h4 {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
}

.value-item p {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}
</style>
