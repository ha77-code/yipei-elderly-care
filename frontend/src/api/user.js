/**
 * 用户相关 API
 * — 登录、注册、获取/修改个人信息
 */
import request from './request'

/** 注册 */
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

/** 登录 */
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

/** 获取当前登录用户信息（通过 X-User-Id Header 识别） */
export function getUserInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}

/** 修改个人信息 */
export function updateUserInfo(data) {
  return request({
    url: '/api/user/info',
    method: 'put',
    data
  })
}
