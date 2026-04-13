<template>
  <div class="admin-login-container">
    <div class="admin-login-card">
      <div class="card-header">
        <div class="brand" @click="$router.push('/')">
          <el-icon class="brand-icon"><Setting /></el-icon>
          <span>后台管理</span>
        </div>
        <h2>管理员登录</h2>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            :prefix-icon="Lock"
            @keyup.enter="submitForm"
          />
        </el-form-item>

        <el-button type="primary" class="submit-btn" :loading="loading" @click="submitForm">登录后台</el-button>

        <div class="footer-links">
          <router-link to="/login" class="link">前台登录</router-link>
          <router-link to="/" class="link">返回首页</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const formRef = ref(null)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const getRedirectPath = () => {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
  if (redirect.startsWith('/admin')) {
    return redirect
  }
  return '/admin/dashboard'
}

const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    if (!['ADMIN', 'OPERATOR'].includes(res?.role)) {
      ElMessage.error('当前账号无后台权限')
      return
    }
    userStore.setAdminUser(res)
    ElMessage.success('登录成功')
    router.push(getRedirectPath())
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at 20% 20%, rgba(64, 158, 255, 0.18), transparent 45%),
    radial-gradient(circle at 80% 80%, rgba(80, 200, 120, 0.16), transparent 40%),
    linear-gradient(140deg, #f4f8ff 0%, #eef5ff 100%);
}

.admin-login-card {
  width: 420px;
  max-width: 100%;
  background: #fff;
  border-radius: 16px;
  padding: 28px 26px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.card-header {
  text-align: center;
  margin-bottom: 20px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #2563eb;
  font-weight: 600;
  cursor: pointer;
}

.brand-icon {
  font-size: 18px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #0f172a;
}

.card-header p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 14px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.submit-btn {
  width: 100%;
  margin-top: 6px;
  padding: 12px 0;
  font-size: 15px;
  border-radius: 8px;
}

.footer-links {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.link {
  color: #2563eb;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}
</style>
