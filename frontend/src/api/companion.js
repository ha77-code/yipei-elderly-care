/**
 * 陪诊师相关 API
 * — 陪诊师列表、入驻资料、审核
 */
import request from './request'

/** 获取陪诊师列表（客户浏览） */
export function getCompanionList(params) {
  return request({
    url: '/companion/list',
    method: 'get',
    params
  })
}

/** 获取陪诊师详情 */
export function getCompanionDetail(id) {
  return request({
    url: `/companion/${id}`,
    method: 'get'
  })
}

/** 保存/更新入驻资料 */
export function saveCompanionProfile(data) {
  return request({
    url: '/companion/profile',
    method: 'post',
    data
  })
}

/** 获取我的入驻资料 */
export function getMyProfile() {
  return request({
    url: '/companion/profile/my',
    method: 'get'
  })
}

/** 审核陪诊师（管理员） */
export function auditCompanion(data) {
  return request({
    url: '/companion/audit',
    method: 'put',
    data
  })
}
