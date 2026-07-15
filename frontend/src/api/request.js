/**
 * Axios 封装 — 统一请求实例
 * 包含：基础配置、请求拦截器（注入 token）、响应拦截器（统一错误处理）
 */
import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, getUser, clearUser } from '@/utils/auth'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

/* ===== 请求拦截器：自动注入用户身份 ===== */
request.interceptors.request.use(
  config => {
    const user = getUser()
    if (user && user.id) {
      config.headers['X-User-Id'] = user.id
    }
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/* ===== 响应拦截器：统一错误处理 ===== */
request.interceptors.response.use(
  response => {
    const res = response.data
    // 如果后端返回了 code 字段且不为 200，视为业务错误
    if (res.code !== undefined && res.code !== 200) {
      Message.error(res.message || '请求失败')
      // 401 未授权 → 清除登录状态并跳转登录页
      if (res.code === 401) {
        clearUser()
        router.push({ path: '/login', query: { redirect: router.currentRoute.fullPath } })
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const status = error.response?.status
    if (status === 401) {
      clearUser()
      Message.error('登录已过期，请重新登录')
      router.push({ path: '/login', query: { redirect: router.currentRoute.fullPath } })
    } else if (status === 403) {
      Message.error('无权限执行此操作')
    } else if (status === 404) {
      Message.error('请求的资源不存在')
    } else if (status >= 500) {
      Message.error('服务器异常，请稍后重试')
    } else {
      Message.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
