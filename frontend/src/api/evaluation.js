/**
 * 评价相关 API
 */
import request from './request'

/** 提交评价 */
export function submitEvaluation(data) {
  return request({ url: '/api/evaluation/create', method: 'post', data })
}

/** 获取订单的评价 */
export function getEvaluationByOrder(orderId) {
  return request({ url: `/api/evaluation/order/${orderId}`, method: 'get' })
}

/** 获取陪诊师收到的评价 */
export function getCompanionEvaluations(companionId) {
  return request({ url: `/api/evaluation/companion/${companionId}`, method: 'get' })
}
