/**
 * 管理员相关 API
 * — 用户管理、平台统计
 */
import request from './request'

/** 获取用户列表 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/** 修改用户状态（启用/禁用） */
export function updateUserStatus(id, data) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data
  })
}

/** 获取待审核的陪诊师列表 */
export function getPendingCompanions(params) {
  return request({
    url: '/admin/companions/pending',
    method: 'get',
    params
  })
}

/** 获取平台统计数据 */
export function getStatistics() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}
