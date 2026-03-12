import request from '@/utils/request'

export function getAddressList(userId) {
    return request({
        url: '/address/list',
        method: 'get',
        params: { userId }
    })
}

export function saveAddress(data) {
    return request({
        url: '/address',
        method: 'post',
        data
    })
}

export function updateAddress(data) {
    return request({
        url: '/address',
        method: 'put',
        data
    })
}

export function deleteAddress(id) {
    return request({
        url: `/address/${id}`,
        method: 'delete'
    })
}
