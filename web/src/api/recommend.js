import request from '@/utils/request'

export function getRecommendedMaterials(params) {
    return request({
        url: '/recommend/materials',
        method: 'get',
        params
    })
}

export function getPopularMaterials(params) {
    return request({
        url: '/recommend/popular',
        method: 'get',
        params
    })
}

export function getRecentlyViewedMaterials(params) {
    return request({
        url: '/recommend/recently-viewed',
        method: 'get',
        params
    })
}
