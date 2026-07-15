import Vue from 'vue'
import VueRouter from 'vue-router'
import { Message } from 'element-ui'
import { getUser, ROLES } from '@/utils/auth'

Vue.use(VueRouter)

/* ===== 懒加载：核心页面 ===== */
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')
const Home = () => import('@/views/Home.vue')
const Profile = () => import('@/views/Profile.vue')
const NotFound = () => import('@/views/NotFound.vue')

/* ===== 懒加载：客户端 ===== */
const Companions = () => import('@/views/customer/Companions.vue')
const RequestCreate = () => import('@/views/customer/RequestCreate.vue')
const MyRequests = () => import('@/views/customer/MyRequests.vue')
const CustomerOrders = () => import('@/views/customer/CustomerOrders.vue')

/* ===== 懒加载：陪诊师端 ===== */
const CompanionProfile = () => import('@/views/companion/CompanionProfile.vue')
const AvailableOrders = () => import('@/views/companion/AvailableOrders.vue')
const CompanionOrders = () => import('@/views/companion/CompanionOrders.vue')
const ServiceRecords = () => import('@/views/companion/ServiceRecords.vue')

/* ===== 懒加载：管理员端 ===== */
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const CompanionReview = () => import('@/views/admin/CompanionReview.vue')
const AdminRequests = () => import('@/views/admin/AdminRequests.vue')
const AdminOrders = () => import('@/views/admin/AdminOrders.vue')
const AdminReports = () => import('@/views/admin/AdminReports.vue')
const AdminStatistics = () => import('@/views/admin/AdminStatistics.vue')

const routes = [
  /* ==================== 公开路由 ==================== */
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册', public: true }
  },

  /* ==================== 通用（已登录即可访问） ==================== */
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { title: '个人信息' }
  },

  /* ==================== 客户端 CUSTOMER ==================== */
  {
    path: '/customer/companions',
    name: 'Companions',
    component: Companions,
    meta: { title: '陪诊师列表', role: ROLES.CUSTOMER }
  },
  {
    path: '/customer/request/create',
    name: 'RequestCreate',
    component: RequestCreate,
    meta: { title: '发布需求', role: ROLES.CUSTOMER }
  },
  {
    path: '/customer/requests',
    name: 'MyRequests',
    component: MyRequests,
    meta: { title: '我的需求', role: ROLES.CUSTOMER }
  },
  {
    path: '/customer/orders',
    name: 'CustomerOrders',
    component: CustomerOrders,
    meta: { title: '我的订单', role: ROLES.CUSTOMER }
  },

  /* ==================== 陪诊师端 COMPANION ==================== */
  {
    path: '/companion/profile',
    name: 'CompanionProfile',
    component: CompanionProfile,
    meta: { title: '入驻资料', role: ROLES.COMPANION }
  },
  {
    path: '/companion/available-orders',
    name: 'AvailableOrders',
    component: AvailableOrders,
    meta: { title: '可接订单', role: ROLES.COMPANION }
  },
  {
    path: '/companion/orders',
    name: 'CompanionOrders',
    component: CompanionOrders,
    meta: { title: '我的订单', role: ROLES.COMPANION }
  },
  {
    path: '/companion/service-records',
    name: 'ServiceRecords',
    component: ServiceRecords,
    meta: { title: '服务记录', role: ROLES.COMPANION }
  },

  /* ==================== 管理员端 ADMIN ==================== */
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsers,
    meta: { title: '用户管理', role: ROLES.ADMIN }
  },
  {
    path: '/admin/companion-review',
    name: 'CompanionReview',
    component: CompanionReview,
    meta: { title: '陪诊师审核', role: ROLES.ADMIN }
  },
  {
    path: '/admin/requests',
    name: 'AdminRequests',
    component: AdminRequests,
    meta: { title: '需求管理', role: ROLES.ADMIN }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: AdminOrders,
    meta: { title: '订单管理', role: ROLES.ADMIN }
  },
  {
    path: '/admin/reports',
    name: 'AdminReports',
    component: AdminReports,
    meta: { title: '投诉处理', role: ROLES.ADMIN }
  },
  {
    path: '/admin/statistics',
    name: 'AdminStatistics',
    component: AdminStatistics,
    meta: { title: '平台统计', role: ROLES.ADMIN }
  },

  /* ==================== 404 ==================== */
  {
    path: '*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '404', public: true }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes
})

/* ===== 全局路由守卫 ===== */
router.beforeEach((to, from, next) => {
  const user = getUser()
  const loggedIn = !!user
  const isPublic = to.meta.public === true

  // ① 公开路由：已登录访问 /login /register → 重定向首页
  if (isPublic) {
    if (loggedIn && (to.path === '/login' || to.path === '/register')) {
      return next('/')
    }
    return next()
  }

  // ② 需登录的页面：未登录 → 跳转登录页（带 redirect 参数）
  if (!loggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // ③ 角色权限检查
  const requiredRole = to.meta.role
  if (requiredRole) {
    const userRole = user.role

    // ADMIN 可访问所有页面
    if (userRole === ROLES.ADMIN) {
      return next()
    }

    // 角色不匹配
    if (userRole !== requiredRole) {
      Message.warning('无权限访问该页面')
      return next('/')
    }
  }

  next()
})

/* ===== 全局后置守卫：设置页面标题 ===== */
router.afterEach((to) => {
  const title = to.meta.title
  document.title = title ? `${title} — 益陪养老` : '益陪养老 — 专业养老陪护服务平台'
})

export default router
