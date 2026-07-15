/**
 * 开发环境测试账号
 * 提供快速登录功能，方便切换不同角色体验
 */
import { login } from '@/api/user'
import { setUser, setToken } from '@/utils/auth'

const TEST_ACCOUNTS = [
  { role: 'CUSTOMER', label: '张三', roleName: '客户（家属）' },
  { role: 'COMPANION', label: '李陪诊', roleName: '陪诊师' },
  { role: 'ADMIN', label: '管理员', roleName: '平台管理员' }
]

/** 获取测试账号列表 */
export function getTestAccounts() {
  return TEST_ACCOUNTS
}

/** 快速登录：使用内置测试账号 */
export async function quickLogin(role) {
  const account = TEST_ACCOUNTS.find(a => a.role === role)
  if (!account) return

  const usernameMap = {
    CUSTOMER: 'customer1',
    COMPANION: 'companion1',
    ADMIN: 'admin1'
  }

  try {
    const res = await login({
      username: usernameMap[role] || 'customer1',
      password: '123456'
    })
    setToken(res.data?.token || '')
    setUser(res.data?.user || res.data)

    // 获取当前 Vue 实例并跳转
    const app = document.getElementById('app').__vue__
    if (app) {
      app.$message && app.$message.success('登录成功（测试账号）')
    }
    location.href = '/'
  } catch {
    // 接口未实现时不做处理
  }
}
