<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px" class="admin-aside">
        <div class="logo">后台管理</div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard" v-if="canViewDashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/users" v-if="canManageUsers">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/materials" v-if="canManageMaterials">
            <el-icon><Files /></el-icon>
            <span>资料管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders" v-if="canManageOrders">
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/news" v-if="canManageContent">
            <el-icon><Document /></el-icon>
            <span>资讯管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/guides" v-if="canManageContent">
            <el-icon><Compass /></el-icon>
            <span>指南管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/forum" v-if="canManageContent">
            <el-icon><ChatLineSquare /></el-icon>
            <span>论坛管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/feedbacks" v-if="canManageFeedback">
            <el-icon><Message /></el-icon>
            <span>反馈管理</span>
          </el-menu-item>
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="admin-topbar">
          <span class="admin-name">{{ adminInfo.nickname || adminInfo.username || '管理员' }}</span>
          <el-button size="small" @click="handleAdminLogout">退出后台</el-button>
        </div>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataLine, User, HomeFilled, Message, Files, Document, Compass, ChatLineSquare, Tickets } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)
const userStore = useUserStore()
const { canViewDashboard, canManageUsers, canManageOrders, canManageMaterials, canManageContent, canManageFeedback, adminInfo } = storeToRefs(userStore)

const handleAdminLogout = () => {
  userStore.logoutAdmin()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.admin-aside {
  background-color: #304156;
  color: #fff;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.el-menu-vertical {
  border-right: none;
}
.admin-topbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}
.admin-name {
  font-size: 14px;
  color: #475569;
}
</style>
