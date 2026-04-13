import request from '@/utils/request'

// 获取资料列表
export const getMaterialList = (params) => {
    return request({
        url: '/material/list',
        method: 'get',
        params
    })
}

export const getFlashMaterialList = (limit = 6) => {
    return request({
        url: '/material/flash-list',
        method: 'get',
        params: { limit }
    })
}

// 根据ID获取资料详情
export const getMaterialById = (id) => {
    return request({
        url: `/material/${id}`,
        method: 'get'
    })
}

// 新增资料
export const saveMaterial = (data) => {
    return request({
        url: '/material',
        method: 'post',
        data
    })
}

// 更新资料
export const updateMaterial = (data) => {
    return request({
        url: '/material',
        method: 'put',
        data
    })
}

// 删除资料
export const deleteMaterial = (id) => {
    return request({
        url: `/material/${id}`,
        method: 'delete'
    })
}

// 获取所有资料（管理端）
export const getMaterialListAll = (params) => {
    return request({
        url: '/material/list-all',
        method: 'get',
        params
    })
}

// 更新资料状态
export const updateMaterialStatus = (id, status) => {
    return request({
        url: `/material/${id}/status/${status}`,
        method: 'put'
    })
}

export const swapMaterialOrder = (id, swapId) => {
    return request({
        url: '/material/swap-order',
        method: 'put',
        params: { id, swapId }
    })
}
