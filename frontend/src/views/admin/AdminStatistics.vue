<template>
  <div class="page-wrap">
    <div class="page-head">
      <div>
        <h2 class="page-title">平台统计</h2>
        <p class="page-subtitle">查看平台用户、订单、投诉和收入概况</p>
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
        <div class="ql-card" @click="$router.push('/admin/dashboard')">
          <i class="el-icon-s-home"></i><span>后台首页</span>
        </div>
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
  totalUsers: 0, totalCompanions: 0, pendingAuditCount: 0,
  totalOrders: 0, completedOrders: 0, pendingAcceptOrders: 0, inServiceOrders: 0,
  complaintCount: 0, pendingComplaintCount: 0,
  totalRevenue: 0, platformRevenue: 0, companionIncome: 0
}

export default {
  name: 'AdminStatistics',
  data() {
    return { loading: false, stats: { ...DEFAULT_STATS } }
  },
  computed: {
    cards() {
      return [
        { key: 'totalUsers', label: '注册用户', value: this.fmt(this.stats.totalUsers), icon: 'el-icon-user', iconClass: 'sc-icon--green' },
        { key: 'totalCompanions', label: '陪诊师', value: this.fmt(this.stats.totalCompanions), icon: 'el-icon-s-custom', iconClass: 'sc-icon--coffee' },
        { key: 'pendingAuditCount', label: '待审核', value: this.fmt(this.stats.pendingAuditCount), icon: 'el-icon-finished', iconClass: 'sc-icon--red' },
        { key: 'totalOrders', label: '总订单', value: this.fmt(this.stats.totalOrders), icon: 'el-icon-s-order', iconClass: 'sc-icon--blue' },
        { key: 'pendingAcceptOrders', label: '待接单', value: this.fmt(this.stats.pendingAcceptOrders), icon: 'el-icon-s-claim', iconClass: 'sc-icon--green' },
        { key: 'inServiceOrders', label: '服务中', value: this.fmt(this.stats.inServiceOrders), icon: 'el-icon-loading', iconClass: 'sc-icon--coffee' },
        { key: 'completedOrders', label: '已完成', value: this.fmt(this.stats.completedOrders), icon: 'el-icon-circle-check', iconClass: 'sc-icon--green' },
        { key: 'complaintCount', label: '投诉总数', value: this.fmt(this.stats.complaintCount), icon: 'el-icon-warning-outline', iconClass: 'sc-icon--red' },
        { key: 'pendingComplaintCount', label: '待处理投诉', value: this.fmt(this.stats.pendingComplaintCount), icon: 'el-icon-bell', iconClass: 'sc-icon--red' },
        { key: 'totalRevenue', label: '订单总额', value: this.money(this.stats.totalRevenue), icon: 'el-icon-money', iconClass: 'sc-icon--coffee' },
        { key: 'platformRevenue', label: '平台收入', value: this.money(this.stats.platformRevenue), icon: 'el-icon-s-finance', iconClass: 'sc-icon--green' },
        { key: 'companionIncome', label: '陪诊师收入', value: this.money(this.stats.companionIncome), icon: 'el-icon-s-cooperation', iconClass: 'sc-icon--blue' }
      ]
    }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() {
      this.loading = true
      try {
        const res = await getStatistics()
        const data = res.data || res || {}
        this.stats = {
          totalUsers: data.totalUsers || 0,
          totalCompanions: data.totalCompanions || 0,
          pendingAuditCount: data.pendingAuditCount || 0,
          totalOrders: data.totalOrders || 0,
          completedOrders: data.completedOrders || 0,
          pendingAcceptOrders: data.pendingAcceptOrders || 0,
          inServiceOrders: data.inServiceOrders || 0,
          complaintCount: data.complaintCount || 0,
          pendingComplaintCount: data.pendingComplaintCount || 0,
          totalRevenue: data.totalRevenue || 0,
          platformRevenue: data.platformRevenue || 0,
          companionIncome: data.companionIncome || 0
        }
      } catch { this.stats = { ...DEFAULT_STATS } } finally { this.loading = false }
    },
    fmt(v) { return Number(v || 0).toLocaleString('zh-CN') },
    money(v) { return `￥${Number(v || 0).toFixed(2)}` }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0; }
.page-subtitle { margin: 6px 0 0; font-size: 13px; color: var(--color-text-secondary); }
.stat-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(185px, 1fr)); gap: 12px; margin-bottom: 32px; }
.stat-card {
  display: flex; align-items: center; gap: 14px;
  background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md);
  padding: 18px 20px; box-shadow: var(--shadow-xs);
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1); cursor: default;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); border-color: rgba(122,154,126,0.1); }
.sc-icon { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 19px; flex-shrink: 0; }
.sc-icon--green { background: rgba(122,154,126,.1); color: #5C7A60; }
.sc-icon--coffee { background: rgba(196,168,130,.1); color: #8B7355; }
.sc-icon--blue { background: rgba(100,130,180,.08); color: #5577AA; }
.sc-icon--red { background: rgba(212,120,110,.08); color: #C05050; }
.sc-body { display: flex; flex-direction: column; min-width: 0; }
.sc-value { font-size: 20px; font-weight: 700; color: var(--color-text-primary); letter-spacing: -0.02em; }
.sc-label { font-size: 12px; color: var(--color-text-placeholder); margin-top: 2px; font-weight: 500; }
.section { margin-top: 8px; }
.section-title { font-family: var(--font-family); font-size: 16px; font-weight: 600; color: var(--color-text-primary); margin: 0 0 14px; }
.quick-links { display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: 10px; }
.ql-card { display: flex; align-items: center; gap: 10px; padding: 14px 18px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); cursor: pointer; transition: all .2s; font-size: 14px; font-weight: 500; color: var(--color-text-primary); box-shadow: var(--shadow-sm); }
.ql-card:hover { border-color: var(--color-primary-light); transform: translateY(-2px); box-shadow: var(--shadow-md); }
.ql-card i { font-size: 18px; color: var(--color-primary); }
</style>
