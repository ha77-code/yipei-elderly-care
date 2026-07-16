<template>
  <div class="home-page">
    <!-- ===== 欢迎区域 ===== -->
    <section class="welcome-section">
      <div class="welcome-text">
        <p class="welcome-greeting">{{ greeting }}</p>
        <h1 class="welcome-name">{{ userNickname }}，下午好 👋</h1>
        <p class="welcome-desc">益陪养老，用心守护每一段陪护旅程</p>
      </div>
      <div class="welcome-stats">
        <div class="stat-card">
          <span class="stat-value">{{ stats.orders }}</span>
          <span class="stat-label">今日订单</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ stats.companions }}</span>
          <span class="stat-label">在线陪诊师</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ stats.families }}</span>
          <span class="stat-label">服务家庭</span>
        </div>
      </div>
    </section>

    <!-- ===== 快捷入口 ===== -->
    <section class="quick-section">
      <h2 class="section-title">快捷入口</h2>
      <div class="quick-grid">
        <!-- CUSTOMER 快捷入口 -->
        <template v-if="userRole === 'CUSTOMER'">
          <div class="quick-card" @click="$router.push('/customer/companions')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="quick-info">
              <h3>找陪诊师</h3>
              <p>浏览可预约的专业陪诊师</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/customer/request/create')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-edit-outline"></i>
            </div>
            <div class="quick-info">
              <h3>发布需求</h3>
              <p>填写陪诊需求，快速匹配</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/customer/requests')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-document"></i>
            </div>
            <div class="quick-info">
              <h3>我的需求</h3>
              <p>查看已发布的服务需求</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/customer/orders')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="quick-info">
              <h3>我的订单</h3>
              <p>跟踪订单状态和服务进度</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
        </template>

        <!-- COMPANION 快捷入口 -->
        <template v-if="userRole === 'COMPANION'">
          <div class="quick-card" @click="$router.push('/companion/profile')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-postcard"></i>
            </div>
            <div class="quick-info">
              <h3>入驻资料</h3>
              <p>完善个人资料，展示专业能力</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/companion/available-orders')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-s-claim"></i>
            </div>
            <div class="quick-info">
              <h3>可接订单</h3>
              <p>浏览并接取合适的陪诊订单</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/companion/orders')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="quick-info">
              <h3>我的订单</h3>
              <p>管理已接订单和服务进度</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/companion/service-records')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-tickets"></i>
            </div>
            <div class="quick-info">
              <h3>服务记录</h3>
              <p>查看已完成的服务历史</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
        </template>

        <!-- ADMIN 快捷入口 -->
        <template v-if="userRole === 'ADMIN'">
          <div class="quick-card" @click="$router.push('/admin/users')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-user"></i>
            </div>
            <div class="quick-info">
              <h3>用户管理</h3>
              <p>管理系统用户账号和状态</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/admin/companion-review')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-finished"></i>
            </div>
            <div class="quick-info">
              <h3>陪诊师审核</h3>
              <p>审核陪诊师入驻申请</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/admin/orders')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="quick-info">
              <h3>订单管理</h3>
              <p>查看和管控所有订单</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/admin/reports')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="quick-info">
              <h3>投诉处理</h3>
              <p>处理用户投诉和反馈</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/admin/requests')">
            <div class="quick-icon quick-icon--green">
              <i class="el-icon-document-copy"></i>
            </div>
            <div class="quick-info">
              <h3>需求管理</h3>
              <p>查看所有服务需求</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
          <div class="quick-card" @click="$router.push('/admin/statistics')">
            <div class="quick-icon quick-icon--coffee">
              <i class="el-icon-data-line"></i>
            </div>
            <div class="quick-info">
              <h3>平台统计</h3>
              <p>查看平台运营数据概览</p>
            </div>
            <i class="el-icon-arrow-right quick-arrow"></i>
          </div>
        </template>
      </div>
    </section>
  </div>
</template>

<script>
import { getUser, ROLES } from '@/utils/auth'

export default {
  name: 'Home',
  data() {
    const user = getUser() || {}
    return {
      userNickname: user.nickname || user.username || '用户',
      userRole: user.role || ROLES.CUSTOMER,
      greeting: this._getGreeting(),
      stats: {
        orders: 128,
        companions: 86,
        families: 12000
      }
    }
  },
  methods: {
    _getGreeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了 🌙'
      if (hour < 12) return '早上好 ☀️'
      if (hour < 18) return '下午好 🌿'
      return '晚上好 🌙'
    }
  }
}
</script>

<style scoped>
.home-page {
  padding: 32px;
  max-width: 1200px;
}

/* ===== 欢迎区域 ===== */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  padding: 36px 40px;
  margin-bottom: 32px;
  box-shadow: var(--shadow-sm);
}

.welcome-greeting {
  font-size: 14px;
  color: var(--color-primary);
  font-weight: 500;
  margin-bottom: 4px;
}

.welcome-name {
  font-family: var(--font-family);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 8px;
}

.welcome-desc {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* 右侧统计 */
.welcome-stats {
  display: flex;
  gap: 32px;
}

.stat-card {
  text-align: center;
  padding: 0 20px;
  border-right: 1px solid var(--color-border-light);
}
.stat-card:last-child {
  border-right: none;
}

.stat-value {
  display: block;
  font-family: var(--font-family);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary-dark);
}

.stat-label {
  display: block;
  font-size: 13px;
  color: var(--color-text-placeholder);
  margin-top: 4px;
}

/* ===== 快捷入口 ===== */
.section-title {
  font-family: var(--font-family);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 20px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 20px 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.quick-card:hover {
  border-color: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.quick-icon--green {
  background: rgba(122, 154, 126, 0.12);
  color: var(--color-primary-dark);
}

.quick-icon--coffee {
  background: rgba(196, 168, 130, 0.12);
  color: var(--color-deep-coffee);
}

.quick-info {
  flex: 1;
  min-width: 0;
}

.quick-info h3 {
  font-family: var(--font-family);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 4px;
}

.quick-info p {
  font-size: 13px;
  color: var(--color-text-placeholder);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.quick-arrow {
  font-size: 14px;
  color: var(--color-border);
  flex-shrink: 0;
  transition: color 0.2s;
}
.quick-card:hover .quick-arrow {
  color: var(--color-primary);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .home-page {
    padding: 16px;
  }
  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }
  .welcome-stats {
    gap: 16px;
  }
  .stat-card {
    padding: 0 12px;
  }
  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
