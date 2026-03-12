import request from '@/utils/request'

// 获取帖子列表
export const getPostList = (params) => {
    return request({
        url: '/post/list',
        method: 'get',
        params
    })
}

// 根据ID获取帖子详情
export const getPostById = (id) => {
    return request({
        url: `/post/${id}`,
        method: 'get'
    })
}

// 发布帖子
export const createPost = (data) => {
    return request({
        url: '/post',
        method: 'post',
        data
    })
}

// 更新帖子
export const updatePost = (data) => {
    return request({
        url: '/post',
        method: 'put',
        data
    })
}

// 删除帖子
export const deletePost = (id) => {
    return request({
        url: `/post/${id}`,
        method: 'delete'
    })
}

// 获取所有帖子（管理端）
export const getPostListAll = (params) => {
    return request({
        url: '/post/list-all',
        method: 'get',
        params
    })
}

// 审核帖子
export const auditPost = (id, status) => {
    return request({
        url: `/post/${id}/audit/${status}`,
        method: 'put'
    })
}

// 置顶帖子
export const toggleTop = (id, isTop) => {
    return request({
        url: `/post/${id}/top/${isTop}`,
        method: 'put'
    })
}
