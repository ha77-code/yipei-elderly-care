/**
 * 陪诊师申请接单 / 客户挑选陪诊师 相关 API
 */
import request from './request'

/** 需求广场：陪诊师可申请的需求池 */
export function getRequestPool(params) {
  return request({ url: '/api/application/pool', method: 'get', params })
}

/** 陪诊师申请接单 */
export function applyForRequest(data) {
  return request({ url: '/api/application/apply', method: 'post', data })
}

/** 陪诊师撤回申请 */
export function withdrawApplication(id) {
  return request({ url: `/api/application/${id}/withdraw`, method: 'put' })
}

/** 陪诊师查看自己的申请 */
export function getMyApplications() {
  return request({ url: '/api/application/mine', method: 'get' })
}

/** 客户查看某需求的申请列表 */
export function getApplicationsByRequest(requestId) {
  return request({ url: `/api/application/by-request/${requestId}`, method: 'get' })
}

/** 客户接受某申请（可选 servicePrice） */
export function acceptApplication(id, data) {
  return request({ url: `/api/application/${id}/accept`, method: 'put', data: data || {} })
}

/** 客户拒绝某申请 */
export function rejectApplication(id) {
  return request({ url: `/api/application/${id}/reject`, method: 'put' })
}
