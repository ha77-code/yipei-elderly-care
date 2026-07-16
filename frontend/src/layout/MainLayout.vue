<template>
  <div class="main-layout">
    <!-- ===== 顶部导航栏 ===== -->
    <header class="top-bar">
      <div class="top-bar-left">
        <span class="brand-logo">益陪养老</span>
      </div>
      <nav class="top-bar-center">
        <router-link to="/" class="nav-link" exact-active-class="nav-link--active">首页</router-link>
        <router-link to="/customer/companions" class="nav-link">陪护人员</router-link>
        <router-link v-if="userRole === 'CUSTOMER'" to="/customer/requests" class="nav-link">我的需求</router-link>
        <router-link v-if="userRole === 'COMPANION'" to="/companion/available-orders" class="nav-link">可接订单</router-link>
        <router-link v-if="userRole === 'ADMIN'" to="/admin/statistics" class="nav-link">平台管理</router-link>
      </nav>
      <div class="top-bar-right">
        <el-dropdown trigger="click" @command="handleUserCommand">
          <span class="user-info">
            <el-avatar :size="32" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
            <span class="user-nickname">{{ userNickname }}</span>
            <span :class="['role-tag', `role-tag--${userRole.toLowerCase()}`]">{{ roleLabel }}</span>
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">
              <i class="el-icon-user"></i> 个人信息
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <i class="el-icon-switch-button"></i> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </header>

    <!-- ===== 主体区域 ===== -->
    <div class="layout-body">
      <!-- 侧边栏 -->
      <aside class="side-bar">
        <el-menu
          :default-active="activeMenu"
          :router="true"
          :unique-opened="true"
          background-color="transparent"
          text-color="#333333"
          active-text-color="#5C7A60"
          class="side-menu"
        >
          <!-- ===== CUSTOMER 菜单 ===== -->
          <template v-if="userRole === 'CUSTOMER'">
            <el-menu-item index="/customer/companions">
              <i class="el-icon-s-custom"></i>
              <span>陪诊师列表</span>
            </el-menu-item>
            <el-menu-item index="/customer/request/create">
              <i class="el-icon-edit-outline"></i>
              <span>发布需求</span>
            </el-menu-item>
            <el-menu-item index="/customer/requests">
              <i class="el-icon-document"></i>
              <span>我的需求</span>
            </el-menu-item>
            <el-menu-item index="/customer/orders">
              <i class="el-icon-s-order"></i>
              <span>我的订单</span>
            </el-menu-item>
          </template>

          <!-- ===== COMPANION 菜单 ===== -->
          <template v-if="userRole === 'COMPANION'">
            <el-menu-item index="/companion/profile">
              <i class="el-icon-postcard"></i>
              <span>入驻资料</span>
            </el-menu-item>
            <el-menu-item index="/companion/available-orders">
              <i class="el-icon-s-claim"></i>
              <span>可接订单</span>
            </el-menu-item>
            <el-menu-item index="/companion/orders">
              <i class="el-icon-s-order"></i>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/companion/service-records">
              <i class="el-icon-tickets"></i>
              <span>服务记录</span>
            </el-menu-item>
          </template>

          <!-- ===== ADMIN 菜单 ===== -->
          <template v-if="userRole === 'ADMIN'">
            <el-menu-item index="/admin/users">
              <i class="el-icon-user"></i>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/companion-review">
              <i class="el-icon-finished"></i>
              <span>陪诊师审核</span>
            </el-menu-item>
            <el-menu-item index="/admin/requests">
              <i class="el-icon-document-copy"></i>
              <span>需求管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/orders">
              <i class="el-icon-s-order"></i>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/reports">
              <i class="el-icon-warning-outline"></i>
              <span>投诉处理</span>
            </el-menu-item>
            <el-menu-item index="/admin/statistics">
              <i class="el-icon-data-line"></i>
              <span>平台统计</span>
            </el-menu-item>
          </template>
        </el-menu>
      </aside>

      <!-- 内容区 -->
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { getUser, clearUser, ROLES, ROLE_LABELS } from '@/utils/auth'

export default {
  name: 'MainLayout',
  data() {
    const user = getUser() || {}
    return {
      userNickname: user.nickname || user.username || '用户',
      userRole: user.role || ROLES.CUSTOMER
    }
  },
  computed: {
    roleLabel() {
      return ROLE_LABELS[this.userRole] || '客户'
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.doLogout()
      }
    },
    doLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        clearUser()
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* ===== Shell ===== */
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--color-bg-page);
}

/* ===== Top Bar ===== */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 36px;
  background: rgba(255,255,255,0.82);
  backdrop-filter: blur(16px) saturate(1.3);
  -webkit-backdrop-filter: blur(16px) saturate(1.3);
  border-bottom: 1px solid rgba(0,0,0,0.06);
  flex-shrink: 0;
  z-index: 100;
  position: relative;
}

.brand-logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary-dark);
  letter-spacing: 0.03em;
}

.top-bar-center { display: flex; align-items: center; gap: 2px; }

.nav-link {
  position: relative;
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 450;
  color: var(--color-text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: color 0.2s ease;
}
.nav-link::after {
  content: '';
  position: absolute;
  bottom: 2px; left: 50%; transform: translateX(-50%);
  width: 0; height: 2px;
  background: var(--color-primary-dark);
  border-radius: 1px;
  transition: width 0.25s cubic-bezier(0.4,0,0.2,1);
}
.nav-link:hover { color: var(--color-primary-dark); }
.nav-link:hover::after { width: 60%; }
.nav-link--active { color: var(--color-primary-dark); font-weight: 600; }
.nav-link--active::after { width: 60%; }

.top-bar-right { display: flex; align-items: center; }

.user-info {
  display: flex; align-items: center; gap: 10px;
  cursor: pointer;
  padding: 6px 12px 6px 6px;
  border-radius: 50px;
  background: transparent;
  transition: background 0.2s ease;
}
.user-info:hover { background: var(--color-bg-hover); }

.user-avatar { background: var(--color-primary-light); color: #fff; }

.user-nickname { font-size: 14px; font-weight: 550; color: var(--color-text-primary); }

.role-tag { padding: 2px 12px; border-radius: 20px; font-size: 11px; font-weight: 550; letter-spacing: 0.02em; }
.role-tag--customer { background: var(--color-primary-dim); color: var(--color-primary-dark); }
.role-tag--companion { background: var(--color-warm-dim); color: var(--color-warm-dark); }
.role-tag--admin { background: rgba(0,0,0,0.05); color: var(--color-text-secondary); }

/* ===== Body ===== */
.layout-body { display: flex; flex: 1; overflow: hidden; }

/* ===== Sidebar ===== */
.side-bar {
  width: 230px;
  flex-shrink: 0;
  padding: 20px 12px;
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-right: 1px solid rgba(0,0,0,0.04);
  overflow-y: auto;
}

.side-menu { border: none; background: transparent; }

.side-menu .el-menu-item {
  height: 42px;
  line-height: 42px;
  margin-bottom: 2px;
  padding-left: 16px !important;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 450;
  color: var(--color-text-secondary);
  position: relative;
  transition: all 0.2s ease;
}
.side-menu .el-menu-item::before {
  content: '';
  position: absolute;
  left: 0; top: 50%; transform: translateY(-50%);
  width: 3px; height: 0;
  background: var(--color-primary-dark);
  border-radius: 0 2px 2px 0;
  transition: height 0.2s ease;
}
.side-menu .el-menu-item:hover { background: rgba(122,154,126,0.06); color: var(--color-text-primary); }
.side-menu .el-menu-item.is-active {
  background: rgba(122,154,126,0.1);
  color: var(--color-primary-dark);
  font-weight: 580;
}
.side-menu .el-menu-item.is-active::before { height: 18px; }
.side-menu .el-menu-item i { color: var(--color-text-placeholder); margin-right: 4px; transition: color 0.2s ease; }
.side-menu .el-menu-item:hover i { color: var(--color-text-secondary); }
.side-menu .el-menu-item.is-active i { color: var(--color-primary-dark); }

/* ===== Main Content ===== */
.main-content {
  flex: 1;
  overflow-y: auto;
  background:
    radial-gradient(ellipse at 50% 0%, rgba(122,154,126,0.04) 0%, transparent 55%),
    var(--color-bg-page);
}
</style>
