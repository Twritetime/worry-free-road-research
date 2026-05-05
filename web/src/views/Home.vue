<template>
  <div class="home-container">
      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-content">
            <h1 class="hero-title">研路无忧，一站式考研服务平台</h1>
            <p class="hero-subtitle">汇聚海量真题资料、权威报考指南、活跃交流社区，助你轻松上岸</p>
            <div class="hero-actions">
                <el-button type="primary" size="large" class="cta-btn" @click="$router.push('/materials')">
                    立即探索 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
                </el-button>
                <el-button size="large" class="secondary-btn" @click="$router.push('/guides')">
                    查看指南
                </el-button>
            </div>
            
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-num">10k+</span>
                    <span class="stat-label">资料下载</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                    <span class="stat-num">5k+</span>
                    <span class="stat-label">活跃用户</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                    <span class="stat-num">98%</span>
                    <span class="stat-label">好评率</span>
                </div>
            </div>
        </div>
        <div class="hero-image">
            <!-- Decorative elements representing study/success -->
            <div class="floating-card card-1">
                <el-icon><Document /></el-icon>
                <span>真题资料</span>
            </div>
            <div class="floating-card card-2">
                <el-icon><ChatDotRound /></el-icon>
                <span>经验交流</span>
            </div>
            <div class="floating-card card-3">
                <el-icon><Trophy /></el-icon>
                <span>成功上岸</span>
            </div>
            <div class="hero-bg-circle"></div>
        </div>
      </section>

      <div class="content-wrapper">
        <el-row :gutter="40">
            <!-- Left Column: News -->
            <el-col :xs="24" :lg="16">
                <div class="section-header">
                    <h2 class="section-title">最新资讯</h2>
                    <div class="header-actions">
                        <el-button v-if="isFrontAdmin" type="primary" link @click="handleAddNews">发布资讯</el-button>
                        <el-button link @click="$router.push('/news')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
                    </div>
                </div>
                
                <div class="news-list" v-loading="loading">
                    <div v-for="item in newsList" :key="item.id" class="news-card" @click="viewNews(item.id)">
                        <div class="news-content">
                            <div class="news-badges">
                                <el-tag size="small" :type="getTypeTag(item.type)" effect="plain" round>{{ getTypeLabel(item.type) }}</el-tag>
                                <span class="news-date">{{ formatDate(item.createTime) }}</span>
                            </div>
                            <h3 class="news-title">{{ item.title }}</h3>
                            <p class="news-excerpt">{{ stripHtml(item.content).substring(0, 100) }}...</p>
                        </div>
                        <div v-if="isFrontAdmin" class="admin-actions" @click.stop>
                            <el-button circle size="small" @click="handleEditNews(item)"><el-icon><Edit /></el-icon></el-button>
                            <el-button circle size="small" type="danger" @click="handleDeleteNews(item)"><el-icon><Delete /></el-icon></el-button>
                        </div>
                    </div>
                    <el-empty v-if="!loading && newsList.length === 0" description="暂无资讯"></el-empty>
                </div>
            </el-col>
            
            <!-- Right Column: Ranking & Quick Links -->
            <el-col :xs="24" :lg="8">
                <div class="sidebar-section">
                    <h3 class="sidebar-title">热门推荐</h3>
                    <div class="ranking-list">
                        <div v-for="(item, index) in hotRecommendations" :key="item.id" class="ranking-item" @click="viewNews(item.id)">
                            <span class="rank-num" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                            <div class="rank-info">
                                <span class="rank-title">{{ item.title }}</span>
                                <span class="rank-heat">🔥 {{ item.hot }} 热度</span>
                            </div>
                        </div>
                        <el-empty v-if="!loading && hotRecommendations.length === 0" description="暂无热门推荐"></el-empty>
                    </div>
                </div>

                <div class="sidebar-section" style="margin-top: 30px;">
                    <h3 class="sidebar-title">快速通道</h3>
                    <div class="quick-links">
                        <div class="quick-link-item" @click="$router.push('/materials?category=public')">
                            <div class="icon-box public"><el-icon><Collection /></el-icon></div>
                            <span>公共课资料</span>
                        </div>
                        <div class="quick-link-item" @click="$router.push('/materials?category=major')">
                            <div class="icon-box major"><el-icon><Reading /></el-icon></div>
                            <span>专业课资料</span>
                        </div>
                        <div class="quick-link-item" @click="$router.push('/materials?category=interview')">
                            <div class="icon-box interview"><el-icon><Microphone /></el-icon></div>
                            <span>复试资料</span>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
      </div>

      <!-- 资料商城 Section -->
      <div class="content-wrapper" v-if="materialList.length > 0">
        <div class="section-header">
            <h2 class="section-title">热门资料</h2>
            <div class="header-actions">
                <el-button v-if="isFrontAdmin" type="primary" link @click="$router.push('/materials')">管理资料</el-button>
                <el-button link @click="$router.push('/materials')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
            </div>
        </div>
        <div class="material-grid">
            <div v-for="item in materialList" :key="item.id" class="material-card" @click="$router.push(`/materials/${item.id}`)">
                <div class="material-image">
                    <el-image :src="item.coverImg || 'https://placehold.co/300x200?text=Material'" fit="cover" class="material-img" />
                    <span v-if="item.fileFormat" class="format-badge">{{ item.fileFormat }}</span>
                </div>
                <div class="material-info">
                    <h4 class="material-name">{{ item.name }}</h4>
                    <div class="material-meta">
                        <span class="material-price">¥{{ item.price }}</span>
                        <span v-if="item.originalPrice && item.originalPrice > item.price" class="material-origin">¥{{ item.originalPrice }}</span>
                    </div>
                </div>
            </div>
        </div>
      </div>

      <!-- 报考指南 Section -->
      <div class="content-wrapper" v-if="guideList.length > 0">
        <div class="section-header">
            <h2 class="section-title">报考指南</h2>
            <div class="header-actions">
                <el-button v-if="isFrontAdmin" type="primary" link @click="$router.push('/guides')">管理指南</el-button>
                <el-button link @click="$router.push('/guides')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
            </div>
        </div>
        <div class="guide-grid">
            <div v-for="item in guideList" :key="item.id" class="guide-card" @click="$router.push(`/guides/${item.id}`)">
                <div class="guide-icon">
                    <el-icon><Document /></el-icon>
                </div>
                <div class="guide-content">
                    <h4 class="guide-title">{{ item.title }}</h4>
                    <p class="guide-meta">{{ item.institution || '未知院校' }} · {{ item.major || '未知专业' }}</p>
                    <el-tag v-if="item.category" size="small">{{ getGuideCategoryLabel(item.category) }}</el-tag>
                </div>
            </div>
        </div>
      </div>

      <!-- 交流论坛 Section -->
      <div class="content-wrapper" v-if="postList.length > 0">
        <div class="section-header">
            <h2 class="section-title">热门帖子</h2>
            <div class="header-actions">
                <el-button v-if="isFrontAdmin" type="primary" link @click="$router.push('/forum')">管理帖子</el-button>
                <el-button link @click="$router.push('/forum')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
            </div>
        </div>
        <div class="forum-list">
            <div v-for="item in postList" :key="item.id" class="forum-card" @click="handlePostClick(item.id)">
                <div class="forum-content">
                    <h4 class="forum-title">{{ item.title }}</h4>
                    <div class="forum-meta">
                        <span class="forum-author">{{ item.authorName || item.author || '匿名用户' }}</span>
                        <span class="forum-time">{{ formatDate(item.createTime) }}</span>
                    </div>
                </div>
                <div class="forum-stats">
                    <span><el-icon><ChatLineSquare /></el-icon> {{ item.commentCount || 0 }}</span>
                    <span>👍 {{ item.likeCount || 0 }}</span>
                </div>
            </div>
        </div>
      </div>

        <!-- Dialogs remain similar but with better styling classes if needed -->
        <el-dialog :title="formTitle" v-model="dialogVisible" width="600px" class="custom-dialog">
            <el-form :model="newsForm" label-width="80px" label-position="top">
                <el-form-item label="标题">
                    <el-input v-model="newsForm.title" placeholder="请输入资讯标题"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                    <el-select v-model="newsForm.type" style="width: 100%">
                        <el-option v-for="item in NEWS_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="内容">
                    <el-input type="textarea" v-model="newsForm.content" :rows="8" placeholder="请输入内容..."></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </template>
        </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Document, ChatDotRound, Trophy, Edit, Delete, Collection, Reading, Microphone, Folder, ChatLineSquare } from '@element-plus/icons-vue'
import { getNewsList, createNews, updateNews, deleteNews } from '@/api/news'
import { getMaterialList } from '@/api/material'
import { getGuideList } from '@/api/guide'
import { getPostList } from '@/api/post'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const { isFrontAdmin, user } = storeToRefs(userStore)

const newsList = ref([])
const materialList = ref([])
const guideList = ref([])
const postList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const newsForm = ref({})
const formTitle = ref('发布资讯')
const NEWS_TYPE_OPTIONS = [
    { label: '报考指南', value: 'notice' },
    { label: '政策解读', value: 'policy' },
    { label: '备考活动', value: 'activity' },
    { label: '其他资讯', value: 'other' }
]
const NEWS_TYPE_LABEL_MAP = NEWS_TYPE_OPTIONS.reduce((map, item) => {
    map[item.value] = item.label
    return map
}, {})
const hotRecommendations = computed(() =>
    (newsList.value || []).slice(0, 5).map((item, index) => ({
        ...item,
        hot: item.viewCount || Math.max(900 - index * 80, 500)
    }))
)

onMounted(() => {
    fetchNews()
    fetchMaterials()
    fetchGuides()
    fetchPosts()
})

const fetchNews = async () => {
    loading.value = true
    try {
        const res = await getNewsList({ pageNum: 1, pageSize: 6 })
        newsList.value = res.records || []
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const fetchMaterials = async () => {
    try {
        const res = await getMaterialList({ pageNum: 1, pageSize: 4 })
        materialList.value = res.records || []
    } catch (error) {
        console.error(error)
    }
}

const fetchGuides = async () => {
    try {
        const res = await getGuideList({ pageNum: 1, pageSize: 4 })
        guideList.value = res.records || []
    } catch (error) {
        console.error(error)
    }
}

const fetchPosts = async () => {
    try {
        const res = await getPostList({ pageNum: 1, pageSize: 5 })
        postList.value = res.records || []
    } catch (error) {
        console.error(error)
    }
}

const handlePostClick = (id) => {
    if (!user.value?.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    router.push(`/forum/${id}`)
}

const viewNews = (id) => {
    router.push(`/news/${id}`)
}

const getTypeTag = (type) => {
    const map = {
        '报考': 'primary',
        '政策': 'warning',
        '经验': 'success',
        '复试调剂': 'danger'
    }
    return map[type] || 'info'
}

const getTypeLabel = (type) => {
    return NEWS_TYPE_LABEL_MAP[type] || type
}

const formatDate = (dateStr) => {
    if (!dateStr) return ''
    return dateStr.split(' ')[0]
}

const getGuideCategoryLabel = (category) => {
    const map = {
        'zhaoshengjianzhang': '招生简章',
        'zhuanyemulu': '专业目录',
        'kaoshidagang': '考试大纲',
        'fushixize': '复试细则'
    }
    return map[category] || category
}

const stripHtml = (html) => {
   let tmp = document.createElement("DIV");
   tmp.innerHTML = html;
   return tmp.textContent || tmp.innerText || "";
}

const handleAddNews = () => {
    newsForm.value = { type: NEWS_TYPE_OPTIONS[0].value, status: 1 }
    formTitle.value = '发布资讯'
    dialogVisible.value = true
}

const handleEditNews = (item) => {
    newsForm.value = { ...item }
    formTitle.value = '编辑资讯'
    dialogVisible.value = true
}

const handleDeleteNews = (item) => {
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
.home-container {
    min-height: 100vh;
    background:
        radial-gradient(circle at 10% -20%, rgba(56, 189, 248, 0.24), transparent 45%),
        radial-gradient(circle at 95% 15%, rgba(59, 130, 246, 0.18), transparent 40%),
        linear-gradient(180deg, #f8fbff 0%, #eef6ff 35%, #f4f8ff 100%);
}

.content-wrapper {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;
}

.hero-section {
    background: linear-gradient(135deg, rgba(255,255,255,0.88) 0%, rgba(238,246,255,0.9) 100%);
    backdrop-filter: blur(6px);
    padding: 80px 0;
    margin-bottom: 60px;
    border-radius: 0 0 40px 40px;
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: center;
}

.hero-content {
    max-width: 1200px;
    width: 100%;
    padding: 0 20px;
    position: relative;
    z-index: 2;
    text-align: center;
}

.hero-title {
    font-size: 48px;
    font-weight: 800;
    color: #0f172a;
    margin-bottom: 24px;
    letter-spacing: -1px;
}

.hero-subtitle {
    font-size: 20px;
    color: #64748b;
    margin-bottom: 40px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
}

.hero-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-bottom: 60px;
}

.cta-btn {
    padding: 12px 32px;
    font-weight: 600;
    font-size: 16px;
    height: auto;
    border-radius: 50px;
}

.secondary-btn {
    padding: 12px 32px;
    font-weight: 600;
    font-size: 16px;
    height: auto;
    border-radius: 50px;
    background: white;
    border: 1px solid #e2e8f0;
    color: #475569;
}
.secondary-btn:hover {
    border-color: var(--primary-color);
    color: var(--primary-color);
}

.hero-stats {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 40px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-num {
    font-size: 24px;
    font-weight: 700;
    color: #0f172a;
}

.stat-label {
    font-size: 14px;
    color: #64748b;
    margin-top: 4px;
}

.stat-divider {
    width: 1px;
    height: 30px;
    background-color: #cbd5e1;
}

/* Floating cards decoration */
.hero-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
}

.floating-card {
    position: absolute;
    background: white;
    padding: 12px 20px;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.08);
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 600;
    color: #475569;
    animation: float 6s ease-in-out infinite;
}

.floating-card .el-icon {
    font-size: 20px;
    color: var(--primary-color);
}

.card-1 { top: 20%; left: 10%; animation-delay: 0s; }
.card-2 { top: 60%; right: 15%; animation-delay: 2s; }
.card-3 { top: 25%; right: 10%; animation-delay: 4s; }

@keyframes float {
    0% { transform: translateY(0px); }
    50% { transform: translateY(-20px); }
    100% { transform: translateY(0px); }
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.section-title {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    position: relative;
    padding-left: 16px;
}

.section-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 20px;
    background-color: var(--primary-color);
    border-radius: 2px;
}

.news-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.news-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    border: 1px solid #f1f5f9;
    transition: all 0.3s;
    cursor: pointer;
    position: relative;
}

.news-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.05);
    border-color: transparent;
}

.news-badges {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}

.news-date {
    font-size: 13px;
    color: #94a3b8;
}

.news-title {
    font-size: 18px;
    font-weight: 600;
    color: #334155;
    margin-bottom: 8px;
}

.news-excerpt {
    font-size: 14px;
    color: #64748b;
    line-height: 1.6;
}

.sidebar-section {
    background: white;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid #f1f5f9;
}

.sidebar-title {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 20px;
}

.ranking-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 12px 0;
    border-bottom: 1px solid #f1f5f9;
    cursor: pointer;
    transition: all 0.2s;
}

.ranking-item:hover {
    transform: translateX(4px);
}

.ranking-item:last-child {
    border-bottom: none;
}

.rank-num {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    color: #94a3b8;
    font-size: 14px;
}

.rank-num.rank-1 { color: #f59e0b; }
.rank-num.rank-2 { color: #64748b; }
.rank-num.rank-3 { color: #b45309; }

.rank-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.rank-title {
    font-size: 14px;
    color: #334155;
    font-weight: 500;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.rank-heat {
    font-size: 12px;
    color: #94a3b8;
}

.quick-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
}

.quick-link-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: transform 0.2s;
}

.quick-link-item:hover {
    transform: translateY(-3px);
}

.icon-box {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
}

.icon-box.public { background: #eff6ff; color: #3b82f6; }
.icon-box.major { background: #eff6ff; color: #2563eb; }
.icon-box.interview { background: #dbeafe; color: #1d4ed8; }

.quick-link-item span {
    font-size: 12px;
    color: #64748b;
}

@media (max-width: 768px) {
    .hero-title { font-size: 32px; }
    .hero-subtitle { font-size: 16px; }
    .hero-stats { gap: 20px; }
    .floating-card { display: none; }
}

/* 新增模块样式 */
.material-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-top: 20px;
}

.material-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.material-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.material-image {
    position: relative;
    height: 160px;
    overflow: hidden;
}

.material-image .material-img {
    width: 100%;
    height: 100%;
}

.format-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    background: rgba(0,0,0,0.6);
    color: white;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.material-info {
    padding: 12px;
}

.material-name {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.material-meta {
    display: flex;
    align-items: center;
    gap: 8px;
}

.material-price {
    color: #f59e0b;
    font-weight: 700;
    font-size: 16px;
}

.material-origin {
    color: #9ca3af;
    text-decoration: line-through;
    font-size: 13px;
}

.guide-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    margin-top: 20px;
}

.guide-card {
    display: flex;
    gap: 16px;
    padding: 16px;
    background: white;
    border-radius: 12px;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.guide-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.guide-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 24px;
    flex-shrink: 0;
}

.guide-content {
    flex: 1;
    min-width: 0;
}

.guide-title {
    font-size: 15px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.guide-meta {
    font-size: 13px;
    color: #6b7280;
    margin-bottom: 6px;
}

.forum-list {
    margin-top: 20px;
}

.forum-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: white;
    border-radius: 10px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.forum-card:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.forum-content {
    flex: 1;
    min-width: 0;
}

.forum-title {
    font-size: 15px;
    font-weight: 500;
    color: #1f2937;
    margin-bottom: 6px;
}

.forum-meta {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: #9ca3af;
}

.forum-stats {
    display: flex;
    gap: 16px;
    color: #6b7280;
    font-size: 13px;
}

.forum-stats span {
    display: flex;
    align-items: center;
    gap: 4px;
}

@media (max-width: 768px) {
    .material-grid { grid-template-columns: repeat(2, 1fr); }
    .guide-grid { grid-template-columns: 1fr; }
}
</style>
