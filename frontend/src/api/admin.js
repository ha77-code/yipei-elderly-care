/**
 * 管理员相关 API
 */
import request from './request'

/** 获取用户列表 */
export function getUserList() {
  return request({ url: '/api/user/list', method: 'get' })
}

/** 修改用户状态（启用/禁用） */
export function updateUserStatus(id, data) {
  return request({ url: `/api/user/${id}/status`, method: 'put', data })
}

/** 获取待审核的陪诊师列表 */
export function getPendingCompanions(params) {
  return request({ url: '/api/admin/companion/pending', method: 'get', params })
}

/** 审核陪诊师 */
export function auditCompanion(id, data) {
  return request({ url: `/api/admin/companion/${id}/audit`, method: 'put', data })
}

/** 获取平台统计数据 */
export function getStatistics() {
  return request({ url: '/api/admin/statistics', method: 'get' })
}

/** 获取待审核头像列表 */
export function getPendingAvatars() {
  return request({ url: '/api/admin/avatar/pending', method: 'get' })
}

/** 审核用户头像（auditStatus：1 通过 / 2 拒绝） */
export function auditAvatar(userId, data) {
  return request({ url: `/api/admin/avatar/${userId}/audit`, method: 'put', data })
}

/** 获取待审核需求列表 */
export function getPendingRequests() {
  return request({ url: '/api/admin/service-request/pending', method: 'get' })
}

/** 审核需求（auditStatus：1 通过 / 2 拒绝） */
export function auditRequest(id, data) {
  return request({ url: `/api/admin/service-request/${id}/audit`, method: 'put', data })
}

/** 获取投诉列表（管理员） */
export function getAllReports(params) {
  return request({ url: '/api/admin/report/list', method: 'get', params })
}

/** 处理投诉（管理员） */
export function handleReport(id, data) {
  return request({ url: `/api/admin/report/${id}/handle`, method: 'put', data })
}
