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

/** 获取用户信息，id 为当前登录用户 ID */
export function getUserInfo(id) {
  return request({
    url: '/api/user/info',
    method: 'get',
    params: { id }
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
