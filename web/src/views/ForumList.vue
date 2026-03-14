<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">交流论坛</h1>
        <p class="page-subtitle">畅所欲言，分享经验，互助成长</p>
        
        <div class="search-wrapper">
          <el-input
            v-model="keyword"
            placeholder="搜索帖子..."
            class="search-input"
            size="large"
            @keyup.enter="fetchPosts"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="fetchPosts">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
      <div class="header-bg"></div>
    </div>

    <div class="main-content">
       <div class="filter-bar">
          <div class="filter-tabs">
            <div 
                class="filter-tab" 
                :class="{ active: activeCategory === '' }"
                @click="handleCategoryChange('')"
            >
                全部
            </div>
            <div 
                v-for="(label, value) in categoryMap" 
                :key="value"
                class="filter-tab" 
                :class="{ active: activeCategory == value }"
                @click="handleCategoryChange(value)"
            >
                {{ label }}
            </div>
          </div>
          <div class="flex-spacer"></div>
          <el-button type="primary" :icon="EditPen" @click="handleCreate" class="create-btn">发布帖子</el-button>
       </div>

       <div class="post-list" v-loading="loading">
          <div v-for="item in postList" :key="item.id" class="post-card" @click="viewDetail(item.id)">
             <div class="post-main">
                 <div class="post-header">
                     <el-tag size="small" :type="getCategoryTag(item.category)" effect="light">{{ getCategoryLabel(item.category) }}</el-tag>
                     <h3 class="post-title">{{ item.title }}</h3>
                     <el-tag v-if="item.isTop" type="danger" effect="dark" size="small" class="top-tag">置顶</el-tag>
                 </div>
                 <p class="post-summary">{{ getSummary(item.content) }}</p>
                 <div class="post-meta">
                    <div class="author-info">
                        <el-avatar :size="24" :src="item.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                        <span class="author-name">{{ item.nickname || '匿名用户' }}</span>
                    </div>
                    <span class="meta-separator">•</span>
                    <span class="post-date">{{ formatDate(item.createTime) }}</span>
                 </div>
             </div>
             
             <div class="post-stats">
                 <div class="stat-item">
                     <el-icon><View /></el-icon>
                     <span>{{ item.viewCount || 0 }}</span>
                 </div>
                 <div class="stat-item">
                     <el-icon><ChatDotRound /></el-icon>
                     <span>{{ item.commentCount || 0 }}</span>
                 </div>
                 <div class="stat-item">
                     <el-icon><Star /></el-icon>
                     <span>{{ item.likeCount || 0 }}</span>
                 </div>
             </div>
             
             <div class="admin-actions" v-if="isAdmin" @click.stop>
                <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(item)"></el-button>
            </div>
          </div>
          <el-empty v-if="!loading && postList.length === 0" description="暂无帖子" />
       </div>

        <div class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="pageNum"
            @current-change="handlePageChange"
          />
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPostList, deletePost } from '@/api/post'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Search, EditPen, View, ChatDotRound, Star, Delete } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const { isAdmin, isLoggedIn } = storeToRefs(userStore)

const postList = ref([])
const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeCategory = ref('')

const categoryMap = {
    1: '公共课交流',
    2: '专业课交流',
    3: '院校复试经验',
    4: '其他'
}

onMounted(() => {
    fetchPosts()
})

const fetchPosts = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value,
            category: activeCategory.value || undefined
        }
        const res = await getPostList(params)
        postList.value = res.records || []
        total.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleCategoryChange = (category) => {
    activeCategory.value = category
    pageNum.value = 1
    fetchPosts()
}

const getCategoryLabel = (category) => {
    return categoryMap[category] || '其他'
}

const getCategoryTag = (category) => {
    const map = {
        1: '',
        2: 'success',
        3: 'warning',
        4: 'info'
    }
    return map[category] || 'info'
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchPosts()
}

const viewDetail = (id) => {
    router.push(`/posts/${id}`)
}

const handleCreate = () => {
    if (!isLoggedIn.value) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    router.push('/posts/create')
}

const handleDelete = (item) => {
    ElMessageBox.confirm('确认删除该帖子吗?', '提示', { type: 'warning' })
    .then(async () => {
        await deletePost(item.id)
        ElMessage.success('删除成功')
        fetchPosts()
    })
}

const formatDate = (date) => {
    if (!date) return ''
    return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getSummary = (content) => {
    if (!content) return '暂无内容'
    return content.substring(0, 100).replace(/<[^>]+>/g, '') + '...'
}
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    background:
      radial-gradient(circle at 0% -15%, rgba(99, 102, 241, 0.16), transparent 45%),
      radial-gradient(circle at 100% 12%, rgba(59, 130, 246, 0.14), transparent 38%),
      linear-gradient(180deg, #f9fbff 0%, #f4f8ff 44%, #f8fbff 100%);
}

.page-header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    padding: 60px 0 40px;
    position: relative;
    overflow: hidden;
    text-align: center;
    margin-bottom: 30px;
}

.header-content {
    position: relative;
    z-index: 2;
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
}

.page-title {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 10px;
    color: var(--text-primary);
}

.page-subtitle {
    font-size: 16px;
    color: var(--text-secondary);
    margin-bottom: 30px;
}

.search-wrapper {
    max-width: 600px;
    margin: 0 auto;
}

.search-input :deep(.el-input__wrapper) {
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    border-radius: 8px 0 0 8px;
}

.search-input :deep(.el-input-group__append) {
    background-color: #fff;
    border-radius: 0 8px 8px 0;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: radial-gradient(circle at 20% 150%, rgba(99,102,241,0.12) 0%, transparent 52%),
                      radial-gradient(circle at 80% -50%, rgba(59,130,246,0.12) 0%, transparent 52%);
    z-index: 1;
}

.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;
    position: relative;
    z-index: 3;
}

.filter-bar {
    background: white;
    padding: 20px 30px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
}

.filter-tabs {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.filter-tab {
    padding: 8px 20px;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    color: #606266;
    transition: all 0.3s;
    background: #f5f7fa;
}

.filter-tab:hover {
    color: var(--el-color-primary);
    background: #ecf5ff;
}

.filter-tab.active {
    background: var(--el-color-primary);
    color: white;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.flex-spacer {
    flex: 1;
}

.post-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.post-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    transition: all 0.3s ease;
    cursor: pointer;
    position: relative;
    gap: 20px;
}

.post-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.post-main {
    flex: 1;
    min-width: 0;
}

.post-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;
}

.post-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.top-tag {
    flex-shrink: 0;
}

.post-summary {
    color: #606266;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.post-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
    color: #909399;
}

.author-info {
    display: flex;
    align-items: center;
    gap: 6px;
}

.meta-separator {
    color: #e4e7ed;
}

.post-stats {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: flex-end;
    min-width: 60px;
    border-left: 1px solid #f0f2f5;
    padding-left: 20px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #909399;
    font-size: 13px;
}

.admin-actions {
    position: absolute;
    top: 16px;
    right: 16px;
    opacity: 0;
    transition: opacity 0.3s;
}

.post-card:hover .admin-actions {
    opacity: 1;
}

.pagination-wrapper {
    margin-top: 40px;
    display: flex;
    justify-content: center;
}

@media (max-width: 768px) {
    .post-card {
        flex-direction: column;
    }
    
    .post-stats {
        flex-direction: row;
        width: 100%;
        border-left: none;
        padding-left: 0;
        border-top: 1px solid #f0f2f5;
        padding-top: 12px;
        margin-top: 12px;
        justify-content: space-around;
    }
    
    .filter-bar {
        padding: 16px;
    }
}
</style>
