/**
 * 投诉相关 API
 */
import request from './request'

/** 提交投诉 */
export function submitReport(data) {
  return request({ url: '/api/report/create', method: 'post', data })
}
