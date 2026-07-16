/**
 * 投诉相关 API
 * — 提交投诉、查看投诉、处理投诉
 */
import request from './request'

/** 提交投诉 */
export function submitReport(data) {
  return request({
    url: '/report/submit',
    method: 'post',
    data
  })
}

/** 获取我的投诉列表 */
export function getMyReports(params) {
  return request({
    url: '/report/my',
    method: 'get',
    params
  })
}

/** 获取投诉详情 */
export function getReportDetail(id) {
  return request({
    url: `/report/${id}`,
    method: 'get'
  })
}

/** 获取所有投诉（管理员） */
export function getAllReports(params) {
  return request({
    url: '/report/list',
    method: 'get',
    params
  })
}

/** 处理投诉（管理员） */
export function handleReport(id, data) {
  return request({
    url: `/report/${id}/handle`,
    method: 'put',
    data
  })
}
