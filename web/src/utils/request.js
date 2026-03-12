import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// Request interceptor
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
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
            return res.data;
        } else {
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
