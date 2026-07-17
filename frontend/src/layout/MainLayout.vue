<template>
  <div class="main-layout">
    <header class="top-bar">
      <div class="brand-lockup" @click="$router.push('/')">
        <span class="brand-symbol">{{ text.brandSymbol }}</span>
        <span class="brand-text">{{ text.brandName }}</span>
      </div>

      <nav class="top-nav">
        <router-link v-for="item in topItems" :key="item.path" :to="item.path" class="nav-link" exact-active-class="nav-link--active">{{ item.label }}</router-link>
      </nav>

      <el-dropdown trigger="click" @command="handleUserCommand">
        <span class="user-chip">
          <el-avatar :size="32" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
          <span class="user-name">{{ userNickname }}</span>
          <span :class="['role-tag', `role-tag--${userRole.toLowerCase()}`]">{{ roleLabel }}</span>
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
        <el-menu :default-active="activeMenu" :router="true" :unique-opened="true" background-color="transparent" text-color="rgba(245,240,232,0.82)" active-text-color="#f0d2af" class="side-menu">
          <el-menu-item v-for="item in sideItems" :key="item.path" :index="item.path">
            <i :class="item.icon"></i><span>{{ item.label }}</span>
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

const T = {
  brandSymbol: '\u76ca',
  brandName: '\u76ca\u966a\u517b\u8001',
  workbench: '\u5de5\u4f5c\u53f0',
  profile: '\u4e2a\u4eba\u4fe1\u606f',
  logout: '\u9000\u51fa\u767b\u5f55',
  confirmTitle: '\u63d0\u793a',
  confirmLogout: '\u786e\u5b9a\u8981\u9000\u51fa\u767b\u5f55\u5417\uff1f',
  confirm: '\u786e\u5b9a',
  cancel: '\u53d6\u6d88',
  logoutDone: '\u5df2\u9000\u51fa\u767b\u5f55',
  fallbackUser: '\u7528\u6237',
  home: '\u9996\u9875',
  companions: '\u966a\u8bca\u5e08',
  myRequests: '\u6211\u7684\u9700\u6c42',
  availableOrders: '\u53ef\u63a5\u8ba2\u5355',
  admin: '\u5e73\u53f0\u7ba1\u7406',
  companionList: '\u966a\u8bca\u5e08\u5217\u8868',
  createRequest: '\u53d1\u5e03\u9700\u6c42',
  myOrders: '\u6211\u7684\u8ba2\u5355',
  companionProfile: '\u5165\u9a7b\u8d44\u6599',
  serviceRecords: '\u670d\u52a1\u8bb0\u5f55',
  userManage: '\u7528\u6237\u7ba1\u7406',
  companionReview: '\u966a\u8bca\u5e08\u5ba1\u6838',
  requestManage: '\u9700\u6c42\u7ba1\u7406',
  orderManage: '\u8ba2\u5355\u7ba1\u7406',
  reportManage: '\u6295\u8bc9\u5904\u7406',
  statistics: '\u5e73\u53f0\u7edf\u8ba1'
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
    { label: T.admin, path: '/admin/statistics' }
  ]
}

const SIDE_MAP = {
  CUSTOMER: [
    { label: T.companionList, path: '/customer/companions', icon: 'el-icon-s-custom' },
    { label: T.createRequest, path: '/customer/request/create', icon: 'el-icon-edit-outline' },
    { label: T.myRequests, path: '/customer/requests', icon: 'el-icon-document' },
    { label: T.myOrders, path: '/customer/orders', icon: 'el-icon-s-order' }
  ],
  COMPANION: [
    { label: T.companionProfile, path: '/companion/profile', icon: 'el-icon-postcard' },
    { label: T.availableOrders, path: '/companion/available-orders', icon: 'el-icon-s-claim' },
    { label: T.myOrders, path: '/companion/orders', icon: 'el-icon-s-order' },
    { label: T.serviceRecords, path: '/companion/service-records', icon: 'el-icon-tickets' }
  ],
  ADMIN: [
    { label: T.userManage, path: '/admin/users', icon: 'el-icon-user' },
    { label: T.companionReview, path: '/admin/companion-review', icon: 'el-icon-finished' },
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
      userRole: user.role || ROLES.CUSTOMER
    }
  },
  computed: {
    roleLabel() {
      return ROLE_LABELS[this.userRole] || ROLE_LABELS.CUSTOMER
    },
    activeMenu() {
      return this.$route.path
    },
    topItems() {
      return TOP_MAP[this.userRole] || TOP_MAP.CUSTOMER
    },
    sideItems() {
      return SIDE_MAP[this.userRole] || SIDE_MAP.CUSTOMER
    }
  },
  methods: {
    handleUserCommand(command) {
      if (command === 'profile') this.$router.push('/profile')
      if (command === 'logout') this.doLogout()
    },
    doLogout() {
      this.$confirm(T.confirmLogout, T.confirmTitle, {
        confirmButtonText: T.confirm,
        cancelButtonText: T.cancel,
        type: 'warning'
      }).then(() => {
        clearUser()
        this.$message.success(T.logoutDone)
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #071007;
  position: relative;
  overflow: hidden;
}
.main-layout::before {
  content: '';
  position: fixed;
  inset: -24px;
  background:
    linear-gradient(150deg, rgba(10, 18, 8, 0.6), rgba(18, 32, 15, 0.58) 48%, rgba(10, 18, 8, 0.74)),
    url('../../public/img/bg-bamboo.jpg') center/cover no-repeat;
  filter: blur(10px) saturate(0.92) brightness(0.88);
  transform: scale(1.03);
  z-index: 0;
}
.main-layout::after {
  content: '';
  position: fixed;
  inset: 0;
  background:
    linear-gradient(90deg, transparent 0 16%, rgba(255, 210, 140, 0.035) 16.1% 16.25%, transparent 16.6% 36%, rgba(255, 210, 140, 0.028) 36.1% 36.25%, transparent 36.6%),
    radial-gradient(ellipse at 24% 0%, rgba(225, 195, 160, 0.14), transparent 34%);
  pointer-events: none;
  z-index: 0;
}
.top-bar {
  height: 66px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 0 28px;
  background: rgba(7, 16, 7, 0.78);
  border-bottom: 1px solid rgba(230, 200, 160, 0.14);
  backdrop-filter: blur(20px) saturate(1.24);
  position: sticky;
  top: 0;
  z-index: 20;
}
.brand-lockup { display: flex; align-items: center; gap: 12px; cursor: pointer; min-width: 170px; }
.brand-symbol { width: 38px; height: 38px; display: flex; align-items: center; justify-content: center; border-radius: 10px; border: 1px solid rgba(240, 210, 175, 0.36); color: var(--brand-gold-420); font-family: var(--font-serif); font-size: 20px; font-weight: 800; background: rgba(18, 34, 16, 0.74); }
.brand-text { color: var(--brand-cream-100); font-family: var(--font-serif); font-size: 18px; font-weight: 800; }
.top-nav { flex: 1; display: flex; justify-content: center; gap: 6px; }
.nav-link { height: 38px; display: inline-flex; align-items: center; padding: 0 16px; border-radius: 999px; color: rgba(250, 247, 242, 0.82); text-decoration: none; font-size: 14px; font-weight: 700; transition: all 0.22s var(--ease-standard); }
.nav-link:hover, .nav-link--active { color: var(--brand-gold-420); background: rgba(225, 195, 160, 0.1); }
.user-chip { display: flex; align-items: center; gap: 10px; padding: 6px 10px 6px 6px; border: 1px solid rgba(230, 200, 160, 0.16); border-radius: 999px; color: rgba(245, 240, 232, 0.86); background: rgba(18, 34, 16, 0.62); cursor: pointer; }
.user-avatar { background: rgba(225, 195, 160, 0.22); color: var(--brand-gold-420); }
.user-name { max-width: 110px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 14px; font-weight: 700; }
.role-tag { height: 22px; display: inline-flex; align-items: center; padding: 0 9px; border-radius: 999px; font-size: 12px; font-weight: 800; }
.role-tag--customer { background: rgba(45, 90, 58, 0.34); color: #cfe4c7; }
.role-tag--companion { background: rgba(225, 195, 160, 0.16); color: var(--brand-gold-420); }
.role-tag--admin { background: rgba(245, 240, 232, 0.12); color: rgba(245, 240, 232, 0.82); }
.layout-body { flex: 1; display: flex; min-height: 0; position: relative; z-index: 1; }
.side-bar { width: 236px; flex: 0 0 236px; padding: 22px 12px; border-right: 1px solid rgba(230, 200, 160, 0.12); background: rgba(7, 16, 7, 0.48); backdrop-filter: blur(16px); overflow-y: auto; }
.side-title { padding: 0 14px 12px; color: rgba(225, 205, 175, 0.62); font-size: 12px; font-weight: 800; }
.side-menu { border: 0; }
.side-menu ::v-deep .el-menu-item { height: 44px; line-height: 44px; margin-bottom: 6px; border-radius: 10px; font-size: 14px; font-weight: 700; transition: all 0.22s var(--ease-standard); }
.side-menu ::v-deep .el-menu-item i { color: inherit; margin-right: 8px; }
.side-menu ::v-deep .el-menu-item:hover { background: rgba(225, 195, 160, 0.08); color: var(--brand-cream-100) !important; }
.side-menu ::v-deep .el-menu-item.is-active { background: rgba(225, 195, 160, 0.14); color: var(--brand-gold-420) !important; box-shadow: inset 3px 0 0 rgba(240, 210, 175, 0.72); }
.main-content { flex: 1; min-width: 0; overflow-y: auto; background: rgba(245, 240, 232, 0.04); backdrop-filter: blur(2px); }
.main-content ::v-deep .page-container,
.main-content ::v-deep .companions-page,
.main-content ::v-deep .requests-page,
.main-content ::v-deep .order-list,
.main-content ::v-deep .admin-page,
.main-content ::v-deep .profile-page,
.main-content ::v-deep .service-record-page { color: #172615; }
.main-content ::v-deep .page-header,
.main-content ::v-deep .filter-card,
.main-content ::v-deep .list-card,
.main-content ::v-deep .form-card,
.main-content ::v-deep .detail-card,
.main-content ::v-deep .content-card,
.main-content ::v-deep .profile-card,
.main-content ::v-deep .companion-card,
.main-content ::v-deep .request-card,
.main-content ::v-deep .order-card,
.main-content ::v-deep .stat-card,
.main-content ::v-deep .el-card {
  background: rgba(255, 248, 238, 0.94) !important;
  border: 1px solid rgba(230, 200, 160, 0.22) !important;
  border-radius: 10px !important;
  box-shadow: 0 16px 40px rgba(7, 16, 7, 0.16) !important;
}
.main-content ::v-deep .page-title,
.main-content ::v-deep h1,
.main-content ::v-deep h2,
.main-content ::v-deep h3 { color: #172615; }
.main-content ::v-deep .page-subtitle,
.main-content ::v-deep .desc,
.main-content ::v-deep .meta,
.main-content ::v-deep p { color: rgba(23, 38, 21, 0.66); }
.main-content ::v-deep .el-table,
.main-content ::v-deep .el-table__expanded-cell { background: rgba(255, 248, 238, 0.96) !important; }
.main-content ::v-deep .el-table th { background: rgba(239, 230, 214, 0.88) !important; color: #42513d !important; }
.main-content ::v-deep .el-table td { color: #243421 !important; }
.main-content ::v-deep .el-input__inner,
.main-content ::v-deep .el-textarea__inner,
.main-content ::v-deep .el-select .el-input__inner { background: rgba(255, 255, 255, 0.92) !important; border-color: rgba(210, 195, 175, 0.48) !important; color: #172615 !important; }
.main-content ::v-deep .el-input__inner:focus,
.main-content ::v-deep .el-textarea__inner:focus { border-color: rgba(225, 195, 160, 0.92) !important; box-shadow: 0 0 0 4px rgba(225, 195, 160, 0.16) !important; }
.main-content ::v-deep .el-form-item__label,
.main-content ::v-deep .el-radio__label,
.main-content ::v-deep .el-checkbox__label { color: rgba(23, 38, 21, 0.74) !important; }
.main-content ::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active { background: #2d5a3a !important; }
@media (max-width: 860px) {
  .top-bar { padding: 0 14px; gap: 12px; }
  .brand-text, .top-nav { display: none; }
  .side-bar { width: 72px; flex-basis: 72px; padding: 16px 8px; }
  .side-title, .side-menu ::v-deep .el-menu-item span { display: none; }
  .side-menu ::v-deep .el-menu-item { padding: 0 !important; display: flex; justify-content: center; }
  .side-menu ::v-deep .el-menu-item i { margin: 0; }
}
</style>
