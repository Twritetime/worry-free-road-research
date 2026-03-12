import request from '@/utils/request'

// 获取评论列表
export const getCommentList = (postId, params) => {
    return request({
        url: `/comment/list/${postId}`,
        method: 'get',
        params
    })
}

// 获取通用评论列表
export const getComments = (params) => {
    return request({
        url: '/comment/list',
        method: 'get',
        params
    })
}

// 发布评论
export const createComment = (data) => {
    return request({
        url: '/comment',
        method: 'post',
        data
    })
}

// 删除评论
export const deleteComment = (id) => {
    return request({
        url: `/comment/${id}`,
        method: 'delete'
    })
}
