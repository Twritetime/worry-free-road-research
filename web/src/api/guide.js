import request from '@/utils/request'

// 获取指南列表
export const getGuideList = (params) => {
    return request({
        url: '/guide/list',
        method: 'get',
        params
    })
}

// 根据ID获取指南详情
export const getGuideById = (id) => {
    return request({
        url: `/guide/${id}`,
        method: 'get'
    })
}

// 创建指南
export const createGuide = (data) => {
    return request({
        url: '/guide',
        method: 'post',
        data
    })
}

// 更新指南
export const updateGuide = (data) => {
    return request({
        url: '/guide',
        method: 'put',
        data
    })
}

// 删除指南
export const deleteGuide = (id) => {
    return request({
        url: `/guide/${id}`,
        method: 'delete'
    })
}

// 获取所有指南（管理端）
export const getGuideListAll = (params) => {
    return request({
        url: '/guide/list-all',
        method: 'get',
        params
    })
}

// 更新指南状态
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

// 爬取指南
export const crawlGuides = (url, category) => {
    return request({
        url: '/guide/crawl',
        method: 'post',
        params: { url, category },
        timeout: 60000 // 增加超时时间到60秒
    })
}

// 获取定时任务配置
export const getScheduleConfig = () => {
    return request({
        url: '/guide/schedule/config',
        method: 'get'
    })
}

// 手动执行定时任务
export const runScheduleTask = (category) => {
    return request({
        url: '/guide/schedule/run',
        method: 'post',
        params: { category },
        timeout: 60000
    })
}

// 获取所有院校列表
export const getInstitutions = () => {
    return request({
        url: '/guide/institutions',
        method: 'get'
    })
}

// 获取所有专业列表
export const getMajors = () => {
    return request({
        url: '/guide/majors',
        method: 'get'
    })
}
