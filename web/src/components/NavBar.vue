<template>
  <el-header class="header">
    <div class="header-inner">
      <div class="logo" @click="$router.push('/')">
        <el-icon class="logo-icon"><School /></el-icon>
        <span class="logo-text">研路无忧</span>
      </div>
      <div class="nav-menu">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/news" class="nav-link" :class="{ active: $route.path.startsWith('/news') }">考研资讯</router-link>
          <router-link to="/materials" class="nav-link" :class="{ active: $route.path.startsWith('/materials') }">资料商城</router-link>
          <router-link to="/guides" class="nav-link" :class="{ active: $route.path.startsWith('/guides') }">报考指南</router-link>
          <router-link to="/forum" class="nav-link" :class="{ active: $route.path.startsWith('/forum') }">交流论坛</router-link>
      </div>
      <div class="user-info">
        <template v-if="user.id">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link user-profile">
              <el-avatar :size="32" :src="user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span class="username">{{ user.nickname || user.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="canEnterAdmin" @click="$router.push('/admin')">后台管理</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/cart')">购物车</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <div v-else class="auth-buttons">
          <el-button link @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" round size="small" @click="$router.push('/register')">注册</el-button>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { School, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)
const canEnterAdmin = computed(() => ['ADMIN', 'OPERATOR'].includes(user.value?.role))

const logout = () => {
    userStore.logout()
    router.push('/login')
}
</script>

<style scoped>
.header {
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    height: 64px;
    padding: 0;
    position: sticky;
    top: 0;
    z-index: 1000;
}

.header-inner {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}

.logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: opacity 0.2s;
}

.logo:hover {
    opacity: 0.8;
}

.logo-icon {
    font-size: 28px;
    color: var(--primary-color);
}

.logo-text {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);
    letter-spacing: -0.5px;
}

.nav-menu {
    display: flex;
    gap: 40px;
}

.nav-link {
    font-size: 15px;
    color: var(--text-secondary);
    font-weight: 500;
    padding: 8px 0;
    position: relative;
    transition: color 0.2s;
}

.nav-link:hover, .nav-link.active {
    color: var(--primary-color);
}

.nav-link.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: var(--primary-color);
    border-radius: 2px;
}

.user-info {
    display: flex;
    align-items: center;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: var(--text-primary);
    font-size: 14px;
    font-weight: 500;
}

.auth-buttons {
    display: flex;
    gap: 12px;
}
</style>
