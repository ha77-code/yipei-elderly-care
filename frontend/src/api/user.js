/**
 * 用户相关 API
 * — 登录、注册、获取/修改个人信息
 */
import request from './request'

/** 登录 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/** 注册 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/** 获取当前用户信息 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/** 修改个人信息 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}
