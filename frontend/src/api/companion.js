/**
 * 陪诊师相关 API
 */
import request from './request'

/** 获取陪诊师列表（客户浏览） */
export function getCompanionList(params) {
  return request({ url: '/api/companion/list', method: 'get', params })
}

/** 获取陪诊师详情 */
export function getCompanionDetail(id) {
  return request({ url: `/api/companion/${id}`, method: 'get' })
}

/** 提交入驻资料 */
export function saveCompanionProfile(data) {
  return request({ url: '/api/companion/apply', method: 'post', data })
}

/** 修改入驻资料 */
export function updateCompanionProfile(data) {
  return request({ url: '/api/companion/profile', method: 'put', data })
}

/** 获取我的入驻资料 */
export function getMyProfile() {
  return request({ url: '/api/companion/profile/my', method: 'get' })
}
