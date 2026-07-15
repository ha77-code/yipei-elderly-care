/**
 * 评价相关 API
 * — 提交评价、查看评价
 */
import request from './request'

/** 提交评价 */
export function submitEvaluation(data) {
  return request({
    url: '/evaluation/submit',
    method: 'post',
    data
  })
}

/** 获取订单的评价 */
export function getEvaluationByOrder(orderId) {
  return request({
    url: `/evaluation/order/${orderId}`,
    method: 'get'
  })
}

/** 获取用户收到的评价 */
export function getReceivedEvaluations(params) {
  return request({
    url: '/evaluation/received',
    method: 'get',
    params
  })
}
