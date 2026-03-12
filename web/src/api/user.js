import request from '@/utils/request'

// 用户登录
export function login(data) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

// 用户注册
export function register(data) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}

// 获取用户信息
export function getUserInfo(id) {
    return request({
        url: `/user/${id}`,
        method: 'get'
    })
}

// 更新用户信息
export function updateUser(data) {
    return request({
        url: '/user',
        method: 'put',
        data
    })
}

// 修改密码
export function updatePassword(data) {
    return request({
        url: '/user/password',
        method: 'put',
        data
    })
}

// 获取用户列表（管理端）
export const getUserList = (params) => {
    return request({
        url: '/user/list-all',
        method: 'get',
        params
    })
}

// 删除用户
export const deleteUser = (id) => {
    return request({
        url: `/user/${id}`,
        method: 'delete'
    })
}
