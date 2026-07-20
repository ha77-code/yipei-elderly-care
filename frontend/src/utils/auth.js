const USER_KEY = 'yipei_user'

function normalizeRole(role) {
  const value = String(role || '').trim().toUpperCase()
  const aliases = {
    CUSTOMER: 'CUSTOMER',
    COMPANION: 'COMPANION',
    ADMIN: 'ADMIN',
    '\u5ba2\u6237': 'CUSTOMER',
    '\u966a\u8bca\u5e08': 'COMPANION',
    '\u966a\u62a4\u5e08': 'COMPANION',
    '\u7ba1\u7406\u5458': 'ADMIN'
  }
  return aliases[value] || value
}

function normalizeUser(user) {
  if (!user || typeof user !== 'object') return user
  return { ...user, role: normalizeRole(user.role) }
}

export function setUser(user) {
  const data = JSON.stringify(normalizeUser(user))
  sessionStorage.setItem(USER_KEY, data)
  localStorage.setItem(USER_KEY, data)
}

export function getUser() {
  // 优先从 sessionStorage 读（Vue 登录页存放处）
  let raw = sessionStorage.getItem(USER_KEY)
  // 如果没有，尝试从 localStorage 读（静态 login.html 页存放处）
  if (!raw) {
    raw = localStorage.getItem(USER_KEY)
    // 如果从 localStorage 读到，同步到 sessionStorage
    if (raw) {
      sessionStorage.setItem(USER_KEY, raw)
    }
  }
  if (!raw) return null
  try {
    return normalizeUser(JSON.parse(raw))
  } catch {
    return null
  }
}

export function clearUser() {
  sessionStorage.removeItem(USER_KEY)
  localStorage.removeItem(USER_KEY)
}

export function isLoggedIn() {
  return !!getUser()
}

export function getUserRole() {
  const user = getUser()
  return user ? user.role : null
}

export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  COMPANION: 'COMPANION',
  ADMIN: 'ADMIN'
}

export const ROLE_LABELS = {
  CUSTOMER: '\u5ba2\u6237',
  COMPANION: '\u966a\u8bca\u5e08',
  ADMIN: '\u7ba1\u7406\u5458'
}
