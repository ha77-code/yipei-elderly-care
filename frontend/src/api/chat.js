/**
 * 订单私信聊天 API
 */
import request from './request'

/** 某订单聊天记录（自动标记已读） */
export function getChatMessages(orderId) {
  return request({ url: `/api/chat/${orderId}/messages`, method: 'get' })
}

/** 发送消息 */
export function sendChatMessage(orderId, content) {
  return request({ url: `/api/chat/${orderId}/send`, method: 'post', data: { content } })
}

/** 某订单聊天是否开启 */
export function getChatOpen(orderId) {
  return request({ url: `/api/chat/${orderId}/open`, method: 'get' })
}

/** 总未读数 */
export function getUnreadTotal() {
  return request({ url: '/api/chat/unread', method: 'get' })
}
