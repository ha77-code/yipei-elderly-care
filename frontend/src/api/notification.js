import request from './request'

export function getNotifications() {
  return request({ url: '/api/notifications', method: 'get' })
}

export function getNotificationUnread() {
  return request({ url: '/api/notifications/unread', method: 'get' })
}

export function markNotificationRead(id) {
  return request({ url: `/api/notifications/${id}/read`, method: 'put' })
}

export function markAllNotificationsRead() {
  return request({ url: '/api/notifications/read-all', method: 'put' })
}
