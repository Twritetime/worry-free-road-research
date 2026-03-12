<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">报考指南</h1>
        <p class="page-subtitle">最新最全的考研资讯与政策解读</p>
        
        <div class="search-wrapper">
          <div class="search-group">
            <el-input
              v-model="keyword"
              placeholder="搜索指南关键词..."
              class="search-input"
              size="large"
              @keyup.enter="fetchGuides"
            >
               <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
            <el-select
              v-model="searchInstitution"
              placeholder="报考院校"
              class="search-input-small"
              size="large"
              clearable
              filterable
              @change="fetchGuides"
            >
              <el-option
                v-for="item in institutions"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
            <el-select
              v-model="searchMajor"
              placeholder="报考专业"
              class="search-input-small"
              size="large"
              clearable
              filterable
              @change="fetchGuides"
            >
              <el-option
                v-for="item in majors"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
            <el-button type="primary" size="large" @click="fetchGuides">搜索</el-button>
          </div>
        </div>
      </div>
      <div class="header-bg"></div>
    </div>

    <div class="main-content">
      <div class="content-layout">
        <!-- Sidebar Menu -->
        <div class="sidebar-menu">
            <div 
                v-for="item in menuItems" 
                :key="item.key" 
                class="menu-item"
                :class="{ active: activeCategory === item.key }"
                @click="handleCategoryChange(item.key)"
            >
                <el-icon class="menu-icon"><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
                <el-icon class="active-indicator" v-if="activeCategory === item.key"><ArrowRight /></el-icon>
            </div>
             <div class="admin-action" v-if="isAdmin">
                <el-button type="primary" class="full-width" :icon="Plus" @click="handleAdd">发布指南</el-button>
            </div>
        </div>

        <!-- List Content -->
        <div class="list-content" v-loading="loading">
             <div v-for="item in guideList" :key="item.id" class="guide-card" @click="viewDetail(item.id)">
                <div class="guide-icon-box" :class="item.category">
                    <el-icon><Document /></el-icon>
                </div>
                <div class="guide-info">
                    <h3 class="guide-title">{{ item.title }}</h3>
                    <p class="guide-summary">{{ stripHtml(item.content) }}</p>
                    <div class="guide-meta">
                        <span class="meta-tag">{{ formatCategory(item.category) }}</span>
                        <span v-if="item.institution" class="meta-tag info-tag">{{ item.institution }}</span>
                        <span v-if="item.major" class="meta-tag info-tag">{{ item.major }}</span>
                        <span class="meta-separator">•</span>
                        <span class="meta-date">{{ formatDate(item.createTime) }}</span>
                        <span class="meta-separator">•</span>
                        <span class="meta-views"><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
                    </div>
                </div>
                <div class="guide-arrow">
                    <el-button circle plain><el-icon><ArrowRight /></el-icon></el-button>
                </div>
                 <div class="admin-actions" v-if="isAdmin" @click.stop>
                    <el-button circle size="small" :icon="Edit" @click="handleEdit(item)"></el-button>
                    <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(item)"></el-button>
                </div>
             </div>
             <el-empty v-if="!loading && guideList.length === 0" description="暂无指南" />
             
             <div class="pagination-wrapper">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="total"
                    :page-size="pageSize"
                    @current-change="handlePageChange"
                  />
            </div>
        </div>
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog :title="formTitle" v-model="dialogVisible" width="700px" class="custom-dialog">
        <el-form :model="guideForm" label-width="80px" label-position="top">
            <el-form-item label="标题">
                <el-input v-model="guideForm.title" placeholder="请输入指南标题" size="large"></el-input>
            </el-form-item>
            <div style="display: flex; gap: 20px;">
                <el-form-item label="报考院校" style="flex: 1">
                    <el-input v-model="guideForm.institution" placeholder="例如：北京大学" size="large"></el-input>
                </el-form-item>
                <el-form-item label="报考专业" style="flex: 1">
                    <el-input v-model="guideForm.major" placeholder="例如：计算机科学与技术" size="large"></el-input>
                </el-form-item>
            </div>
            <el-form-item label="分类">
                <el-select v-model="guideForm.category" style="width: 100%" size="large">
                    <el-option v-for="(label, value) in categoryMap" :key="value" :label="label" :value="value"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="内容">
                <el-input type="textarea" v-model="guideForm.content" :rows="12" placeholder="支持Markdown格式..."></el-input>
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
import { getGuideList, createGuide, updateGuide, deleteGuide, getInstitutions, getMajors } from '@/api/guide'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Search, Plus, Document, View, ArrowRight, Edit, Delete, Collection, Reading, List, Memo } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const { isAdmin } = storeToRefs(userStore)

const guideList = ref([])
const loading = ref(false)
const keyword = ref('')
const searchInstitution = ref('')
const searchMajor = ref('')
const institutions = ref([])
const majors = ref([])
const activeCategory = ref('all')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const guideForm = ref({})
const formTitle = ref('发布指南')

const categoryMap = {
  'zhaoshengjianzhang': '招生简章',
  'zhuanyemulu': '专业目录',
  'kaoshidagang': '考试大纲',
  'fushixize': '复试细则'
}

const menuItems = [
    { key: 'all', label: '全部指南', icon: Collection },
    { key: 'zhaoshengjianzhang', label: '招生简章', icon: Document },
    { key: 'zhuanyemulu', label: '专业目录', icon: List },
    { key: 'kaoshidagang', label: '考试大纲', icon: Reading },
    { key: 'fushixize', label: '复试细则', icon: Memo }
]

onMounted(() => {
    fetchGuides()
    fetchOptions()
})

const fetchOptions = async () => {
    try {
        const [instRes, majorRes] = await Promise.all([
            getInstitutions(),
            getMajors()
        ])
        institutions.value = instRes
        majors.value = majorRes
    } catch (error) {
        console.error('Failed to fetch options:', error)
    }
}

const fetchGuides = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value,
            institution: searchInstitution.value,
            major: searchMajor.value,
            category: activeCategory.value === 'all' ? undefined : activeCategory.value
        }
        const res = await getGuideList(params)
        guideList.value = res.records || []
        total.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleCategoryChange = (key) => {
    activeCategory.value = key
    pageNum.value = 1
    fetchGuides()
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchGuides()
}

const viewDetail = (id) => {
    router.push(`/guides/${id}`)
}

const formatCategory = (key) => {
    return categoryMap[key] || key
}

const formatDate = (date) => {
    return dayjs(date).format('YYYY-MM-DD')
}

const stripHtml = (html) => {
    if (!html) return '暂无简介'
    return html.replace(/<[^>]+>/g, '').substring(0, 120) + '...'
}

const handleAdd = () => {
    guideForm.value = { category: 'zhaoshengjianzhang', status: 1 }
    formTitle.value = '发布指南'
    dialogVisible.value = true
}

const handleEdit = (item) => {
    guideForm.value = { ...item }
    formTitle.value = '编辑指南'
    dialogVisible.value = true
}

const handleDelete = (item) => {
    ElMessageBox.confirm('确认删除该指南吗?', '提示', { type: 'warning' })
    .then(async () => {
        await deleteGuide(item.id)
        ElMessage.success('删除成功')
        fetchGuides()
    })
}

const handleSubmit = async () => {
    if (!guideForm.value.title || !guideForm.value.content) {
        ElMessage.warning('请填写完整信息')
        return
    }
    try {
        if (guideForm.value.id) {
            await updateGuide(guideForm.value)
            ElMessage.success('更新成功')
        } else {
            await createGuide(guideForm.value)
            ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        fetchGuides()
    } catch (error) {
        ElMessage.error('操作失败')
    }
}
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    background-color: var(--bg-color);
}

.page-header {
    background: white;
    padding: 60px 0 40px;
    text-align: center;
    position: relative;
    overflow: hidden;
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
    box-shadow: 0 10px 30px rgba(0,0,0,0.05);
    border-radius: 4px;
}

.info-tag {
    background: #f4f4f5;
    color: #909399;
    margin-right: 5px;
}

.search-group {
    display: flex;
    gap: 10px;
}

.search-input-small {
    width: 150px;
}

.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;
}

.content-layout {
    display: flex;
    gap: 30px;
    align-items: flex-start;
}

.sidebar-menu {
    width: 260px;
    background: white;
    border-radius: 12px;
    padding: 15px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.03);
    position: sticky;
    top: 90px;
    flex-shrink: 0;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: 8px;
    cursor: pointer;
    color: var(--text-secondary);
    transition: all 0.3s;
    margin-bottom: 5px;
    font-weight: 500;
}

.menu-item:hover {
    background-color: #f8f9fa;
    color: var(--primary-color);
}

.menu-item.active {
    background-color: #eff6ff;
    color: var(--primary-color);
}

.menu-icon {
    font-size: 18px;
}

.active-indicator {
    margin-left: auto;
    font-size: 14px;
}

.admin-action {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f1f5f9;
}

.full-width {
    width: 100%;
}

.list-content {
    flex: 1;
}

.guide-card {
    background: white;
    border-radius: 12px;
    padding: 25px;
    margin-bottom: 20px;
    display: flex;
    gap: 20px;
    cursor: pointer;
    transition: all 0.3s;
    border: 1px solid transparent;
    position: relative;
}

.guide-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 30px rgba(0,0,0,0.05);
    border-color: #f1f5f9;
}

.guide-icon-box {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    background: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: var(--text-secondary);
    flex-shrink: 0;
}

.guide-icon-box.zhaoshengjianzhang { background: #e0f2fe; color: #0284c7; }
.guide-icon-box.zhuanyemulu { background: #dcfce7; color: #16a34a; }
.guide-icon-box.kaoshidagang { background: #fef9c3; color: #ca8a04; }
.guide-icon-box.fushixize { background: #fee2e2; color: #dc2626; }

.guide-info {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.guide-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 10px;
    line-height: 1.4;
}

.guide-summary {
    font-size: 14px;
    color: var(--text-secondary);
    margin-bottom: 15px;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.guide-meta {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #94a3b8;
    margin-top: auto;
}

.meta-tag {
    color: var(--primary-color);
    background: #eff6ff;
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: 500;
}

.meta-separator {
    margin: 0 10px;
    color: #cbd5e1;
}

.guide-arrow {
    display: flex;
    align-items: center;
    opacity: 0;
    transition: opacity 0.3s;
}

.guide-card:hover .guide-arrow {
    opacity: 1;
}

.admin-actions {
    position: absolute;
    top: 15px;
    right: 15px;
    opacity: 0;
    transition: opacity 0.3s;
}

.guide-card:hover .admin-actions {
    opacity: 1;
}

.pagination-wrapper {
    margin-top: 40px;
    display: flex;
    justify-content: center;
}

@media (max-width: 768px) {
    .content-layout {
        flex-direction: column;
    }
    .sidebar-menu {
        width: 100%;
        position: static;
        margin-bottom: 20px;
    }
    .guide-card {
        flex-direction: column;
    }
    .guide-icon-box {
        width: 40px;
        height: 40px;
        font-size: 18px;
    }
}
</style>
