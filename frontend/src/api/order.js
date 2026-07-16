/**
 * 订单相关 API
 * — 创建订单、接单、订单列表、状态变更、取消
 */
import request from './request'

/** 创建订单（客户确认陪诊师后） */
export function createOrder(data) {
  return request({
    url: '/api/order/create',
    method: 'post',
    data
  })
}

/** 获取我的订单列表（客户/陪诊师通用） */
export function getMyOrders(params) {
  return request({
    url: '/api/order/my',
    method: 'get',
    params
  })
}

/** 获取可接订单列表（陪诊师） */
export function getAvailableOrders(params) {
  return request({
    url: '/api/order/available',
    method: 'get',
    params
  })
}

/** 获取订单详情 */
export function getOrderDetail(id) {
  return request({
    url: `/api/order/${id}`,
    method: 'get'
  })
}

/** 接单（陪诊师） */
export function acceptOrder(id) {
  return request({ url: `/api/order/${id}/accept`, method: 'put' })
}

/** 拒单（陪诊师） */
export function rejectOrder(id, data) {
  return request({ url: `/api/order/${id}/reject`, method: 'put', data })
}

/** 开始服务 */
export function startService(id) {
  return request({
    url: `/api/order/${id}/start`,
    method: 'put'
  })
}

/** 完成服务 */
export function completeService(id) {
  return request({
    url: `/api/order/${id}/complete`,
    method: 'put'
  })
}

/** 确认完成（客户） */
export function confirmOrder(id) {
  return request({
    url: `/api/order/${id}/confirm`,
    method: 'put'
  })
}

/** 取消订单 */
export function cancelOrder(id, data) {
  return request({
    url: `/api/order/${id}/cancel`,
    method: 'put',
    data
  })
}

/** 获取所有订单（管理员） */
export function getAllOrders(params) {
  return request({
    url: '/api/admin/orders',
    method: 'get',
    params
  })
}
