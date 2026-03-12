import request from '@/utils/request'

// 获取购物车列表
export const getCartList = (userId) => {
    return request({
        url: '/cart/list',
        method: 'get',
        params: { userId }
    })
}

// 添加到购物车
export const addToCart = (data) => {
    return request({
        url: '/cart',
        method: 'post',
        data
    })
}

// 更新购物车
export const updateCartItem = (data) => {
    return request({
        url: '/cart',
        method: 'put',
        data
    })
}

// 删除购物车项
export const deleteCartItem = (id) => {
    return request({
        url: `/cart/${id}`,
        method: 'delete'
    })
}

// 创建订单
export const createOrder = (data) => {
    return request({
        url: '/order/create',
        method: 'post',
        data
    })
}

// 获取订单列表
export const getOrderList = (userId) => {
    return request({
        url: '/order/list',
        method: 'get',
        params: { userId }
    })
}

// 支付订单
export const payOrder = (id) => {
    return request({
        url: `/order/pay/${id}`,
        method: 'post'
    })
}

// 检查是否已购买
export const checkPurchased = (userId, materialId) => {
    return request({
        url: '/order/check-purchased',
        method: 'get',
        params: { userId, materialId }
    })
}

// 获取所有订单（管理端）
export const getAllOrders = (params) => {
    return request({
        url: '/order/list-all',
        method: 'get',
        params
    })
}

// 更新订单状态
export const updateOrderStatus = (id, status) => {
    return request({
        url: `/order/${id}/status/${status}`,
        method: 'put'
    })
}

// 清空购物车
export const clearCart = (userId) => {
    return request({
        url: '/cart/clear',
        method: 'delete',
        params: { userId }
    })
}
