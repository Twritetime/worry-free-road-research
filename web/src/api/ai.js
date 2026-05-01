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
