<template>
  <div class="frosted-page">
    <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:24px">
      <div>
        <h2 class="frosted-title">平台统计</h2>
        <p class="frosted-subtitle">查看平台用户、订单、投诉和收入概况</p>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round :loading="loading" @click="fetchStats">刷新</el-button>
    </div>

    <div class="stat-grid" v-loading="loading">
      <div v-for="item in cards" :key="item.key" class="stat-item">
        <div :class="['sc-icon', item.iconClass]"><i :class="item.icon"></i></div>
        <div class="sc-body">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <div class="frosted-card" style="padding:20px 24px;margin-top:14px">
      <h3 style="font-size:16px;font-weight:700;color:rgba(78,106,56,0.9);margin:0 0 14px">快捷操作</h3>
      <div class="entry-grid">
        <div class="entry-card" @click="$router.push('/admin/users')"><h3>用户管理</h3><p>管理平台用户账号</p></div>
        <div class="entry-card" @click="$router.push('/admin/companion-review')"><h3>陪诊师审核</h3><p>审核陪诊师入驻申请</p></div>
        <div class="entry-card" @click="$router.push('/admin/orders')"><h3>订单管理</h3><p>查看所有订单进度</p></div>
        <div class="entry-card" @click="$router.push('/admin/reports')"><h3>投诉处理</h3><p>处理用户投诉举报</p></div>
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
.sc-icon { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 19px; flex-shrink: 0; }
.sc-icon--green { background: rgba(122,154,126,.12); color: #5C7A60; }
.sc-icon--coffee { background: rgba(196,168,130,.12); color: #8B7355; }
.sc-icon--blue { background: rgba(100,130,180,.1); color: #5577AA; }
.sc-icon--red { background: rgba(212,120,110,.1); color: #C05050; }
.sc-body { display: flex; flex-direction: column; min-width: 0; align-items: center; }
</style>
