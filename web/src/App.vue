<script setup>
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'
import AiChat from '@/components/AiChat.vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isAdminLayout = computed(() => route.path.startsWith('/admin'))
</script>

<template>
  <div class="app-container">
    <NavBar v-if="!isAdminLayout" />
    <div class="main-content">
      <router-view></router-view>
    </div>
    <Footer v-if="!isAdminLayout" />
    <AiChat v-if="!isAdminLayout" />
  </div>
</template>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
}

/* Global Container Optimization */
.material-container, 
.guide-container, 
.forum-container, 
.cart-container, 
.profile-container,
.post-detail-container,
.order-confirm-container,
.post-edit-container,
.order-container,
.material-detail-container,
.guide-detail-container,
.news-detail-container {
    max-width: 1200px !important;
    margin: 30px auto !important;
    padding: 0 20px !important;
    box-sizing: border-box;
}

/* Card Hover Effects */
.el-card {
    transition: all 0.3s;
}
.el-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

/* Section Headers */
h2 {
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 20px;
}
</style>
