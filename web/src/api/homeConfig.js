import request from '@/utils/request'

/**
 * 获取首页配置（分组返回）
 */
export const getHomeConfigs = () => {
  return request.get('/home-config')
}

/**
 * 根据类型获取配置列表
 * @param {string} type - 配置类型：banner, notice, hot, recommend
 */
export const getConfigsByType = (type) => {
  return request.get(`/home-config/type/${type}`)
}

/**
 * 获取所有配置列表（管理后台用）
 * @param {string} type - 可选，按类型筛选
 */
export const getAllConfigs = (type) => {
  const params = type ? { type } : {}
  return request.get('/home-config/list', { params })
}

/**
 * 获取单个配置详情
 * @param {number} id - 配置ID
 */
export const getConfigById = (id) => {
  return request.get(`/home-config/${id}`)
}

/**
 * 创建配置
 * @param {object} config - 配置对象
 */
export const createConfig = (config) => {
  return request.post('/home-config', config)
}

/**
 * 更新配置
 * @param {object} config - 配置对象
 */
export const updateConfig = (config) => {
  return request.put('/home-config', config)
}

/**
 * 更新配置状态
 * @param {number} id - 配置ID
 * @param {number} status - 状态值：1启用，0禁用
 */
export const updateConfigStatus = (id, status) => {
  return request.put(`/home-config/${id}/status/${status}`)
}

/**
 * 交换排序顺序
 * @param {number} id - 当前配置ID
 * @param {number} swapId - 目标配置ID
 */
export const swapOrder = (id, swapId) => {
  return request.put('/home-config/swap-order', null, { params: { id, swapId } })
}

/**
 * 删除配置
 * @param {number} id - 配置ID
 */
export const deleteConfig = (id) => {
  return request.delete(`/home-config/${id}`)
}
