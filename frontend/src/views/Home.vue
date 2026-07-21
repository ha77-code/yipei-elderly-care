<template>
  <div class="frosted-page">
    <section class="frosted-card" style="display:flex;justify-content:space-between;align-items:center;padding:36px 44px;margin-bottom:24px">
      <div>
        <p style="color:rgba(130,140,116,0.8);font-size:13px;font-weight:600;margin-bottom:6px">{{ greeting }}</p>
        <h1 style="font-size:26px;font-weight:700;color:rgba(46,60,38,0.92);margin:0 0 6px;font-family:'PingFang SC','Microsoft YaHei',sans-serif">{{ userNickname }}{{ text.welcomeSuffix }}</h1>
        <p style="font-size:14px;color:rgba(96,110,82,0.75);margin:0">{{ text.heroDesc }}</p>
      </div>
      <div style="display:flex;gap:36px">
        <div v-for="item in statItems" :key="item.label" style="text-align:center;padding:0 20px;border-right:1px solid rgba(150,140,110,0.15)" :style="item === statItems[statItems.length-1] ? {borderRight:'none'} : {}">
          <span style="display:block;font-size:24px;font-weight:700;color:rgba(78,106,56,0.88)">{{ item.value }}</span>
          <span style="display:block;font-size:12px;color:rgba(130,140,116,0.7);margin-top:4px">{{ item.label }}</span>
        </div>
      </div>
    </section>

    <section>
      <h2 class="frosted-title" style="margin-bottom:16px">{{ text.quickTitle }}</h2>
      <div class="entry-grid">
        <div v-for="item in quickItems" :key="item.path" class="entry-card" @click="$router.push(item.path)" style="display:flex;align-items:center;gap:14px;min-height:80px">
          <span :class="['quick-icon', item.tone]"><i :class="item.icon"></i></span>
          <span style="flex:1;min-width:0;display:flex;flex-direction:column;gap:4px">
            <strong style="color:rgba(46,60,38,0.88)">{{ item.title }}</strong>
            <small style="color:rgba(130,140,116,0.65);font-size:12px">{{ item.desc }}</small>
          </span>
          <i class="el-icon-arrow-right" style="color:rgba(150,140,110,0.35)"></i>
        </div>
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
.quick-icon { width: 46px; height: 46px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-size: 20px; flex: 0 0 46px; }
.tone-green { background: rgba(108,140,80,.14); color: rgba(78,106,56,0.85); }
.tone-gold { background: rgba(245,215,140,.22); color: rgba(170,130,60,0.9); }
@media (max-width: 920px) { .frosted-page { padding: 16px !important; } }
</style>
