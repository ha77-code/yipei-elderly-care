<template>
  <div class="dashboard-page">
    <div class="page-head">
      <div>
        <h2 class="page-title">后台管理</h2>
        <p class="page-subtitle">欢迎使用益陪养老管理后台</p>
      </div>
    </div>

    <!-- 概览卡片 -->
    <div class="stat-row" v-loading="loading">
      <div class="mini-stat" v-for="s in summary" :key="s.key">
        <span class="mini-value">{{ s.value }}</span>
        <span class="mini-label">{{ s.label }}</span>
      </div>
    </div>

    <!-- 管理入口 -->
    <div class="section">
      <h3 class="section-title">管理功能</h3>
      <div class="menu-grid">
        <div class="menu-card" @click="$router.push('/admin/users')">
          <div class="mc-icon mc-icon--green"><i class="el-icon-user"></i></div>
          <div class="mc-body">
            <h4>用户管理</h4>
            <p>查看和管理系统用户，启用或禁用账号</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
        <div class="menu-card" @click="$router.push('/admin/companion-review')">
          <div class="mc-icon mc-icon--coffee"><i class="el-icon-finished"></i></div>
          <div class="mc-body">
            <h4>陪诊师审核</h4>
            <p>审核陪诊师入驻申请，通过或拒绝</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
        <div class="menu-card" @click="$router.push('/admin/orders')">
          <div class="mc-icon mc-icon--blue"><i class="el-icon-s-order"></i></div>
          <div class="mc-body">
            <h4>订单管理</h4>
            <p>查看所有订单，按状态筛选和跟踪</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
        <div class="menu-card" @click="$router.push('/admin/requests')">
          <div class="mc-icon mc-icon--green"><i class="el-icon-document-copy"></i></div>
          <div class="mc-body">
            <h4>需求管理</h4>
            <p>查看所有服务需求发布情况</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
        <div class="menu-card" @click="$router.push('/admin/reports')">
          <div class="mc-icon mc-icon--red"><i class="el-icon-warning-outline"></i></div>
          <div class="mc-body">
            <h4>投诉处理</h4>
            <p>处理用户投诉，维护平台秩序</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
        <div class="menu-card" @click="$router.push('/admin/statistics')">
          <div class="mc-icon mc-icon--coffee"><i class="el-icon-data-line"></i></div>
          <div class="mc-body">
            <h4>平台统计</h4>
            <p>查看运营数据和收入概览</p>
          </div>
          <i class="el-icon-arrow-right mc-arrow"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getStatistics } from '@/api/admin'

export default {
  name: 'AdminDashboard',
  data() {
    return { loading: false, stats: {} }
  },
  computed: {
    summary() {
      return [
        { key: 'users', label: '注册用户', value: this.fmt(this.stats.totalUsers) },
        { key: 'companions', label: '陪诊师', value: this.fmt(this.stats.totalCompanions) },
        { key: 'pending', label: '待审核', value: this.fmt(this.stats.pendingAuditCount) },
        { key: 'orders', label: '总订单', value: this.fmt(this.stats.totalOrders) },
        { key: 'complaints', label: '待处理投诉', value: this.fmt(this.stats.pendingComplaintCount) },
        { key: 'revenue', label: '平台收入', value: this.money(this.stats.platformRevenue) }
      ]
    }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() {
      this.loading = true
      try {
        const res = await getStatistics()
        this.stats = res.data || res || {}
      } catch { this.stats = {} } finally { this.loading = false }
    },
    fmt(v) { return Number(v || 0).toLocaleString('zh-CN') },
    money(v) { return `￥${Number(v || 0).toFixed(2)}` }
  }
}
</script>

<style scoped>
.dashboard-page { padding: 32px 36px; max-width: 1100px; }
.page-head { margin-bottom: 28px; }
.page-title { font-size: 22px; font-weight: 700; color: var(--color-text-primary); margin: 0; }
.page-subtitle { margin: 4px 0 0; font-size: 13px; color: var(--color-text-secondary); }

.stat-row { display: grid; grid-template-columns: repeat(auto-fill, minmax(155px, 1fr)); gap: 12px; margin-bottom: 36px; min-height: 60px; }
.mini-stat {
  background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md);
  padding: 20px 18px; text-align: center; box-shadow: var(--shadow-xs);
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1); cursor: default;
}
.mini-stat:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); border-color: rgba(122,154,126,0.12); }
.mini-value { display: block; font-size: 24px; font-weight: 700; color: var(--color-text-primary); letter-spacing: -0.02em; }
.mini-label { display: block; font-size: 12px; color: var(--color-text-placeholder); margin-top: 6px; font-weight: 500; }

.section-title { font-size: 17px; font-weight: 650; color: var(--color-text-primary); margin: 0 0 14px; }
.menu-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 12px; }
.menu-card { display: flex; align-items: center; gap: 16px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px 24px; cursor: pointer; transition: all .2s; box-shadow: var(--shadow-sm); }
.menu-card:hover { border-color: var(--color-primary-light); transform: translateY(-2px); box-shadow: var(--shadow-md); }
.mc-icon { width: 44px; height: 44px; border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.mc-icon--green { background: rgba(122,154,126,.12); color: #5C7A60; }
.mc-icon--coffee { background: rgba(196,168,130,.12); color: #8B7355; }
.mc-icon--blue { background: rgba(100,130,180,.10); color: #5577AA; }
.mc-icon--red { background: rgba(224,96,96,.08); color: #C05050; }
.mc-body { flex: 1; min-width: 0; }
.mc-body h4 { font-family: var(--font-family); font-size: 15px; font-weight: 600; color: var(--color-text-primary); margin: 0 0 4px; }
.mc-body p { font-size: 13px; color: var(--color-text-placeholder); margin: 0; }
.mc-arrow { font-size: 16px; color: var(--color-border); flex-shrink: 0; }
.menu-card:hover .mc-arrow { color: var(--color-primary); }
</style>
