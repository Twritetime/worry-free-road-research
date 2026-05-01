import request from '@/utils/request'

export const getDashboardStats = () => {
    return request({
        url: '/dashboard/stats',
        method: 'get'
    })
}

export const getSalesTrend = (params) => {
    return request({
        url: '/dashboard/sales-trend',
        method: 'get',
        params
    })
}

export const getMaterialSalesRanking = (params) => {
    return request({
        url: '/dashboard/material-sales-ranking',
        method: 'get',
        params
    })
}

export const getCategorySales = () => {
    return request({
        url: '/dashboard/category-sales',
        method: 'get'
    })
}

export const getUserGrowth = (params) => {
    return request({
        url: '/dashboard/user-growth',
        method: 'get',
        params
    })
}

export const getActivityStats = () => {
    return request({
        url: '/dashboard/activity-stats',
        method: 'get'
    })
}

export const getHotContent = (params) => {
    return request({
        url: '/dashboard/hot-content',
        method: 'get',
        params
    })
}
