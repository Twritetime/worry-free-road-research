<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">考研资讯</h1>
        <p class="page-subtitle">实时掌握考研动态，把握上岸先机</p>
        
        <div class="search-wrapper">
          <el-input
            v-model="keyword"
            placeholder="搜索资讯..."
            class="search-input"
            size="large"
            @keyup.enter="fetchNews"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="fetchNews">搜索</el-button>
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
                :class="{ active: activeType === '' }"
                @click="handleTypeChange('')"
            >
                全部
            </div>
            <div
                v-for="item in NEWS_TYPE_OPTIONS"
                :key="item.value"
                class="filter-tab"
                :class="{ active: activeType === item.value }"
                @click="handleTypeChange(item.value)"
            >
                {{ item.label }}
            </div>
          </div>
          <div class="flex-spacer"></div>
          <div class="sort-wrapper">
            <span class="sort-label">排序：</span>
            <el-select v-model="sortBy" size="default" style="width: 140px;" @change="handleSortChange">
              <el-option label="按发布时间" value="createTime" />
              <el-option label="按浏览量" value="viewCount" />
              <el-option label="按评论数" value="commentCount" />
            </el-select>
            <el-button-group class="sort-order">
              <el-button :type="sortOrder === 'desc' ? 'primary' : 'default'" @click="handleSortOrderChange('desc')">
                <el-icon><SortDown /></el-icon>
              </el-button>
              <el-button :type="sortOrder === 'asc' ? 'primary' : 'default'" @click="handleSortOrderChange('asc')">
                <el-icon><SortUp /></el-icon>
              </el-button>
            </el-button-group>
          </div>
          <el-button v-if="isAdmin" type="primary" :icon="Plus" @click="handleAdd">发布资讯</el-button>
       </div>

       <div class="news-list" v-loading="loading">
          <div v-for="item in newsList" :key="item.id" class="news-card" @click="viewDetail(item.id)">
             <div class="news-cover">
                 <el-image
                   :src="item.coverImg || 'https://placehold.co/300x200?text=News'"
                   :preview-src-list="[item.coverImg]"
                   fit="cover"
                   :preview-teleported="true"
                   class="news-img"
                   @click.stop
                 />
             </div>
             <div class="news-content">
                <div class="news-header">
                    <el-tag size="small" :type="getTypeTag(item.type)" effect="light">{{ getTypeLabel(item.type) }}</el-tag>
                    <span class="news-date">{{ formatDate(item.createTime) }}</span>
                </div>
                <h3 class="news-title">{{ item.title }}</h3>
                <p class="news-summary">{{ getSummary(item.content) }}</p>
                <div class="news-footer">
                    <span class="news-views"><el-icon><View /></el-icon> {{ item.viewCount || 0 }} 阅读</span>
                    <el-button link type="primary" class="read-more">阅读全文 <el-icon><ArrowRight /></el-icon></el-button>
                </div>
             </div>
             
             <div class="admin-actions" v-if="isAdmin" @click.stop>
                <el-button circle size="small" :icon="Edit" @click="handleEdit(item)"></el-button>
                <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(item)"></el-button>
            </div>
          </div>
          <el-empty v-if="!loading && newsList.length === 0" description="暂时没有相关信息" />
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

    <!-- Dialog -->
    <el-dialog :title="formTitle" v-model="dialogVisible" width="700px" class="custom-dialog">
        <el-form :model="newsForm" label-width="80px" label-position="top">
            <el-form-item label="标题">
                <el-input v-model="newsForm.title" placeholder="请输入标题" size="large"></el-input>
            </el-form-item>
            <el-row :gutter="20">
                <el-col :span="12">
                     <el-form-item label="类型">
                        <el-select v-model="newsForm.type" style="width: 100%" size="large">
                            <el-option v-for="item in NEWS_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
           
            <el-form-item label="内容">
                <el-input type="textarea" v-model="newsForm.content" :rows="10" placeholder="支持Markdown格式..."></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleSubmit" size="large">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewsList, createNews, updateNews, deleteNews } from '@/api/news'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Search, Plus, Edit, Delete, View, ArrowRight, SortDown, SortUp } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const { isAdmin } = storeToRefs(userStore)

const newsList = ref([])
const loading = ref(false)
const keyword = ref('')
const activeType = ref('')
const sortBy = ref('createTime')
const sortOrder = ref('desc')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const newsForm = ref({})
const formTitle = ref('发布资讯')
const NEWS_TYPE_OPTIONS = [
    { label: '报考指南', value: '报考' },
    { label: '政策解读', value: '政策' },
    { label: '备考经验', value: '经验' },
    { label: '复试调剂', value: '复试调剂' }
]
const NEWS_TYPE_LABEL_MAP = NEWS_TYPE_OPTIONS.reduce((map, item) => {
    map[item.value] = item.label
    return map
}, {})

onMounted(() => {
    fetchNews()
})

const fetchNews = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value,
            type: activeType.value || undefined,
            sortBy: sortBy.value,
            sortOrder: sortOrder.value
        }
        const res = await getNewsList(params)
        newsList.value = res.records || []
        total.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleSortChange = () => {
    pageNum.value = 1
    fetchNews()
}

const handleSortOrderChange = (order) => {
    sortOrder.value = order
    pageNum.value = 1
    fetchNews()
}

const handleTypeChange = (type) => {
    activeType.value = type
    pageNum.value = 1
    fetchNews()
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchNews()
}

const viewDetail = (id) => {
    router.push(`/news/${id}`)
}

const getTypeTag = (type) => {
    const map = {
        '报考': 'primary',
        '政策': 'warning',
        '经验': 'success',
        '复试调剂': 'danger'
    }
    return map[type] || 'primary'
}

const getTypeLabel = (type) => {
    return NEWS_TYPE_LABEL_MAP[type] || type
}

const formatDate = (date) => {
    if (!date) return ''
    return dayjs(date).format('YYYY-MM-DD')
}

const getSummary = (content) => {
    if (!content) return '暂无内容'
    return content.substring(0, 120).replace(/<[^>]+>/g, '') + '...'
}

const handleAdd = () => {
    newsForm.value = { type: NEWS_TYPE_OPTIONS[0].value, status: 1 }
    formTitle.value = '发布资讯'
    dialogVisible.value = true
}

const handleEdit = (item) => {
    newsForm.value = { ...item }
    formTitle.value = '编辑资讯'
    dialogVisible.value = true
}

const handleDelete = (item) => {
    ElMessageBox.confirm('确认删除该资讯吗?', '提示', { type: 'warning' })
    .then(async () => {
        await deleteNews(item.id)
        ElMessage.success('删除成功')
        fetchNews()
    })
}

const handleSubmit = async () => {
    if (!newsForm.value.title || !newsForm.value.content) {
        ElMessage.warning('请填写完整信息')
        return
    }
    try {
        if (newsForm.value.id) {
            await updateNews(newsForm.value)
            ElMessage.success('更新成功')
        } else {
            await createNews(newsForm.value)
            ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        fetchNews()
    } catch (error) {
        ElMessage.error('操作失败')
    }
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
}

.filter-tab {
    padding: 8px 24px;
    border-radius: 20px;
    cursor: pointer;
    font-size: 15px;
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

.sort-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
}

.sort-label {
    color: #606266;
    font-size: 14px;
}

.sort-order {
    margin-left: 4px;
}

.news-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.news-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
    display: flex;
    transition: all 0.3s ease;
    cursor: pointer;
    position: relative;
    height: 200px;
}

.news-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.news-cover {
    width: 300px;
    flex-shrink: 0;
    overflow: hidden;
}

.news-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.news-card:hover .news-cover img {
    transform: scale(1.05);
}

.news-content {
    flex: 1;
    padding: 24px;
    display: flex;
    flex-direction: column;
}

.news-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}

.news-date {
    color: #909399;
    font-size: 13px;
}

.news-title {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 12px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.news-summary {
    color: #606266;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: auto;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.news-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
}

.news-views {
    color: #909399;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.admin-actions {
    position: absolute;
    top: 16px;
    right: 16px;
    opacity: 0;
    transition: opacity 0.3s;
}

.news-card:hover .admin-actions {
    opacity: 1;
}

.pagination-wrapper {
    margin-top: 40px;
    display: flex;
    justify-content: center;
}

@media (max-width: 768px) {
    .news-card {
        flex-direction: column;
        height: auto;
    }
    
    .news-cover {
        width: 100%;
        height: 180px;
    }
    
    .news-content {
        padding: 16px;
    }
    
    .filter-bar {
        padding: 16px;
    }
}
</style>
