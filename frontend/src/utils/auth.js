/**
 * 登录状态管理工具
 * 管理用户信息的持久化存储（不含密码、token 等敏感数据）
 */

const USER_KEY = 'yipei_user'

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

/** 清除登录状态 */
export function clearUser() {
  localStorage.removeItem(USER_KEY)
}

/** 是否已登录 */
export function isLoggedIn() {
  return !!getUser()
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
