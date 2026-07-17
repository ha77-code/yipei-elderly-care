/**
 * 服务需求相关 API
 * — 发布需求、需求列表、取消需求
 */
import request from './request'

/** 发布服务需求 */
export function createRequest(data) {
  return request({
    url: '/api/service-request/create',
    method: 'post',
    data
  })
}

/** 获取我的需求列表 */
export function getMyRequests() {
  return request({
    url: '/api/service-request/list',
    method: 'get'
  })
}

/** 获取需求详情 */
export function getRequestDetail(id) {
  return request({
    url: `/api/service-request/${id}`,
    method: 'get'
  })
}

/** 取消需求 */
export function cancelRequest(id) {
  return request({
    url: `/api/service-request/${id}/cancel`,
    method: 'put'
  })
}

/** 获取所有需求列表（管理员） */
export function getAllRequests(params) {
  return request({
    url: '/api/admin/service-request/list',
    method: 'get',
    params
  })
}
