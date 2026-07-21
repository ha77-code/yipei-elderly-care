<template>
  <div class="main-layout">
    <!-- 背景 -->
    <div class="bg-layer" :style="{backgroundImage: 'url(/img/bg-bamboo-soft.webp)'}"></div>
    <div class="bg-corners"></div>

    <!-- 顶栏 -->
    <header class="top-bar">
      <div class="brand-lockup" @click="goHome">
        <span class="brand-symbol">{{ text.brandSymbol }}</span>
        <span class="brand-text">{{ text.brandName }}</span>
      </div>

      <nav class="top-nav">
        <router-link v-for="item in topItems" :key="item.path"
          :to="item.path" class="nav-link"
          exact-active-class="nav-link--active">{{ item.label }}</router-link>
      </nav>

      <el-dropdown trigger="click" @command="handleUserCommand">
        <span class="user-chip">
          <el-avatar :size="32" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
          <span class="user-name">{{ userNickname }}</span>
          <span :class="['role-tag', 'role-tag--'+userRole.toLowerCase()]">{{ roleLabel }}</span>
          <i class="el-icon-arrow-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile"><i class="el-icon-user"></i> {{ text.profile }}</el-dropdown-item>
          <el-dropdown-item command="logout" divided><i class="el-icon-switch-button"></i> {{ text.logout }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </header>

    <div class="layout-body">
      <aside class="side-bar">
        <ArcNav :items="arcItems" />
      </aside>

      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { getUser, clearUser, ROLES, ROLE_LABELS } from '@/utils/auth'
import { getUnreadTotal } from '@/api/chat'
import ArcNav from '@/components/ArcNav.vue'

const T = {
  brandSymbol: '益', brandName: '益陪养老', workbench: '工作台',
  profile: '个人信息', logout: '退出登录',
  confirmTitle: '提示', confirmLogout: '确定要退出登录吗？',
  confirm: '确定', cancel: '取消', logoutDone: '已退出登录', fallbackUser: '用户',
  home: '首页', companions: '陪诊师', myRequests: '我的需求', availableOrders: '可接订单', admin: '平台管理',
  companionList: '陪诊师列表', createRequest: '发布需求', myOrders: '我的订单', messages: '聊天',
  companionProfile: '入驻资料', serviceRecords: '服务记录', myEvaluations: '我的评价', incomeStats: '收入统计',
  userManage: '用户管理', companionReview: '陪诊师审核', avatarReview: '头像审核', requestReview: '需求审核',
  requestManage: '需求管理', orderManage: '订单管理', reportManage: '投诉处理', statistics: '平台统计'
}

const TOP_MAP = {
  CUSTOMER: [
    { label: T.home, path: '/' },
    { label: T.companions, path: '/customer/companions' },
    { label: T.myRequests, path: '/customer/requests' }
  ],
  COMPANION: [
    { label: T.home, path: '/' },
    { label: T.availableOrders, path: '/companion/available-orders' }
  ],
  ADMIN: [
    { label: T.home, path: '/' },
    { label: T.admin, path: '/admin/users' }
  ]
}

const ARC_MAP = {
  CUSTOMER: [
    { label: '首页', path: '/', icon: '⌂' },
    { label: '陪诊师', path: '/customer/companions', icon: '☺' },
    { label: '订单', path: '/customer/orders', icon: '☰' },
    { label: '需求', path: '/customer/requests', icon: '✎' },
    { label: '聊天', path: '/customer/messages', icon: '💬' }
  ],
  COMPANION: [
    { label: '首页', path: '/', icon: '⌂' },
    { label: '广场', path: '/companion/pool', icon: '☑' },
    { label: '接单', path: '/companion/available-orders', icon: '☰' },
    { label: '订单', path: '/companion/orders', icon: '□' },
    { label: '聊天', path: '/companion/messages', icon: '💬' },
    { label: '记录', path: '/companion/service-records', icon: '📋' },
    { label: '评价', path: '/companion/evaluations', icon: '★' },
    { label: '收入', path: '/companion/income', icon: '◆' }
  ],
  ADMIN: [
    { label: '用户', path: '/admin/users', icon: '☺' },
    { label: '审核', path: '/admin/companion-review', icon: '✓' },
    { label: '头像', path: '/admin/avatar-review', icon: '🖼' },
    { label: '需求审', path: '/admin/request-review', icon: '✎' },
    { label: '订单', path: '/admin/orders', icon: '☰' },
    { label: '投诉', path: '/admin/reports', icon: '⚠' },
    { label: '统计', path: '/admin/statistics', icon: '◆' }
  ]
}

const SIDE_MAP = {
  CUSTOMER: [
    { label: T.companionList, path: '/customer/companions', icon: 'el-icon-s-custom' },
    { label: T.createRequest, path: '/customer/request/create', icon: 'el-icon-edit-outline' },
    { label: T.myRequests, path: '/customer/requests', icon: 'el-icon-document' },
    { label: T.myOrders, path: '/customer/orders', icon: 'el-icon-s-order' },
    { label: T.messages, path: '/customer/messages', icon: 'el-icon-chat-dot-round', badge: true }
  ],
  COMPANION: [
    { label: T.companionProfile, path: '/companion/profile', icon: 'el-icon-postcard' },
    { label: '需求广场', path: '/companion/pool', icon: 'el-icon-s-grid' },
    { label: T.availableOrders, path: '/companion/available-orders', icon: 'el-icon-s-claim' },
    { label: T.myOrders, path: '/companion/orders', icon: 'el-icon-s-order' },
    { label: T.messages, path: '/companion/messages', icon: 'el-icon-message', badge: true },
    { label: T.serviceRecords, path: '/companion/service-records', icon: 'el-icon-tickets' },
    { label: T.myEvaluations, path: '/companion/evaluations', icon: 'el-icon-chat-dot-round' },
    { label: T.incomeStats, path: '/companion/income', icon: 'el-icon-coin' }
  ],
  ADMIN: [
    { label: T.userManage, path: '/admin/users', icon: 'el-icon-user' },
    { label: T.companionReview, path: '/admin/companion-review', icon: 'el-icon-finished' },
    { label: T.avatarReview, path: '/admin/avatar-review', icon: 'el-icon-picture-outline' },
    { label: T.requestReview, path: '/admin/request-review', icon: 'el-icon-s-check' },
    { label: T.avatarReview, path: '/admin/avatar-review', icon: 'el-icon-picture-outline' },
    { label: T.requestReview, path: '/admin/request-review', icon: 'el-icon-s-check' },
    { label: T.requestManage, path: '/admin/requests', icon: 'el-icon-document-copy' },
    { label: T.orderManage, path: '/admin/orders', icon: 'el-icon-s-order' },
    { label: T.reportManage, path: '/admin/reports', icon: 'el-icon-warning-outline' },
    { label: T.statistics, path: '/admin/statistics', icon: 'el-icon-data-line' }
  ]
}

export default {
  name: 'MainLayout',
  components: { ArcNav },
  data() {
    const user = getUser() || {}
    return {
      text: T,
      userNickname: user.nickname || user.username || T.fallbackUser,
      userRole: user.role || ROLES.CUSTOMER,
      unreadTotal: 0,
      unreadTimer: null
    }
  },
  computed: {
    roleLabel() { return ROLE_LABELS[this.userRole] || ROLE_LABELS.CUSTOMER },
    activeMenu() { return this.$route.path },
    topItems() { return TOP_MAP[this.userRole] || TOP_MAP.CUSTOMER },
    sideItems() { return SIDE_MAP[this.userRole] || SIDE_MAP.CUSTOMER },
    arcItems() { return ARC_MAP[this.userRole] || ARC_MAP.CUSTOMER }
  },
  created() {
    if (this.userRole !== ROLES.ADMIN) {
      this.refreshUnread()
      this.unreadTimer = setInterval(this.refreshUnread, 15000)
    }
  },
  beforeDestroy() {
    if (this.unreadTimer) clearInterval(this.unreadTimer)
  },
  watch: {
    '$route.path'() {
      if (this.userRole !== ROLES.ADMIN) this.refreshUnread()
    }
  },
  methods: {
    async refreshUnread() {
      try {
        const res = await getUnreadTotal()
        this.unreadTotal = res.data != null ? res.data : (res || 0)
      } catch { /* 忽略 */ }
    },
    goHome() {
      const map = { CUSTOMER: '/', COMPANION: '/', ADMIN: '/admin/users' }
      this.$router.push(map[this.userRole] || '/').catch(() => {})
    },
    handleUserCommand(command) {
      if (command === 'profile') this.$router.push('/profile')
      if (command === 'logout') this.doLogout()
    },
    doLogout() {
      this.$confirm(T.confirmLogout, T.confirmTitle, { confirmButtonText: T.confirm, cancelButtonText: T.cancel, type: 'warning' })
        .then(() => { clearUser(); this.$message.success(T.logoutDone); window.location.href = '/landing.html' }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.menu-badge { margin-left: 8px; vertical-align: middle; }
.menu-badge::v-deep .el-badge__content { background-color: rgba(108,140,80,0.85); border: 1px solid rgba(108,140,80,0.3); }
/* CSS 变量（浅色主题） */
.main-layout {
  min-height:100vh; display:flex; flex-direction:column; background:var(--bg-root); position:relative; overflow:hidden;
  font-family:'PingFang SC','Microsoft YaHei',-apple-system,sans-serif; color:var(--text-primary);
  transition: background 0.4s ease;
}
/* 背景 */
.bg-layer{position:fixed;inset:-24px;z-index:0;background:center/cover no-repeat;filter:blur(8px) saturate(0.92) brightness(1.06);transform:scale(1.03);transition:filter 0.5s ease}
.bg-layer::after{content:'';position:absolute;inset:0;background:linear-gradient(155deg,rgba(255,252,240,0.15),rgba(240,246,228,0.08) 48%,rgba(224,234,208,0.2))}
/* 暗色背景：暖调森系 - 暖米渐变蒙版 + 林间光斑 */


.bg-corners{position:fixed;inset:0;z-index:1;pointer-events:none;background:radial-gradient(circle 280px at 0% 0%,rgba(255,245,210,0.18),transparent 62%),radial-gradient(circle 320px at 100% 100%,rgba(210,225,180,0.15),transparent 62%),linear-gradient(90deg,transparent 0 16%,rgba(255,225,140,0.04) 16.1% 16.25%,transparent 16.6% 36%,rgba(255,225,140,0.03) 36.1% 36.25%,transparent 36.6%),radial-gradient(ellipse at 24% 0%,rgba(240,215,150,0.16),transparent 34%)}
/* 顶栏（白瓷磨砂） */
.top-bar{height:66px;display:flex;align-items:center;justify-content:space-between;gap:24px;padding:0 28px;background:var(--topbar-bg);backdrop-filter:blur(20px);-webkit-backdrop-filter:blur(20px);border-bottom:1px solid var(--topbar-border);position:fixed;top:0;left:0;right:0;z-index:20;box-shadow:var(--topbar-shadow)}
.brand-lockup{display:flex;align-items:center;gap:12px;cursor:pointer;min-width:170px}
.brand-symbol{width:38px;height:38px;display:flex;align-items:center;justify-content:center;border-radius:10px;border:1px solid rgba(var(--brand-accent),0.35);color:var(--text-heading);font-family:'Noto Serif SC',serif;font-size:20px;font-weight:800;background:var(--bg-input)}
.brand-text{color:var(--text-heading);font-family:'Noto Serif SC',serif;font-size:18px;font-weight:800}
.top-nav{flex:1;display:flex;justify-content:center;gap:6px}
.nav-link{height:38px;display:inline-flex;align-items:center;padding:0 16px;border-radius:999px;color:var(--text-secondary);text-decoration:none;font-size:14px;font-weight:700;transition:all 0.22s ease}
.nav-link:hover,.nav-link--active{color:var(--text-heading);background:rgba(var(--brand-accent),0.1);text-shadow:0 0 14px rgba(200,210,130,0.3)}


.user-chip{display:flex;align-items:center;gap:10px;padding:6px 10px 6px 6px;border:1px solid var(--border-soft);border-radius:999px;color:var(--text-primary);background:var(--bg-input);cursor:pointer;transition:all 0.22s ease}
.user-chip:hover{border-color:rgba(var(--brand-accent),0.4);box-shadow:0 0 16px -4px rgba(200,210,130,0.3)}
.user-avatar{background:rgba(var(--brand-accent),0.16);color:var(--text-heading)}
.user-name{max-width:110px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;font-size:14px;font-weight:700;color:var(--text-primary)}
.role-tag{height:22px;display:inline-flex;align-items:center;padding:0 9px;border-radius:999px;font-size:12px;font-weight:800}
.role-tag--customer{background:rgba(var(--brand-accent),0.16);color:var(--text-heading)}
.role-tag--companion{background:rgba(var(--brand-gold),0.2);color:rgba(var(--brand-amber),0.9)}
.role-tag--admin{background:rgba(180,175,160,0.18);color:var(--text-secondary)}
/* 侧栏（白瓷磨砂） */
.layout-body{flex:1;display:flex;min-height:0;position:relative;z-index:3;overflow:hidden;padding-top:66px}
.side-bar{width:280px;padding:0;border-right:1px solid var(--border-soft);background:var(--bg-panel-gradient);backdrop-filter:blur(18px);-webkit-backdrop-filter:blur(18px);overflow:hidden;position:fixed;left:0;top:66px;bottom:0;z-index:10}
/* 主内容区 —— 注意：不要在此加 backdrop-filter/filter/transform，
   否则会成为 position:fixed 的包含块，把 el-dialog 弹窗困在内容区内、
   被 body 上的遮罩盖住导致点不动（只能 ESC）。弹窗遮罩挂在 body 上。 */
.main-content{flex:1;min-width:0;overflow-y:auto;margin-left:280px;background:rgba(var(--brand-accent),0.04)}
/* 卡片 */
.main-content ::v-deep .el-card,.main-content ::v-deep .filter-card,.main-content ::v-deep .list-card,.main-content ::v-deep .form-card,.main-content ::v-deep .detail-card,.main-content ::v-deep .content-card,.main-content ::v-deep .profile-card,.main-content ::v-deep .companion-card,.main-content ::v-deep .request-card,.main-content ::v-deep .order-card,.main-content ::v-deep .stat-card {
  background:var(--bg-card) !important; backdrop-filter:blur(14px) !important; -webkit-backdrop-filter:blur(14px) !important;
  border:1px solid var(--border-card) !important; border-radius:12px !important;
  box-shadow:var(--shadow-card) !important
}
.main-content ::v-deep .el-table,.main-content ::v-deep .el-table__expanded-cell{background:var(--bg-card) !important}
.main-content ::v-deep .el-table th{background:var(--bg-table-stripe) !important;color:var(--text-heading) !important;font-weight:700 !important;border-bottom:1px solid var(--border-soft) !important}
.main-content ::v-deep .el-table td{color:var(--text-primary) !important;border-bottom:1px solid var(--border-soft) !important}
.main-content ::v-deep .el-table__body tr:hover>td{background:var(--bg-hover) !important;box-shadow:inset 0 0 50px -20px rgba(var(--brand-gold),0.3) !important}
.main-content ::v-deep .el-table__empty-text{color:var(--text-muted)}
.main-content ::v-deep .el-input__inner,.main-content ::v-deep .el-textarea__inner,.main-content ::v-deep .el-select .el-input__inner{background:var(--bg-input) !important;border-color:var(--border-input) !important;color:var(--text-primary) !important;border-radius:9px !important}
.main-content ::v-deep .el-input__inner:focus,.main-content ::v-deep .el-textarea__inner:focus{border-color:rgba(var(--brand-accent),0.5) !important;box-shadow:0 0 0 4px rgba(var(--brand-accent),0.15),0 0 16px -2px rgba(var(--brand-gold),0.15) !important}
.main-content ::v-deep .el-form-item__label,.main-content ::v-deep .el-radio__label,.main-content ::v-deep .el-checkbox__label{color:var(--text-primary) !important}
.main-content ::v-deep .el-button--primary{background:linear-gradient(135deg,rgba(var(--brand-accent),0.9),rgba(var(--brand-deep),0.9)) !important;border-color:rgba(var(--brand-deep),0.9) !important;color:#fff !important;border-radius:9px !important;font-weight:600 !important}
.main-content ::v-deep .el-button--primary:hover{background:linear-gradient(135deg,rgba(var(--brand-accent),0.95),rgba(var(--brand-deep),0.95)) !important;box-shadow:0 6px 18px -6px rgba(var(--brand-accent),0.4),0 0 26px -2px rgba(var(--brand-accent),0.3) !important}
.main-content ::v-deep .el-button--default{background:var(--bg-input) !important;border-color:var(--border-input) !important;color:var(--text-heading) !important;border-radius:9px !important}
.main-content ::v-deep .el-button--default:hover{background:var(--bg-card) !important;border-color:rgba(var(--brand-gold),0.4) !important;box-shadow:0 4px 14px -4px rgba(var(--brand-gold),0.15),0 0 22px -2px rgba(var(--brand-gold),0.2) !important}
.main-content ::v-deep .el-button--danger{color:#fff !important;background:var(--btn-danger-bg) !important;border-color:var(--btn-danger-bg) !important}
.main-content ::v-deep .el-button--danger:hover{background:var(--btn-danger-hover) !important;border-color:var(--btn-danger-hover) !important}
.main-content ::v-deep .el-button--text{color:var(--text-heading) !important}
.main-content ::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active{background:rgba(var(--brand-accent),0.85) !important}
.main-content ::v-deep .el-tag--success{background:rgba(var(--brand-accent),0.15) !important;border-color:rgba(var(--brand-accent),0.3) !important;color:rgba(var(--brand-accent),0.85) !important}
.main-content ::v-deep .el-tag--warning{background:rgba(var(--brand-gold),0.15) !important;border-color:rgba(var(--brand-gold),0.3) !important;color:rgba(var(--brand-amber),0.88) !important}
.main-content ::v-deep .el-tag--danger{background:rgba(210,140,120,0.12) !important;border-color:rgba(195,120,105,0.3) !important;color:rgba(190,90,75,0.85) !important}
.main-content ::v-deep .page-title,.main-content ::v-deep h1{color:var(--text-heading) !important}
.main-content ::v-deep h2,.main-content ::v-deep h3{color:var(--text-primary) !important}
.main-content ::v-deep .el-dialog{background:var(--bg-card-solid) !important;border:1px solid var(--border-card) !important;border-radius:14px !important;box-shadow:var(--shadow-dropdown) !important}
.main-content ::v-deep .el-dialog__title{color:var(--text-heading) !important;font-weight:700 !important}
::v-deep .el-dropdown-menu{background:var(--bg-card-solid) !important;border:1px solid var(--border-card) !important;border-radius:11px !important;box-shadow:var(--shadow-dropdown) !important;backdrop-filter:blur(20px) !important}
::v-deep .el-dropdown-menu__item{color:var(--text-primary) !important}
::v-deep .el-dropdown-menu__item:hover{background:var(--bg-hover) !important;color:var(--text-heading) !important}
::v-deep .el-message{background:var(--bg-card-solid) !important;border:1px solid var(--border-card) !important;border-radius:10px !important;box-shadow:var(--shadow-dropdown) !important}
@media (max-width:860px){
  .top-bar{padding:0 14px;gap:12px}
  .brand-text,.top-nav{display:none}
  .side-bar{width:72px;flex-basis:72px}
  
  
  
}
</style>
