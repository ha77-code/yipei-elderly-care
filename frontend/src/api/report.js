/**
 * 投诉相关 API
 */
import request from './request'

/** 提交投诉 */
export function submitReport(data) {
  return request({ url: '/api/report/create', method: 'post', data })
}

/** 获取我的投诉列表 */
export function getMyReports(params) {
  return request({ url: '/api/report/my', method: 'get', params })
}

/** 获取投诉详情 */
export function getReportDetail(id) {
  return request({ url: `/api/report/${id}`, method: 'get' })
}

/** 获取所有投诉（管理员） */
export function getAllReports(params) {
  return request({ url: '/api/admin/report/list', method: 'get', params })
}

/** 处理投诉（管理员） */
export function handleReport(id, data) {
  return request({ url: `/api/admin/report/${id}/handle`, method: 'put', data })
}
