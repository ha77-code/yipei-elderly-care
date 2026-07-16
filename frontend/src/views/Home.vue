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
.home-page { padding: 36px 40px; max-width: 1200px; }

/* ===== Welcome ===== */
.welcome-section {
  display: flex; justify-content: space-between; align-items: center;
  background: #fff;
  border: 1px solid rgba(0,0,0,0.04);
  border-radius: var(--radius-lg);
  padding: 40px 48px;
  margin-bottom: 36px;
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}
.welcome-section::after {
  content: '';
  position: absolute; right: -60px; top: -60px;
  width: 240px; height: 240px;
  background: radial-gradient(circle, rgba(122,154,126,0.06) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.welcome-greeting { font-size: 13px; color: var(--color-primary); font-weight: 550; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.08em; }
.welcome-name { font-size: 30px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 6px; }
.welcome-desc { font-size: 15px; color: var(--color-text-secondary); margin: 0; }

.welcome-stats { display: flex; gap: 40px; }
.stat-card { text-align: center; padding: 0 24px; border-right: 1px solid rgba(0,0,0,0.05); }
.stat-card:last-child { border-right: none; }
.stat-value { display: block; font-size: 28px; font-weight: 700; color: var(--color-primary-dark); letter-spacing: -0.02em; }
.stat-label { display: block; font-size: 13px; color: var(--color-text-placeholder); margin-top: 4px; }

/* ===== Quick Entry ===== */
.section-title { font-size: 18px; font-weight: 650; color: var(--color-text-primary); margin: 0 0 18px; }

.quick-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(270px, 1fr)); gap: 14px; }

.quick-card {
  display: flex; align-items: center; gap: 16px;
  background: #fff;
  border: 1px solid rgba(0,0,0,0.04);
  border-radius: var(--radius-md);
  padding: 22px 24px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
  box-shadow: var(--shadow-xs);
}
.quick-card:hover {
  border-color: rgba(122,154,126,0.18);
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}
.quick-icon {
  width: 46px; height: 46px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; flex-shrink: 0;
}
.quick-icon--green { background: rgba(122,154,126,0.1); color: var(--color-primary-dark); }
.quick-icon--coffee { background: rgba(196,168,130,0.1); color: var(--color-warm-dark); }
.quick-info { flex: 1; min-width: 0; }
.quick-info h3 { font-size: 15px; font-weight: 650; color: var(--color-text-primary); margin: 0 0 3px; }
.quick-info p { font-size: 13px; color: var(--color-text-placeholder); margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.quick-arrow { font-size: 16px; color: #D0D0D0; flex-shrink: 0; transition: all 0.2s ease; }
.quick-card:hover .quick-arrow { color: var(--color-primary); transform: translateX(3px); }

@media (max-width: 768px) {
  .home-page { padding: 20px; }
  .welcome-section { flex-direction: column; text-align: center; gap: 28px; padding: 32px 24px; }
  .welcome-stats { gap: 20px; }
  .stat-card { padding: 0 14px; }
  .quick-grid { grid-template-columns: 1fr; }
}
</style>
