import request from '@/utils/request'

// 获取新闻列表
export const getNewsList = (params) => {
    return request({
        url: '/news/list',
        method: 'get',
        params
    })
}

// 根据ID获取新闻详情
export const getNewsById = (id) => {
    return request({
        url: `/news/${id}`,
        method: 'get'
    })
}

// 创建新闻
export const createNews = (data) => {
    return request({
        url: '/news',
        method: 'post',
        data
    })
}

// 更新新闻
export const updateNews = (data) => {
    return request({
        url: '/news',
        method: 'put',
        data
    })
}

// 删除新闻
export const deleteNews = (id) => {
    return request({
        url: `/news/${id}`,
        method: 'delete'
    })
}

// 获取所有新闻（管理端）
export const getNewsListAll = (params) => {
    return request({
        url: '/news/list-all',
        method: 'get',
        params
    })
}

// 更新新闻状态
export const updateNewsStatus = (id, status) => {
    return request({
        url: `/news/${id}/status/${status}`,
        method: 'put'
    })
}
