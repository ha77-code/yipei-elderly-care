<template>
  <div class="page-wrap">
    <h2 class="page-title">平台统计</h2>

    <!-- 概览卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="sc-icon sc-icon--green"><i class="el-icon-user"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ stats.totalUsers }}</span>
          <span class="sc-label">注册用户</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="sc-icon sc-icon--coffee"><i class="el-icon-s-custom"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ stats.totalCompanions }}</span>
          <span class="sc-label">陪诊师</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="sc-icon sc-icon--green"><i class="el-icon-s-order"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ stats.totalOrders }}</span>
          <span class="sc-label">总订单</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="sc-icon sc-icon--coffee"><i class="el-icon-data-line"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ stats.completedOrders }}</span>
          <span class="sc-label">已完成</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="sc-icon sc-icon--green"><i class="el-icon-warning-outline"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ stats.pendingReports }}</span>
          <span class="sc-label">待处理投诉</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="sc-icon sc-icon--coffee"><i class="el-icon-money"></i></div>
        <div class="sc-body">
          <span class="sc-value">¥{{ stats.totalRevenue }}</span>
          <span class="sc-label">平台收入</span>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="section">
      <h3 class="section-title">快捷操作</h3>
      <div class="quick-links">
        <div class="ql-card" @click="$router.push('/admin/users')">
          <i class="el-icon-user"></i><span>用户管理</span>
        </div>
        <div class="ql-card" @click="$router.push('/admin/companion-review')">
          <i class="el-icon-finished"></i><span>陪诊师审核</span>
        </div>
        <div class="ql-card" @click="$router.push('/admin/orders')">
          <i class="el-icon-s-order"></i><span>订单管理</span>
        </div>
        <div class="ql-card" @click="$router.push('/admin/reports')">
          <i class="el-icon-warning-outline"></i><span>投诉处理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getStatistics } from '@/api/admin'

export default {
  name: 'AdminStatistics',
  data() {
    return {
      stats: { totalUsers: 0, totalCompanions: 0, totalOrders: 0, completedOrders: 0, pendingReports: 0, totalRevenue: 0 }
    }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() {
      try {
        const res = await getStatistics()
        const d = res.data || res
        if (d) this.stats = { ...this.stats, ...d }
      } catch { /* 后端未就绪时显示默认值 */ }
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 24px; }

/* 统计卡片 */
.stat-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; margin-bottom: 32px; }
.stat-card { display: flex; align-items: center; gap: 16px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px 24px; box-shadow: var(--shadow-sm); }
.sc-icon { width: 48px; height: 48px; border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: center; font-size: 22px; flex-shrink: 0; }
.sc-icon--green { background: rgba(122,154,126,.12); color: var(--color-primary-dark); }
.sc-icon--coffee { background: rgba(196,168,130,.12); color: var(--color-deep-coffee); }
.sc-body { display: flex; flex-direction: column; }
.sc-value { font-family: var(--font-family); font-size: 22px; font-weight: 700; color: var(--color-text-primary); }
.sc-label { font-size: 13px; color: var(--color-text-placeholder); margin-top: 2px; }

/* 快捷操作 */
.section { margin-top: 8px; }
.section-title { font-family: var(--font-family); font-size: 16px; font-weight: 600; color: var(--color-text-primary); margin: 0 0 16px; }
.quick-links { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 12px; }
.ql-card { display: flex; align-items: center; gap: 10px; padding: 16px 20px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); cursor: pointer; transition: all .2s; font-size: 14px; font-weight: 500; color: var(--color-text-primary); box-shadow: var(--shadow-sm); }
.ql-card:hover { border-color: var(--color-primary-light); transform: translateY(-2px); box-shadow: var(--shadow-md); }
.ql-card i { font-size: 20px; color: var(--color-primary); }
</style>
