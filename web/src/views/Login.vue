<template>
  <div class="auth-container">
    <div class="auth-box">
      <!-- Left Side: Image/Branding -->
      <div class="auth-branding">
        <div class="branding-content">
          <div class="logo-area" @click="$router.push('/')">
             <el-icon class="logo-icon"><School /></el-icon>
             <span class="logo-text">研路无忧</span>
          </div>
          <div class="branding-text">
            <h2 class="branding-title">一站式考研服务平台</h2>
            <p class="branding-subtitle">汇聚海量真题、权威指南、活跃社区<br>助你轻松上岸，研途无忧</p>
          </div>
          <div class="branding-footer">
            <span>© 2024 YanLuWuYou</span>
          </div>
        </div>
        <div class="branding-bg-shape"></div>
      </div>

      <!-- Right Side: Form -->
      <div class="auth-form-wrapper">
        <div class="form-header">
            <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
            <p class="form-subtitle">
                {{ isLogin ? '请登录您的账号以继续' : '填写以下信息注册新账号' }}
            </p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" size="large" class="auth-form">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="form.password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="submitForm"></el-input>
          </el-form-item>
          
          <template v-if="!isLogin">
              <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="form.nickname" placeholder="您的昵称" :prefix-icon="UserFilled"></el-input>
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="手机号码" :prefix-icon="Iphone"></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="邮箱地址" :prefix-icon="Message"></el-input>
              </el-form-item>
          </template>

          <div class="form-actions">
            <el-button type="primary" class="submit-btn" @click="submitForm" :loading="loading">
                {{ isLogin ? '登录' : '立即注册' }}
            </el-button>
          </div>
          
          <div class="form-footer">
            <span>{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
            <a href="javascript:void(0)" @click="toggleMode" class="toggle-link">{{ isLogin ? '立即注册' : '去登录' }}</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { login, register } from '@/api/user'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, UserFilled, Iphone, Message, School } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)

const props = defineProps({
    registerMode: {
        type: Boolean,
        default: false
    }
})

const isLogin = ref(!props.registerMode)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

watch(() => props.registerMode, (val) => {
    isLogin.value = !val
})

const toggleMode = () => {
  isLogin.value = !isLogin.value
  formRef.value.resetFields()
  // Update URL without reloading
  const path = isLogin.value ? '/login' : '/register'
  if (route.path !== path) {
      router.replace(path)
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isLogin.value) {
          const res = await login(form)
          ElMessage.success('登录成功')
          userStore.setUser(res)
          
          if (userStore.isAdmin) {
              router.push('/admin')
          } else {
              router.push('/')
          }
        } else {
          await register(form)
          ElMessage.success('注册成功，请登录')
          isLogin.value = true
          router.replace('/login')
        }
      } catch (error) {
        // Error handled in interceptor
      } finally {
          loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
  background-image: 
    radial-gradient(at 0% 0%, rgba(59, 130, 246, 0.1) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(16, 185, 129, 0.1) 0px, transparent 50%),
    radial-gradient(at 100% 100%, rgba(59, 130, 246, 0.1) 0px, transparent 50%),
    radial-gradient(at 0% 100%, rgba(16, 185, 129, 0.1) 0px, transparent 50%);
  padding: 20px;
}

.auth-box {
  display: flex;
  width: 900px;
  max-width: 100%;
  height: 600px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.auth-branding {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color) 0%, #2563eb 100%);
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.branding-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 60px;
  cursor: pointer;
}

.logo-icon {
    font-size: 32px;
}

.branding-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.branding-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.branding-subtitle {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
}

.branding-footer {
  font-size: 12px;
  opacity: 0.6;
}

.branding-bg-shape {
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  transform: scale(2);
  z-index: 1;
}

.auth-form-wrapper {
  flex: 1;
  padding: 40px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}

.form-header {
    margin-bottom: 30px;
    text-align: center;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

.auth-form :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    padding: 8px 15px;
    border-radius: 8px;
}

.auth-form :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px var(--primary-color) inset;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px var(--primary-color) inset !important;
    border-color: var(--primary-color);
}

.submit-btn {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    border-radius: 8px;
    margin-top: 10px;
    font-weight: 500;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.toggle-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.toggle-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .auth-box {
    flex-direction: column;
    height: auto;
    width: 100%;
  }
  
  .auth-branding {
    padding: 30px;
    min-height: 200px;
  }
  
  .branding-text {
      display: none;
  }
  
  .auth-form-wrapper {
    padding: 30px;
  }
}
</style>
