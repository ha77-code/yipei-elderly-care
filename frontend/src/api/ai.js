import request from './request'

export function generateServiceSummary(data) {
  return request({
    url: '/api/ai/service-summary',
    method: 'post',
    data
  })
}