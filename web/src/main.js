import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import { createPinia } from 'pinia'

const normalizeMessageOptions = (options) => {
  if (typeof options === 'string' || typeof options === 'number') {
    return {
      message: String(options),
      duration: 1000
    }
  }
  return {
    ...(options || {}),
    duration: 1000
  }
}

;['success', 'warning', 'info', 'error'].forEach((type) => {
  const original = ElMessage[type]
  ElMessage[type] = (options, appContext) => original(normalizeMessageOptions(options), appContext)
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
