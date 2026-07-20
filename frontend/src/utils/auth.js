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
  sessionStorage.setItem(USER_KEY, JSON.stringify(normalizeUser(user)))
}

export function getUser() {
  const raw = sessionStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return normalizeUser(JSON.parse(raw))
  } catch {
    return null
  }
}

export function clearUser() {
  sessionStorage.removeItem(USER_KEY)
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
