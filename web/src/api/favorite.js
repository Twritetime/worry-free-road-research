import request from '@/utils/request'

export function getFavoriteList(params) {
    return request({
        url: '/favorite/list',
        method: 'get',
        params
    })
}

export function toggleFavorite(data) {
    return request({
        url: '/favorite/toggle',
        method: 'post',
        data
    })
}

export function checkFavorite(params) {
    return request({
        url: '/favorite/check',
        method: 'get',
        params
    })
}
