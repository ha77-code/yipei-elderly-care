/**
 * 服务记录相关 API
 * — 创建/查看服务记录
 */
import request from './request'

/** 创建服务记录（陪诊师完成服务后填写） */
export function createServiceRecord(data) {
  return request({
    url: '/service-record/create',
    method: 'post',
    data
  })
}

/** 获取订单的服务记录 */
export function getServiceRecordByOrder(orderId) {
  return request({
    url: `/service-record/order/${orderId}`,
    method: 'get'
  })
}

/** 获取我的服务记录列表（陪诊师） */
export function getMyServiceRecords(params) {
  return request({
    url: '/service-record/my',
    method: 'get',
    params
  })
}
