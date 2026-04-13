import axios from 'axios'
import { ElMessage } from 'element-plus'

const normalizeImageUrl = (value) => {
    if (typeof value === 'string') {
        return value.replace(/placeholder\.co/gi, 'placehold.co')
    }
    if (Array.isArray(value)) {
        return value.map(item => normalizeImageUrl(item))
    }
    if (value && typeof value === 'object') {
        return Object.fromEntries(
            Object.entries(value).map(([key, item]) => [key, normalizeImageUrl(item)])
        )
    }
    return value
}

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
    withCredentials: true
})

const parseStorageUser = (key) => {
    try {
        const raw = localStorage.getItem(key)
        return raw ? JSON.parse(raw) : {}
    } catch {
        return {}
    }
}

const pickToken = () => {
    const admin = parseStorageUser('admin_user')
    const front = parseStorageUser('front_user')
    const isAdminPage = window.location.pathname.startsWith('/admin')
    if (isAdminPage) {
        return admin?.token || front?.token || ''
    }
    return front?.token || admin?.token || ''
}

// Request interceptor
request.interceptors.request.use(config => {
    config.headers = config.headers || {}
    const token = pickToken()
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    if (config.data instanceof FormData) {
        if (config.headers?.delete) {
            config.headers.delete('Content-Type')
        } else if (config.headers && config.headers['Content-Type']) {
            delete config.headers['Content-Type']
        }
    } else if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    return config
}, error => {
    return Promise.reject(error)
});

// Response interceptor
request.interceptors.response.use(
    response => {
        let res = response.data;
        // If it's a file, return directly
        if (response.config.responseType === 'blob') {
            return res
        }
        // Compatible with String response
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        
        if (res.code === 200) {
            return normalizeImageUrl(res.data);
        } else {
            if (typeof res.message === 'string' && (res.message.includes('请先登录') || res.message.includes('登录已'))) {
                const isAdminPage = window.location.pathname.startsWith('/admin')
                if (isAdminPage) {
                    localStorage.removeItem('admin_user')
                    window.location.href = '/admin/login'
                } else {
                    localStorage.removeItem('front_user')
                    window.location.href = '/login'
                }
            }
            ElMessage.error(res.message || 'Error');
            return Promise.reject(res.message);
        }
    },
    error => {
        console.log('err' + error)
        ElMessage.error(error.message)
        return Promise.reject(error)
    }
)

export default request
