const USER_KEY = 'yipei_user'

function normalizeRole(role) {
  const value = String(role || '').trim().toUpperCase()
  const aliases = {
    CUSTOMER: 'CUSTOMER',
    COMPANION: 'COMPANION',
    ADMIN: 'ADMIN',
    '陪诊师': 'COMPANION',
    '陪护师': 'COMPANION',
    '客户': 'CUSTOMER',
    '管理员': 'ADMIN'
  }
  return aliases[value] || value
}

function normalizeUser(user) {
  if (!user || typeof user !== 'object') return user
  return { ...user, role: normalizeRole(user.role) }
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(normalizeUser(user)))
}

export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return normalizeUser(JSON.parse(raw))
  } catch {
    return null
  }
}

export function clearUser() {
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
  CUSTOMER: '客户',
  COMPANION: '陪诊师',
  ADMIN: '管理员'
}
