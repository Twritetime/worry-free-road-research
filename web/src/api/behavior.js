import request from '@/utils/request'

export function recordBehavior(data) {
    return request({
        url: '/behavior/record',
        method: 'post',
        data
    })
}

export function getBehaviorTypes() {
    return request({
        url: '/behavior/types',
        method: 'get'
    })
}
