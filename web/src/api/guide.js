import request from '@/utils/request'

export const getGuideList = (params) => {
    return request({
        url: '/guide/list',
        method: 'get',
        params
    })
}

export const getGuideById = (id) => {
    return request({
        url: `/guide/${id}`,
        method: 'get'
    })
}

export const createGuide = (data) => {
    return request({
        url: '/guide',
        method: 'post',
        data
    })
}

export const updateGuide = (data) => {
    return request({
        url: '/guide',
        method: 'put',
        data
    })
}

export const deleteGuide = (id) => {
    return request({
        url: `/guide/${id}`,
        method: 'delete'
    })
}

export const getGuideListAll = (params) => {
    return request({
        url: '/guide/list-all',
        method: 'get',
        params
    })
}

export const updateGuideStatus = (id, status) => {
    return request({
        url: `/guide/${id}/status/${status}`,
        method: 'put'
    })
}

export const swapGuideOrder = (id, swapId) => {
    return request({
        url: '/guide/swap-order',
        method: 'put',
        params: { id, swapId }
    })
}

export const getInstitutions = () => {
    return request({
        url: '/guide/institutions',
        method: 'get'
    })
}

export const getMajors = () => {
    return request({
        url: '/guide/majors',
        method: 'get'
    })
}
