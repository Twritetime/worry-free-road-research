import request from '@/utils/request'

export function sendChatMessage(content) {
    return request({
        url: '/ai/chat',
        method: 'post',
        data: content
    })
}

export function getChatHistory(params) {
    return request({
        url: '/ai/history',
        method: 'get',
        params
    })
}

export function clearChatHistory(sessionId) {
    return request({
        url: '/ai/history',
        method: 'delete',
        params: { sessionId }
    })
}

export function getAdminChatList(params) {
    return request({
        url: '/ai/admin/list',
        method: 'get',
        params
    })
}

export function getAdminChatSession(sessionId) {
    return request({
        url: '/ai/admin/session',
        method: 'get',
        params: { sessionId }
    })
}

export function deleteAdminChatSession(sessionId) {
    return request({
        url: '/ai/admin/session',
        method: 'delete',
        params: { sessionId }
    })
}

export function deleteAdminChatMessage(id) {
    return request({
        url: '/ai/admin/message',
        method: 'delete',
        params: { id }
    })
}

export function getAdminChatStats() {
    return request({
        url: '/ai/admin/stats',
        method: 'get'
    })
}
