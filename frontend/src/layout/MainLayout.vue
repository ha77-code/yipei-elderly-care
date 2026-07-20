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
        <a v-for="item in topItems" :key="item.path"
          href="#" class="nav-link"
          @click.prevent="goHome">{{ item.label }}</a>
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
        <div class="side-title">{{ text.workbench }}</div>
        <el-menu :default-active="activeMenu" :unique-opened="true" background-color="transparent" text-color="rgba(60,75,45,0.78)" active-text-color="rgba(120,175,85,0.95)" class="side-menu" @select="handleSideSelect">
          <el-menu-item v-for="item in sideItems" :key="item.path" :index="item.path">
            <i :class="item.icon"></i><span>{{ item.label }}</span>
            <el-badge v-if="item.badge && unreadTotal > 0" :value="unreadTotal" class="menu-badge" />
          </el-menu-item>
        </el-menu>
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
    { label: T.admin, path: '/admin' }
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
    { label: T.requestManage, path: '/admin/requests', icon: 'el-icon-document-copy' },
    { label: T.orderManage, path: '/admin/orders', icon: 'el-icon-s-order' },
    { label: T.reportManage, path: '/admin/reports', icon: 'el-icon-warning-outline' },
    { label: T.statistics, path: '/admin/statistics', icon: 'el-icon-data-line' }
  ]
}

export default {
  name: 'MainLayout',
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
    sideItems() { return SIDE_MAP[this.userRole] || SIDE_MAP.CUSTOMER }
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
      const map = { CUSTOMER: '/customer-concept-light.html', COMPANION: '/companion-concept-light.html', ADMIN: '/admin-concept-light.html' }
      window.location.href = map[this.userRole] || '/customer-concept-light.html'
    },
    handleSideSelect(path) {
      if (path.startsWith('/admin')) {
        window.location.href = '/admin-concept-light.html'
        return
      }
      this.$router.push(path).catch(() => {})
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
  --accent:108,140,80; --accent-deep:78,106,56; --amber:200,150,70;
  --warm-line:150,140,110; --ink:46,60,38; --ink-soft:96,110,82; --ink-faint:130,140,116;
  min-height:100vh; display:flex; flex-direction:column; background:#eef2e6; position:relative; overflow:hidden;
  font-family:'PingFang SC','Microsoft YaHei',-apple-system,sans-serif; color:rgba(var(--ink),0.9);
}
/* 背景 */
.bg-layer{position:fixed;inset:-24px;z-index:0;background:center/cover no-repeat;filter:blur(8px) saturate(0.92) brightness(1.06);transform:scale(1.03)}
.bg-layer::after{content:'';position:absolute;inset:0;background:linear-gradient(155deg,rgba(255,252,240,0.15),rgba(240,246,228,0.08) 48%,rgba(224,234,208,0.2))}
.bg-corners{position:fixed;inset:0;z-index:1;pointer-events:none;background:radial-gradient(circle 280px at 0% 0%,rgba(255,245,210,0.18),transparent 62%),radial-gradient(circle 320px at 100% 100%,rgba(210,225,180,0.15),transparent 62%),linear-gradient(90deg,transparent 0 16%,rgba(255,225,140,0.04) 16.1% 16.25%,transparent 16.6% 36%,rgba(255,225,140,0.03) 36.1% 36.25%,transparent 36.6%),radial-gradient(ellipse at 24% 0%,rgba(240,215,150,0.16),transparent 34%)}
/* 顶栏（白瓷磨砂） */
.top-bar{height:66px;display:flex;align-items:center;justify-content:space-between;gap:24px;padding:0 28px;background:linear-gradient(180deg,rgba(255,255,255,0.72),rgba(248,250,240,0.6));backdrop-filter:blur(20px);-webkit-backdrop-filter:blur(20px);border-bottom:1px solid rgba(var(--warm-line),0.2);position:sticky;top:0;z-index:20;box-shadow:0 2px 20px -8px rgba(60,70,40,0.12)}
.brand-lockup{display:flex;align-items:center;gap:12px;cursor:pointer;min-width:170px}
.brand-symbol{width:38px;height:38px;display:flex;align-items:center;justify-content:center;border-radius:10px;border:1px solid rgba(var(--accent),0.35);color:rgba(var(--accent-deep),0.85);font-family:'Noto Serif SC',serif;font-size:20px;font-weight:800;background:rgba(255,255,255,0.7)}
.brand-text{color:rgba(var(--accent-deep),0.9);font-family:'Noto Serif SC',serif;font-size:18px;font-weight:800}
.top-nav{flex:1;display:flex;justify-content:center;gap:6px}
.nav-link{height:38px;display:inline-flex;align-items:center;padding:0 16px;border-radius:999px;color:rgba(var(--ink-soft),0.82);text-decoration:none;font-size:14px;font-weight:700;transition:all 0.22s ease}
.nav-link:hover,.nav-link--active{color:rgba(var(--accent-deep),0.95);background:rgba(var(--accent),0.1);text-shadow:0 0 14px rgba(200,210,130,0.3)}
.user-chip{display:flex;align-items:center;gap:10px;padding:6px 10px 6px 6px;border:1px solid rgba(var(--warm-line),0.28);border-radius:999px;color:rgba(var(--ink),0.82);background:rgba(255,255,255,0.6);cursor:pointer;transition:all 0.22s ease}
.user-chip:hover{border-color:rgba(var(--accent),0.4);box-shadow:0 0 16px -4px rgba(200,210,130,0.3)}
.user-avatar{background:rgba(var(--accent),0.16);color:rgba(var(--accent-deep),0.8)}
.user-name{max-width:110px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;font-size:14px;font-weight:700;color:rgba(var(--ink),0.88)}
.role-tag{height:22px;display:inline-flex;align-items:center;padding:0 9px;border-radius:999px;font-size:12px;font-weight:800}
.role-tag--customer{background:rgba(var(--accent),0.16);color:rgba(var(--accent-deep),0.85)}
.role-tag--companion{background:rgba(245,215,140,0.2);color:rgba(170,130,60,0.92)}
.role-tag--admin{background:rgba(180,175,160,0.18);color:rgba(110,110,95,0.85)}
/* 侧栏（白瓷磨砂） */
.layout-body{flex:1;display:flex;min-height:0;position:relative;z-index:3}
.side-bar{width:236px;flex:0 0 236px;padding:22px 12px;border-right:1px solid rgba(var(--warm-line),0.16);background:linear-gradient(180deg,rgba(255,255,255,0.45),rgba(248,250,240,0.28));backdrop-filter:blur(18px);-webkit-backdrop-filter:blur(18px);overflow-y:auto}
.side-title{padding:0 14px 12px;color:rgba(var(--accent-deep),0.55);font-size:12px;font-weight:800;letter-spacing:0.05em}
.side-menu{border:0}
.side-menu ::v-deep .el-menu-item{height:44px;line-height:44px;margin-bottom:6px;border-radius:10px;font-size:14px;font-weight:700;transition:all 0.22s ease}
.side-menu ::v-deep .el-menu-item i{color:inherit;margin-right:8px}
.side-menu ::v-deep .el-menu-item:hover{background:rgba(var(--accent),0.1);color:rgba(var(--accent-deep),0.9) !important;box-shadow:inset 0 0 20px -8px rgba(var(--accent),0.1)}
.side-menu ::v-deep .el-menu-item.is-active{background:rgba(var(--accent),0.14);color:rgba(var(--accent-deep),0.95) !important;box-shadow:inset 3px 0 0 rgba(var(--accent),0.6)}
/* 主内容区 */
.main-content{flex:1;min-width:0;overflow-y:auto;background:rgba(255,255,255,0.06);backdrop-filter:blur(2px);-webkit-backdrop-filter:blur(2px)}
/* 卡片 */
.main-content ::v-deep .el-card,.main-content ::v-deep .filter-card,.main-content ::v-deep .list-card,.main-content ::v-deep .form-card,.main-content ::v-deep .detail-card,.main-content ::v-deep .content-card,.main-content ::v-deep .profile-card,.main-content ::v-deep .companion-card,.main-content ::v-deep .request-card,.main-content ::v-deep .order-card,.main-content ::v-deep .stat-card {
  background:rgba(255,255,255,0.62) !important; backdrop-filter:blur(14px) !important; -webkit-backdrop-filter:blur(14px) !important;
  border:1px solid rgba(255,255,255,0.7) !important; border-radius:12px !important;
  box-shadow:0 12px 36px -16px rgba(50,60,30,0.2),inset 0 1px 0 rgba(255,255,255,0.6) !important
}
.main-content ::v-deep .el-table,.main-content ::v-deep .el-table__expanded-cell{background:rgba(255,255,255,0.55) !important}
.main-content ::v-deep .el-table th{background:rgba(240,235,218,0.7) !important;color:rgba(78,106,56,0.8) !important;font-weight:700 !important;border-bottom:1px solid rgba(150,140,110,0.2) !important}
.main-content ::v-deep .el-table td{color:rgba(46,60,38,0.82) !important;border-bottom:1px solid rgba(150,140,110,0.08) !important}
.main-content ::v-deep .el-table__body tr:hover>td{background:rgba(108,140,80,0.06) !important;box-shadow:inset 0 0 50px -20px rgba(245,220,140,0.3) !important}
.main-content ::v-deep .el-table__empty-text{color:rgba(130,140,116,0.6)}
.main-content ::v-deep .el-input__inner,.main-content ::v-deep .el-textarea__inner,.main-content ::v-deep .el-select .el-input__inner{background:rgba(255,255,255,0.72) !important;border-color:rgba(150,140,110,0.28) !important;color:rgba(46,60,38,0.88) !important;border-radius:9px !important}
.main-content ::v-deep .el-input__inner:focus,.main-content ::v-deep .el-textarea__inner:focus{border-color:rgba(180,215,115,0.7) !important;box-shadow:0 0 0 4px rgba(200,225,140,0.2),0 0 16px -2px rgba(240,215,130,0.2) !important}
.main-content ::v-deep .el-form-item__label,.main-content ::v-deep .el-radio__label,.main-content ::v-deep .el-checkbox__label{color:rgba(46,60,38,0.85) !important}
.main-content ::v-deep .el-button--primary{background:linear-gradient(135deg,rgba(108,140,80,0.9),rgba(78,106,56,0.9)) !important;border-color:rgba(78,106,56,0.9) !important;color:#fff !important;border-radius:9px !important;font-weight:600 !important}
.main-content ::v-deep .el-button--primary:hover{background:linear-gradient(135deg,rgba(160,210,110,1),rgba(135,190,85,1)) !important;box-shadow:0 6px 18px -6px rgba(140,195,90,0.5),0 0 26px -2px rgba(200,225,140,0.55) !important}
.main-content ::v-deep .el-button--default{background:rgba(255,255,255,0.6) !important;border-color:rgba(150,140,110,0.25) !important;color:rgba(78,106,56,0.85) !important;border-radius:9px !important}
.main-content ::v-deep .el-button--default:hover{background:linear-gradient(135deg,rgba(255,252,240,0.96),rgba(245,220,140,0.2)) !important;border-color:rgba(240,210,130,0.5) !important;box-shadow:0 4px 14px -4px rgba(200,180,130,0.35),0 0 22px -2px rgba(245,220,135,0.4) !important}
.main-content ::v-deep .el-button--danger{color:rgba(170,90,75,0.85) !important;border-color:rgba(190,120,105,0.28) !important}
.main-content ::v-deep .el-button--text{color:rgba(78,106,56,0.8) !important}
.main-content ::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active{background:rgba(108,140,80,0.85) !important;box-shadow:0 0 10px rgba(140,195,90,0.35) !important}
.main-content ::v-deep .el-tag--success{background:rgba(185,220,135,0.18) !important;border-color:rgba(170,210,120,0.4) !important;color:rgba(130,185,80,0.88) !important}
.main-content ::v-deep .el-tag--warning{background:rgba(245,215,140,0.2) !important;border-color:rgba(235,205,130,0.4) !important;color:rgba(170,130,60,0.9) !important}
.main-content ::v-deep .el-tag--danger{background:rgba(210,140,120,0.14) !important;border-color:rgba(195,120,105,0.35) !important;color:rgba(160,80,65,0.85) !important}
.main-content ::v-deep .page-title,.main-content ::v-deep h1{color:rgba(78,106,56,0.92) !important}
.main-content ::v-deep h2,.main-content ::v-deep h3{color:rgba(46,60,38,0.88) !important}
.main-content ::v-deep .el-dialog{background:rgba(254,255,251,0.96) !important;border:1px solid rgba(195,225,140,0.2) !important;border-radius:14px !important;box-shadow:0 24px 60px -16px rgba(50,60,30,0.4) !important}
.main-content ::v-deep .el-dialog__title{color:rgba(78,106,56,0.9) !important;font-weight:700 !important}
::v-deep .el-dropdown-menu{background:rgba(254,255,251,0.96) !important;border:1px solid rgba(195,225,140,0.2) !important;border-radius:11px !important;box-shadow:0 18px 44px -12px rgba(50,60,30,0.35) !important;backdrop-filter:blur(20px) !important}
::v-deep .el-dropdown-menu__item{color:rgba(46,60,38,0.82) !important}
::v-deep .el-dropdown-menu__item:hover{background:rgba(195,225,140,0.18) !important;color:rgba(130,185,80,0.95) !important}
::v-deep .el-message{background:rgba(254,255,251,0.95) !important;border:1px solid rgba(195,225,140,0.25) !important;border-radius:10px !important;box-shadow:0 14px 36px -10px rgba(50,60,30,0.3) !important}
@media (max-width:860px){
  .top-bar{padding:0 14px;gap:12px}
  .brand-text,.top-nav{display:none}
  .side-bar{width:72px;flex-basis:72px;padding:16px 8px}
  .side-title,.side-menu ::v-deep .el-menu-item span{display:none}
  .side-menu ::v-deep .el-menu-item{padding:0 !important;display:flex;justify-content:center}
  .side-menu ::v-deep .el-menu-item i{margin:0}
}
</style>
