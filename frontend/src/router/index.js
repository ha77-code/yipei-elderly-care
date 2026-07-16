import Vue from 'vue'
import VueRouter from 'vue-router'
import { getUser, ROLES } from '@/utils/auth'

Vue.use(VueRouter)

/* ===== 懒加载：公开页面 ===== */
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')
const NotFound = () => import('@/views/NotFound.vue')

/* ===== 懒加载：主布局 ===== */
const MainLayout = () => import('@/layout/MainLayout.vue')

/* ===== 懒加载：已登录通用页面 ===== */
const Home = () => import('@/views/Home.vue')
const Profile = () => import('@/views/Profile.vue')
const OrderList = () => import('@/views/OrderList.vue')
const OrderDetail = () => import('@/views/OrderDetail.vue')

/* ===== 懒加载：客户端 ===== */
const Companions = () => import('@/views/customer/Companions.vue')
const RequestCreate = () => import('@/views/customer/RequestCreate.vue')
const MyRequests = () => import('@/views/customer/MyRequests.vue')

/* ===== 懒加载：陪诊师端 ===== */
const CompanionProfile = () => import('@/views/companion/CompanionProfile.vue')
const AvailableOrders = () => import('@/views/companion/AvailableOrders.vue')
const ServiceRecords = () => import('@/views/companion/ServiceRecords.vue')

/* ===== 懒加载：管理员端 ===== */
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const CompanionReview = () => import('@/views/admin/CompanionReview.vue')
const AdminRequests = () => import('@/views/admin/AdminRequests.vue')
const AdminReports = () => import('@/views/admin/AdminReports.vue')
const AdminStatistics = () => import('@/views/admin/AdminStatistics.vue')

/* ===== 懒加载：服务记录 ===== */
const ServiceRecord = () => import('@/views/ServiceRecord.vue')

/* ===== 懒加载：陪诊师详情 ===== */
const CompanionDetail = () => import('@/views/CompanionDetail.vue')

const routes = [
  /* ==================== 公开路由（无 MainLayout） ==================== */
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

  /* ==================== 已登录路由（包裹在 MainLayout 内） ==================== */
  {
    path: '/',
    component: MainLayout,
    children: [
      /* ---------- 通用 ---------- */
      {
        path: '',
        name: 'Home',
        component: Home,
        meta: { title: '首页' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { title: '个人信息' }
      },
      {
        path: 'order/:orderId',
        name: 'OrderDetail',
        component: OrderDetail,
        meta: { title: '订单详情' }
      },
      {
        path: 'order/:orderId/service-record',
        name: 'ServiceRecord',
        component: ServiceRecord,
        meta: { title: '服务记录' }
      },


      /* ---------- 客户端 CUSTOMER ---------- */
      {
        path: 'customer/companions',
        name: 'Companions',
        component: Companions,
        meta: { title: '陪诊师列表', role: ROLES.CUSTOMER }
      },
      {
        path: 'customer/request/create',
        name: 'RequestCreate',
        component: RequestCreate,
        meta: { title: '发布需求', role: ROLES.CUSTOMER }
      },
      {
        path: 'customer/requests',
        name: 'MyRequests',
        component: MyRequests,
        meta: { title: '我的需求', role: ROLES.CUSTOMER }
      },
      {
        path: 'customer/orders',
        name: 'CustomerOrders',
        component: OrderList,
        meta: { title: '我的订单', role: ROLES.CUSTOMER }
      },

      /* ---------- 陪诊师端 COMPANION ---------- */
      {
        path: 'companion/profile',
        name: 'CompanionProfile',
        component: CompanionProfile,
        meta: { title: '入驻资料', role: ROLES.COMPANION }
      },
      {
        path: 'companion/available-orders',
        name: 'AvailableOrders',
        component: AvailableOrders,
        meta: { title: '可接订单', role: ROLES.COMPANION }
      },
      {
        path: 'companion/orders',
        name: 'CompanionOrders',
        component: OrderList,
        meta: { title: '我的订单', role: ROLES.COMPANION }
      },
      {
        path: 'companion/service-records',
        name: 'ServiceRecords',
        component: ServiceRecords,
        meta: { title: '服务记录', role: ROLES.COMPANION }
      },

      /* ---------- 管理员端 ADMIN ---------- */
      {
        path: 'admin/dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { title: '后台首页', role: ROLES.ADMIN }
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { title: '用户管理', role: ROLES.ADMIN }
      },
      {
        path: 'admin/companion-review',
        name: 'CompanionReview',
        component: CompanionReview,
        meta: { title: '陪诊师审核', role: ROLES.ADMIN }
      },
      {
        path: 'admin/requests',
        name: 'AdminRequests',
        component: AdminRequests,
        meta: { title: '需求管理', role: ROLES.ADMIN }
      },
      {
        path: 'admin/orders',
        name: 'AdminOrders',
        component: OrderList,
        meta: { title: '订单管理', role: ROLES.ADMIN }
      },
      {
        path: 'admin/reports',
        name: 'AdminReports',
        component: AdminReports,
        meta: { title: '投诉处理', role: ROLES.ADMIN }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: AdminStatistics,
        meta: { title: '平台统计', role: ROLES.ADMIN }
      },

      /* ---------- 陪诊师详情（所有角色可查看；必须放在固定陪诊师路由之后） ---------- */
      {
        path: 'companion/:id',
        name: 'CompanionDetail',
        component: CompanionDetail,
        meta: { title: '陪诊师详情' }
      },
    ]
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

const roleHome = {
  [ROLES.CUSTOMER]: '/customer/companions',
  [ROLES.COMPANION]: '/companion/available-orders',
  [ROLES.ADMIN]: '/admin/dashboard'
}

/* ===== 全局路由守卫 ===== */
router.beforeEach((to, from, next) => {
  const user = getUser()
  const loggedIn = !!user

  // 取嵌套路由最深层级的 meta
  const deepMeta = to.matched.length > 0
    ? to.matched[to.matched.length - 1].meta
    : to.meta
  const isPublic = deepMeta.public === true

  // ① 公开路由：已登录访问 /login /register → 重定向首页
  if (isPublic) {
    if (loggedIn && (to.path === '/login' || to.path === '/register')) {
      return next('/')
    }
    return next()
  }

  // ② 未登录：首页 → 落地页，角色页面 → 登录
  if (!loggedIn) {
    if (to.path === '/') {
      window.location.href = '/landing.html'
      return
    }
  }

  // ③ 需要特定角色的页面
  const requiredRole = deepMeta.role
  if (requiredRole) {
    // 未登录 → 去登录
    if (!loggedIn) {
      return next({ path: '/login', query: { redirect: to.fullPath } })
    }

    const userRole = user.role

    // ADMIN 可访问所有页面
    if (userRole === ROLES.ADMIN) {
      return next()
    }

    // 角色不匹配
    if (userRole !== requiredRole) return next(roleHome[userRole] || '/')
  }

  next()
})

/* ===== 全局后置守卫：设置页面标题 ===== */
router.afterEach((to) => {
  const deepMeta = to.matched.length > 0
    ? to.matched[to.matched.length - 1].meta
    : to.meta
  const title = deepMeta.title
  document.title = title ? `${title} — 益陪养老` : '益陪养老 — 专业养老陪护服务平台'
})

export default router
