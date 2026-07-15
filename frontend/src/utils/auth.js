/**
 * 登录状态管理工具
 * 管理用户信息、token 的持久化存储
 */

const USER_KEY = 'yipei_user'
const TOKEN_KEY = 'yipei_token'

/** 保存用户信息到 localStorage */
export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/** 获取当前用户信息 */
export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

/** 清除用户信息 */
export function clearUser() {
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(TOKEN_KEY)
}

/** 保存 token */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 获取 token */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 是否已登录 */
export function isLoggedIn() {
  return !!getUser() && !!getToken()
}

/** 获取用户角色 */
export function getUserRole() {
  const user = getUser()
  return user ? user.role : null
}

/** 角色常量 */
export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  COMPANION: 'COMPANION',
  ADMIN: 'ADMIN'
}

/** 角色中文映射 */
export const ROLE_LABELS = {
  CUSTOMER: '客户',
  COMPANION: '陪诊师',
  ADMIN: '管理员'
}
