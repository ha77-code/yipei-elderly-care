<template>
  <div class="page-wrap">
    <div class="page-head">
      <div>
        <h2 class="page-title">平台统计</h2>
        <p class="page-subtitle">查看平台用户、订单、投诉和模拟收入情况</p>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round :loading="loading" @click="fetchStats">刷新</el-button>
    </div>

    <div class="stat-cards" v-loading="loading">
      <div v-for="item in cards" :key="item.key" class="stat-card">
        <div :class="['sc-icon', item.iconClass]"><i :class="item.icon"></i></div>
        <div class="sc-body">
          <span class="sc-value">{{ item.value }}</span>
          <span class="sc-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

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

const DEFAULT_STATS = {
  totalUsers: 0,
  totalCompanions: 0,
  totalOrders: 0,
  completedOrders: 0,
  complaintCount: 0,
  platformRevenue: 0
}

export default {
  name: 'AdminStatistics',
  data() {
    return {
      loading: false,
      stats: { ...DEFAULT_STATS }
    }
  },
  computed: {
    cards() {
      return [
        { key: 'totalUsers', label: '用户数量', value: this.formatNumber(this.stats.totalUsers), icon: 'el-icon-user', iconClass: 'sc-icon--green' },
        { key: 'totalCompanions', label: '陪诊师数量', value: this.formatNumber(this.stats.totalCompanions), icon: 'el-icon-s-custom', iconClass: 'sc-icon--coffee' },
        { key: 'totalOrders', label: '订单数量', value: this.formatNumber(this.stats.totalOrders), icon: 'el-icon-s-order', iconClass: 'sc-icon--blue' },
        { key: 'completedOrders', label: '完成订单数量', value: this.formatNumber(this.stats.completedOrders), icon: 'el-icon-circle-check', iconClass: 'sc-icon--green' },
        { key: 'complaintCount', label: '投诉数量', value: this.formatNumber(this.stats.complaintCount), icon: 'el-icon-warning-outline', iconClass: 'sc-icon--red' },
        { key: 'platformRevenue', label: '平台模拟收入', value: this.formatMoney(this.stats.platformRevenue), icon: 'el-icon-money', iconClass: 'sc-icon--coffee' }
      ]
    }
  },
  created() {
    this.fetchStats()
  },
  methods: {
    async fetchStats() {
      this.loading = true
      try {
        const res = await getStatistics()
        const data = res.data || res || {}
        this.stats = {
          totalUsers: data.totalUsers || data.userCount || 0,
          totalCompanions: data.totalCompanions || data.companionCount || 0,
          totalOrders: data.totalOrders || data.orderCount || 0,
          completedOrders: data.completedOrders || data.completedOrderCount || 0,
          complaintCount: data.complaintCount || data.totalReports || data.pendingReports || 0,
          platformRevenue: data.platformRevenue || data.totalRevenue || data.revenue || 0
        }
      } catch (error) {
        this.stats = { ...DEFAULT_STATS }
      } finally {
        this.loading = false
      }
    },
    formatNumber(value) {
      return Number(value || 0).toLocaleString('zh-CN')
    },
    formatMoney(value) {
      return `￥${Number(value || 0).toFixed(2)}`
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0; }
.page-subtitle { margin: 6px 0 0; font-size: 13px; color: var(--color-text-secondary); }
.stat-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; margin-bottom: 32px; min-height: 160px; }
.stat-card { display: flex; align-items: center; gap: 16px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px 24px; box-shadow: var(--shadow-sm); }
.sc-icon { width: 48px; height: 48px; border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: center; font-size: 22px; flex-shrink: 0; }
.sc-icon--green { background: rgba(122,154,126,.12); color: var(--color-primary-dark); }
.sc-icon--coffee { background: rgba(196,168,130,.12); color: var(--color-deep-coffee); }
.sc-icon--blue { background: rgba(100,130,180,.10); color: #5577AA; }
.sc-icon--red { background: rgba(224,96,96,.08); color: #C05050; }
.sc-body { display: flex; flex-direction: column; min-width: 0; }
.sc-value { font-family: var(--font-family); font-size: 22px; font-weight: 700; color: var(--color-text-primary); line-height: 1.25; }
.sc-label { font-size: 13px; color: var(--color-text-placeholder); margin-top: 2px; }
.section { margin-top: 8px; }
.section-title { font-family: var(--font-family); font-size: 16px; font-weight: 600; color: var(--color-text-primary); margin: 0 0 16px; }
.quick-links { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 12px; }
.ql-card { display: flex; align-items: center; gap: 10px; padding: 16px 20px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); cursor: pointer; transition: all .2s; font-size: 14px; font-weight: 500; color: var(--color-text-primary); box-shadow: var(--shadow-sm); }
.ql-card:hover { border-color: var(--color-primary-light); transform: translateY(-2px); box-shadow: var(--shadow-md); }
.ql-card i { font-size: 20px; color: var(--color-primary); }
</style>