<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">{{ greeting }}</p>
        <h1>{{ userNickname }}{{ text.welcomeSuffix }}</h1>
        <p>{{ text.heroDesc }}</p>
      </div>
      <div class="hero-stats">
        <div v-for="item in statItems" :key="item.label" class="stat-card">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </section>

    <section class="quick-section">
      <div class="section-head">
        <p>Quick Access</p>
        <h2>{{ text.quickTitle }}</h2>
      </div>
      <div class="quick-grid">
        <button v-for="item in quickItems" :key="item.path" type="button" class="quick-card" @click="$router.push(item.path)">
          <span :class="['quick-icon', item.tone]"><i :class="item.icon"></i></span>
          <span class="quick-info">
            <strong>{{ item.title }}</strong>
            <small>{{ item.desc }}</small>
          </span>
          <i class="el-icon-arrow-right quick-arrow"></i>
        </button>
      </div>
    </section>
  </div>
</template>

<script>
import { getUser, ROLES } from '@/utils/auth'
import { getStatistics } from '@/api/admin'
import { getMyOrders } from '@/api/order'

const T = {
  user: '\u7528\u6237',
  night: '\u591c\u95f4\u503c\u5b88',
  morning: '\u4e0a\u5348\u597d',
  afternoon: '\u4e0b\u5348\u597d',
  evening: '\u665a\u4e0a\u597d',
  welcomeSuffix: '\uff0c\u6b22\u8fce\u56de\u5230\u76ca\u966a',
  heroDesc: '\u5728\u8fd9\u91cc\u67e5\u770b\u9700\u6c42\u3001\u8ba2\u5355\u548c\u966a\u8bca\u5e08\u72b6\u6001\uff0c\u4fdd\u6301\u6bcf\u4e00\u6b21\u966a\u4f34\u90fd\u6e05\u6670\u53ef\u63a7\u3002',
  quickTitle: '\u5e38\u7528\u5165\u53e3',
  todayOrders: '\u4eca\u65e5\u8ba2\u5355',
  onlineCompanions: '\u5728\u7ebf\u966a\u8bca\u5e08',
  servedFamilies: '\u670d\u52a1\u5bb6\u5ead',
  findCompanion: '\u627e\u966a\u8bca\u5e08',
  findCompanionDesc: '\u6d4f\u89c8\u53ef\u9884\u7ea6\u7684\u4e13\u4e1a\u966a\u8bca\u5e08',
  createRequest: '\u53d1\u5e03\u9700\u6c42',
  createRequestDesc: '\u586b\u5199\u966a\u8bca\u9700\u6c42\u5e76\u5feb\u901f\u5339\u914d',
  myRequests: '\u6211\u7684\u9700\u6c42',
  myRequestsDesc: '\u67e5\u770b\u5df2\u53d1\u5e03\u7684\u670d\u52a1\u9700\u6c42',
  myOrders: '\u6211\u7684\u8ba2\u5355',
  myOrdersDesc: '\u8ddf\u8e2a\u8ba2\u5355\u72b6\u6001\u548c\u670d\u52a1\u8fdb\u5ea6',
  profile: '\u5165\u9a7b\u8d44\u6599',
  profileDesc: '\u5b8c\u5584\u8d44\u6599\u5e76\u5c55\u793a\u4e13\u4e1a\u80fd\u529b',
  availableOrders: '\u53ef\u63a5\u8ba2\u5355',
  availableOrdersDesc: '\u6d4f\u89c8\u5e76\u627f\u63a5\u5408\u9002\u7684\u966a\u8bca\u8ba2\u5355',
  serviceRecords: '\u670d\u52a1\u8bb0\u5f55',
  serviceRecordsDesc: '\u67e5\u770b\u5df2\u5b8c\u6210\u7684\u670d\u52a1\u5386\u53f2',
  userManage: '\u7528\u6237\u7ba1\u7406',
  userManageDesc: '\u7ba1\u7406\u7528\u6237\u8d26\u53f7\u548c\u72b6\u6001',
  companionReview: '\u966a\u8bca\u5e08\u5ba1\u6838',
  companionReviewDesc: '\u5ba1\u6838\u966a\u8bca\u5e08\u5165\u9a7b\u7533\u8bf7',
  orderManage: '\u8ba2\u5355\u7ba1\u7406',
  orderManageDesc: '\u67e5\u770b\u548c\u7ba1\u63a7\u5e73\u53f0\u8ba2\u5355',
  reportManage: '\u6295\u8bc9\u5904\u7406',
  reportManageDesc: '\u5904\u7406\u7528\u6237\u6295\u8bc9\u548c\u53cd\u9988',
  requestManage: '\u9700\u6c42\u7ba1\u7406',
  requestManageDesc: '\u67e5\u770b\u6240\u6709\u670d\u52a1\u9700\u6c42',
  statistics: '\u5e73\u53f0\u7edf\u8ba1',
  statisticsDesc: '\u67e5\u770b\u5e73\u53f0\u8fd0\u8425\u6570\u636e\u6982\u89c8'
}

const QUICK_MAP = {
  CUSTOMER: [
    { title: T.findCompanion, desc: T.findCompanionDesc, path: '/customer/companions', icon: 'el-icon-s-custom', tone: 'tone-green' },
    { title: T.createRequest, desc: T.createRequestDesc, path: '/customer/request/create', icon: 'el-icon-edit-outline', tone: 'tone-gold' },
    { title: T.myRequests, desc: T.myRequestsDesc, path: '/customer/requests', icon: 'el-icon-document', tone: 'tone-green' },
    { title: T.myOrders, desc: T.myOrdersDesc, path: '/customer/orders', icon: 'el-icon-s-order', tone: 'tone-gold' }
  ],
  COMPANION: [
    { title: T.profile, desc: T.profileDesc, path: '/companion/profile', icon: 'el-icon-postcard', tone: 'tone-green' },
    { title: T.availableOrders, desc: T.availableOrdersDesc, path: '/companion/available-orders', icon: 'el-icon-s-claim', tone: 'tone-gold' },
    { title: T.myOrders, desc: T.myOrdersDesc, path: '/companion/orders', icon: 'el-icon-s-order', tone: 'tone-green' },
    { title: T.serviceRecords, desc: T.serviceRecordsDesc, path: '/companion/service-records', icon: 'el-icon-tickets', tone: 'tone-gold' }
  ],
  ADMIN: [
    { title: T.userManage, desc: T.userManageDesc, path: '/admin/users', icon: 'el-icon-user', tone: 'tone-green' },
    { title: T.companionReview, desc: T.companionReviewDesc, path: '/admin/companion-review', icon: 'el-icon-finished', tone: 'tone-gold' },
    { title: T.orderManage, desc: T.orderManageDesc, path: '/admin/orders', icon: 'el-icon-s-order', tone: 'tone-green' },
    { title: T.reportManage, desc: T.reportManageDesc, path: '/admin/reports', icon: 'el-icon-warning-outline', tone: 'tone-gold' },
    { title: T.requestManage, desc: T.requestManageDesc, path: '/admin/requests', icon: 'el-icon-document-copy', tone: 'tone-green' },
    { title: T.statistics, desc: T.statisticsDesc, path: '/admin/statistics', icon: 'el-icon-data-line', tone: 'tone-gold' }
  ]
}

export default {
  name: 'Home',
  data() {
    const user = getUser() || {}
    return {
      text: T,
      userNickname: user.nickname || user.username || T.user,
      userRole: user.role || ROLES.CUSTOMER,
      greeting: this.getGreeting(),
      stats: { orders: '-', companions: '-', families: '-' }
    }
  },
  computed: {
    quickItems() {
      return QUICK_MAP[this.userRole] || QUICK_MAP.CUSTOMER
    },
    statItems() {
      return [
        { label: T.todayOrders, value: this.stats.orders },
        { label: T.onlineCompanions, value: this.stats.companions },
        { label: T.servedFamilies, value: this.formatNumber(this.stats.families) }
      ]
    }
  },
  created() { this.fetchStats() },
  methods: {
    async fetchStats() {
      try {
        if (this.userRole === ROLES.ADMIN) {
          const res = await getStatistics()
          const d = res.data || res
          this.stats = {
            orders: d.totalOrders || 0,
            companions: d.totalCompanions || 0,
            families: d.totalUsers || 0
          }
        } else {
          const res = await getMyOrders()
          const list = res.data || res || []
          this.stats = {
            orders: Array.isArray(list) ? list.length : 0,
            companions: '-',
            families: '-'
          }
        }
      } catch { /* keep defaults */ }
    },
    getGreeting() {
      const hour = new Date().getHours()
      if (hour < 6) return T.night
      if (hour < 12) return T.morning
      if (hour < 18) return T.afternoon
      return T.evening
    },
    formatNumber(value) {
      if (value >= 10000) return `${(value / 10000).toFixed(1)}\u4e07`
      return value
    }
  }
}
</script>

<style scoped>
.home-page { width: min(1180px, 100%); padding: 32px; color: var(--brand-cream-100); animation: reveal-up 0.7s var(--ease-smooth) both; }
.hero-section { display: flex; justify-content: space-between; align-items: center; background: rgba(255, 248, 238, 0.94); border: 1px solid rgba(230, 200, 160, 0.1); border-radius: 14px; padding: 40px 48px; margin-bottom: 32px; box-shadow: 0 16px 40px rgba(7, 16, 7, 0.16); position: relative; overflow: hidden; }
.hero-section::after { content: ''; position: absolute; right: -60px; top: -60px; width: 240px; height: 240px; background: radial-gradient(circle, rgba(122, 154, 126, 0.06) 0%, transparent 70%); border-radius: 50%; pointer-events: none; }
.hero-copy { position: relative; z-index: 1; }
.eyebrow { color: #7a9a7e; font-size: 13px; font-weight: 650; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.08em; }
.hero-copy h1 { font-size: 30px; font-weight: 700; color: #1a1a1a; margin: 0 0 8px; font-family: var(--font-sans); }
.hero-copy p:last-child { font-size: 15px; color: #6b6b6b; }
.hero-stats { display: flex; gap: 40px; position: relative; z-index: 1; }
.stat-card { text-align: center; padding: 0 24px; border-right: 1px solid rgba(0,0,0,0.08); }
.stat-card:last-child { border-right: none; }
.stat-value { display: block; font-size: 28px; font-weight: 700; color: #5c7a60; letter-spacing: -0.02em; }
.stat-label { display: block; font-size: 13px; color: #888; margin-top: 4px; }
.quick-section { margin-top: 30px; }
.section-head { margin-bottom: 16px; }
.section-head p { color: rgba(210, 190, 160, 0.56); font-size: 12px; font-weight: 800; margin-bottom: 4px; }
.section-head h2 { font-family: var(--font-serif); font-size: 26px; color: var(--brand-cream-100); }
.quick-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 14px; }
.quick-card { min-height: 112px; display: flex; align-items: center; gap: 15px; padding: 20px; border: 1px solid rgba(230, 200, 160, 0.14); border-radius: 10px; background: rgba(255, 248, 238, 0.94); color: #172615; cursor: pointer; text-align: left; box-shadow: 0 14px 34px rgba(7, 16, 7, 0.14); transition: transform 0.22s var(--ease-standard), border-color 0.22s var(--ease-standard), box-shadow 0.22s var(--ease-standard); }
.quick-card:hover { transform: translateY(-3px); border-color: rgba(225, 195, 160, 0.46); box-shadow: 0 20px 44px rgba(7, 16, 7, 0.2); }
.quick-icon { width: 46px; height: 46px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-size: 20px; flex: 0 0 46px; }
.tone-green { background: rgba(45, 90, 58, 0.12); color: #2d5a3a; }
.tone-gold { background: rgba(225, 195, 160, 0.24); color: #7c5c38; }
.quick-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.quick-info strong { color: #172615; font-size: 15px; }
.quick-info small { color: #555; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.quick-arrow { color: rgba(23, 38, 21, 0.28); transition: transform 0.22s var(--ease-standard), color 0.22s var(--ease-standard); }
.quick-card:hover .quick-arrow { color: #7c5c38; transform: translateX(3px); }
@media (max-width: 920px) { .home-page { padding: 18px; } .hero-section { flex-direction: column; text-align: center; gap: 28px; padding: 32px 24px; } .hero-copy h1 { font-size: 26px; } .hero-stats { gap: 20px; } .stat-card { padding: 0 14px; } }
@media (max-width: 560px) { .hero-stats { flex-direction: column; } .stat-card { border-right: none; border-bottom: 1px solid rgba(0,0,0,0.08); padding: 12px 0; } .stat-card:last-child { border-bottom: none; } .quick-grid { grid-template-columns: 1fr; } }
</style>
