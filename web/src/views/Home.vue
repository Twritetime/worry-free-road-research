<template>
  <div class="home-container">
      <!-- Hero Section - Title at the top -->
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
        </div>
    </section>

      <!-- Carousel Section -->
      <div class="carousel-wrapper">
        <el-carousel height="400px" indicator-position="bottom" class="home-carousel">
          <el-carousel-item v-for="item in homeConfigs.banner" :key="item.id" @click="handleBannerClick(item.linkUrl)">
            <div class="carousel-item">
              <img :src="item.imgUrl" :alt="item.title" class="carousel-image" />
              <div class="carousel-overlay">
                <h3 class="carousel-title">{{ item.title }}</h3>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- Notice Section -->
      <div class="notice-wrapper">
        <div class="notice-header">
          <div class="notice-icon">
            <el-icon class="bell-icon"><Bell /></el-icon>
            <span class="notice-title">通知公告</span>
          </div>
          <button class="notice-more" @click="$router.push('/news')">
            更多 <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
        <div class="notice-list">
          <div v-for="item in homeConfigs.notice.slice(0, 4)" :key="item.id" class="notice-item" @click="handleNoticeClick(item.linkUrl)">
            <span class="notice-dot"></span>
            <span class="notice-text">{{ item.title }}</span>
          </div>
        </div>
      </div>

    <!-- Quick Access Section -->
    <section class="quick-access">
        <div class="content-wrapper">
            <div class="access-grid">
                <div class="access-item" @click="$router.push('/materials')">
                    <div class="access-icon-wrapper">
                        <el-icon class="access-icon"><Document /></el-icon>
                    </div>
                    <span class="access-text">资料商城</span>
                </div>
                <div class="access-item" @click="$router.push('/guides')">
                    <div class="access-icon-wrapper blue">
                        <el-icon class="access-icon"><Document /></el-icon>
                    </div>
                    <span class="access-text">报考指南</span>
                </div>
                <div class="access-item" @click="$router.push('/forum')">
                    <div class="access-icon-wrapper green">
                        <el-icon class="access-icon"><ChatDotRound /></el-icon>
                    </div>
                    <span class="access-text">交流论坛</span>
                </div>
                <div class="access-item" @click="$router.push('/news')">
                    <div class="access-icon-wrapper orange">
                        <el-icon class="access-icon"><Trophy /></el-icon>
                    </div>
                    <span class="access-text">考研资讯</span>
                </div>
            </div>
        </div>
    </section>

    <!-- News Section -->
    <section class="news-section">
        <div class="content-wrapper">
            <div class="section-header">
                <h2 class="section-title">
                    <el-icon class="section-icon"><Reading /></el-icon>
                    最新资讯
                </h2>
                <button class="view-more" @click="$router.push('/news')">
                    查看更多 <el-icon><ArrowRight /></el-icon>
                </button>
            </div>
            <div class="news-grid">
                <div 
                    v-for="news in newsList.slice(0, 6)" 
                    :key="news.id" 
                    class="news-card"
                    @click="$router.push(`/news/${news.id}`)"
                >
                    <div class="news-image-wrapper">
                        <img :src="news.coverImage || 'https://picsum.photos/400/200?random=' + news.id" alt="" class="news-image" />
                    </div>
                    <div class="news-content">
                        <span class="news-tag">{{ getNewsTypeLabel(news.type) }}</span>
                        <h3 class="news-title">{{ news.title }}</h3>
                        <p class="news-summary">{{ stripHtml(news.content)?.slice(0, 100) }}...</p>
                        <div class="news-meta">
                            <span class="news-time">{{ news.createTime }}</span>
                            <span class="news-views">阅读 {{ news.viewCount || 0 }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Materials Section -->
    <section class="materials-section">
        <div class="content-wrapper">
            <div class="section-header">
                <h2 class="section-title">
                    <el-icon class="section-icon"><Folder /></el-icon>
                    热门资料
                </h2>
                <button class="view-more" @click="$router.push('/materials')">
                    查看更多 <el-icon><ArrowRight /></el-icon>
                </button>
            </div>
            <div class="materials-grid">
                <div 
                    v-for="material in materialList.slice(0, 4)" 
                    :key="material.id" 
                    class="material-card"
                    @click="$router.push(`/materials/${material.id}`)"
                >
                    <div class="material-image-wrapper">
                        <img :src="material.coverImage || 'https://picsum.photos/300/200?random=' + material.id" alt="" class="material-image" />
                        <div class="material-price">
                            <span v-if="material.price === 0" class="free-badge">免费</span>
                            <span v-else class="price-text">¥{{ material.price }}</span>
                        </div>
                    </div>
                    <div class="material-content">
                        <span class="material-tag">{{ getCategoryLabel(material.category) }}</span>
                        <h3 class="material-title">{{ material.title }}</h3>
                        <p class="material-summary">{{ stripHtml(material.description)?.slice(0, 60) }}...</p>
                        <div class="material-meta">
                            <span class="material-downloads">下载 {{ material.downloadCount || 0 }}</span>
                            <span class="material-favorites">收藏 {{ material.favoriteCount || 0 }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Guides Section -->
    <section class="guides-section">
        <div class="content-wrapper">
            <div class="section-header">
                <h2 class="section-title">
                    <el-icon class="section-icon"><Document /></el-icon>
                    报考指南
                </h2>
                <button class="view-more" @click="$router.push('/guides')">
                    查看更多 <el-icon><ArrowRight /></el-icon>
                </button>
            </div>
            <div class="guides-grid">
                <div 
                    v-for="guide in guideList.slice(0, 4)" 
                    :key="guide.id" 
                    class="guide-card"
                    @click="$router.push(`/guides/${guide.id}`)"
                >
                    <div class="guide-icon-wrapper">
                        <el-icon class="guide-icon"><Document /></el-icon>
                    </div>
                    <div class="guide-content">
                        <span class="guide-tag">{{ getGuideCategoryLabel(guide.category) }}</span>
                        <h3 class="guide-title">{{ guide.title }}</h3>
                        <p class="guide-summary">{{ stripHtml(guide.content)?.slice(0, 80) }}...</p>
                        <div class="guide-meta">
                            <span class="guide-views">阅读 {{ guide.viewCount || 0 }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Forum Section -->
    <section class="forum-section">
        <div class="content-wrapper">
            <div class="section-header">
                <h2 class="section-title">
                    <el-icon class="section-icon"><ChatLineSquare /></el-icon>
                    交流论坛
                </h2>
                <button class="view-more" @click="$router.push('/forum')">
                    查看更多 <el-icon><ArrowRight /></el-icon>
                </button>
            </div>
            <div class="forum-list">
                <div 
                    v-for="post in postList.slice(0, 5)" 
                    :key="post.id" 
                    class="forum-item"
                    @click="handlePostClick(post.id)"
                >
                    <div class="forum-avatar">
                        <el-avatar :size="48" :src="post.userAvatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                    </div>
                    <div class="forum-content">
                        <div class="forum-title-row">
                            <span class="forum-title">{{ post.title }}</span>
                            <span class="forum-tag" v-if="post.tag">{{ post.tag }}</span>
                        </div>
                        <div class="forum-meta">
                            <span class="forum-author">{{ post.authorName }}</span>
                            <span class="forum-time">{{ post.createTime }}</span>
                            <span class="forum-replies">{{ post.commentCount }} 回复</span>
                            <span class="forum-views">{{ post.viewCount }} 浏览</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section">
        <div class="content-wrapper">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon blue">
                        <el-icon><Document /></el-icon>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value">10,000+</span>
                        <span class="stat-label">考研资料</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon green">
                        <el-icon><User /></el-icon>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value">5,000+</span>
                        <span class="stat-label">活跃用户</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon purple">
                        <el-icon><Trophy /></el-icon>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value">98%</span>
                        <span class="stat-label">好评率</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon orange">
                        <el-icon><ChatLineSquare /></el-icon>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value">50,000+</span>
                        <span class="stat-label">交流帖子</span>
                    </div>
                </div>
            </div>
        </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Document, ChatDotRound, Trophy, Reading, Folder, ChatLineSquare, Bell, User } from '@element-plus/icons-vue'
import { getNewsList } from '@/api/news'
import { getMaterialList } from '@/api/material'
import { getGuideList } from '@/api/guide'
import { getPostList } from '@/api/post'
import { getHomeConfigs } from '@/api/homeConfig'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const newsList = ref([])
const materialList = ref([])
const guideList = ref([])
const postList = ref([])

const homeConfigs = ref({
    banner: [
        { id: 1, type: 'banner', title: '2026考研招生简章发布', imgUrl: 'https://picsum.photos/seed/home1/800/400', linkUrl: '/guides', sortOrder: 1, status: 1 },
        { id: 2, type: 'banner', title: '名师直播：考研政治冲刺押题', imgUrl: 'https://picsum.photos/seed/home2/800/400', linkUrl: '/news', sortOrder: 2, status: 1 },
        { id: 3, type: 'banner', title: '限时优惠：全套考研资料包', imgUrl: 'https://picsum.photos/seed/home3/800/400', linkUrl: '/materials', sortOrder: 3, status: 1 },
        { id: 4, type: 'banner', title: '复试通关秘籍免费领', imgUrl: 'https://picsum.photos/seed/home4/800/400', linkUrl: '/guides', sortOrder: 4, status: 1 },
        { id: 5, type: 'banner', title: '考研院校数据库上线', imgUrl: 'https://picsum.photos/seed/home5/800/400', linkUrl: '/materials', sortOrder: 5, status: 1 }
    ],
    notice: [
        { id: 1, type: 'notice', title: '2026年考研初试时间确定：12月20-21日', linkUrl: '/news', sortOrder: 1, status: 1 },
        { id: 2, type: 'notice', title: '推免服务系统9月28日开放', linkUrl: '/news', sortOrder: 2, status: 1 },
        { id: 3, type: 'notice', title: '网上报名时间：10月8日至25日', linkUrl: '/news', sortOrder: 3, status: 1 },
        { id: 4, type: 'notice', title: '少数民族骨干计划招生简章发布', linkUrl: '/news', sortOrder: 4, status: 1 },
        { id: 5, type: 'notice', title: '退役大学生士兵专项计划说明', linkUrl: '/news', sortOrder: 5, status: 1 }
    ],
    hot: [],
    recommend: []
})

onMounted(() => {
    fetchNews()
    fetchMaterials()
    fetchGuides()
    fetchPosts()
    fetchHomeConfigs()
})

const fetchNews = async () => {
    try {
        const res = await getNewsList({ pageNum: 1, pageSize: 6 })
        newsList.value = res.records || []
    } catch (error) {
        console.error(error)
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

const fetchHomeConfigs = async () => {
    try {
        const res = await getHomeConfigs()
        if (res && Object.keys(res).length > 0) {
            homeConfigs.value = { ...homeConfigs.value, ...res }
        }
    } catch (error) {
        console.error('Error fetching home configs:', error)
    }
}

const handlePostClick = (id) => {
    if (!user.value?.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    router.push(`/posts/${id}`)
}

const handleBannerClick = (linkUrl) => {
    if (linkUrl) {
        router.push(linkUrl)
    }
}

const handleNoticeClick = (linkUrl) => {
    if (linkUrl) {
        router.push(linkUrl)
    }
}

const getNewsTypeLabel = (type) => {
    const map = {
        'notice': '报考指南',
        'policy': '政策解读',
        'activity': '备考活动',
        'other': '其他资讯'
    }
    return map[type] || type
}

const getCategoryLabel = (category) => {
    const map = {
        'kaoyanzhenti': '考研真题',
        'kaoshidagang': '考试大纲',
        'fudaoziliao': '辅导资料',
        'zhaoshengjianzhang': '招生简章',
        'fushixize': '复试细则',
        'xuewei': '学位信息',
        'public': '公共课',
        'major': '专业课',
        'english': '英语',
        'math': '数学',
        'politics': '政治',
        'professional': '专业课'
    }
    return map[category] || category
}

const getGuideCategoryLabel = (category) => {
    const map = {
        'kaoyanzhinan': '考研指南',
        'zhuanye': '专业选择',
        'yuanxiaofenxi': '院校分析',
        'fushi': '复试指导',
        'baoming': '报名流程',
        'fushixize': '复试细则',
        'zhuanyemulu': '专业目录',
        'zhaoshengjianzhang': '招生简章',
        'kaoyan': '考研',
        'beikao': '备考'
    }
    return map[category] || category
}

const stripHtml = (html) => {
   let tmp = document.createElement("DIV");
   tmp.innerHTML = html;
   return tmp.textContent || tmp.innerText || "";
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

.carousel-wrapper {
    max-width: 1400px;
    margin: -20px auto 0;
    padding: 0 20px;
}

.home-carousel {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0,0,0,0.1);
}

.carousel-item {
    position: relative;
    width: 100%;
    height: 100%;
    cursor: pointer;
}

.carousel-image {
    width: 100%;
    height: 400px;
    object-fit: cover;
}

.carousel-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0,0,0,0.6));
    padding: 40px 30px;
}

.carousel-title {
    font-size: 24px;
    font-weight: 700;
    color: white;
    margin: 0;
    text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

.notice-wrapper {
    background: linear-gradient(135deg, #1e40af 0%, #3b82f6 50%, #60a5fa 100%);
    padding: 20px;
    margin: 20px auto;
    max-width: 1400px;
    border-radius: 16px;
}

.notice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.notice-icon {
    display: flex;
    align-items: center;
    gap: 8px;
}

.bell-icon {
    color: #fbbf24;
    font-size: 20px;
}

.notice-title {
    font-size: 16px;
    font-weight: 600;
    color: white;
}

.notice-more {
    display: flex;
    align-items: center;
    gap: 4px;
    background: transparent;
    border: none;
    color: rgba(255,255,255,0.8);
    font-size: 14px;
    cursor: pointer;
    transition: color 0.2s;
}

.notice-more:hover {
    color: white;
}

.notice-list {
    display: flex;
    gap: 30px;
    flex-wrap: wrap;
}

.notice-item {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: transform 0.2s;
}

.notice-item:hover {
    transform: translateX(4px);
}

.notice-dot {
    width: 6px;
    height: 6px;
    background: #fbbf24;
    border-radius: 50%;
}

.notice-text {
    font-size: 14px;
    color: rgba(255,255,255,0.9);
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.hero-section {
    padding: 60px 20px;
    text-align: center;
}

.hero-content {
    max-width: 800px;
    margin: 0 auto;
}

.hero-title {
    font-size: 40px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 16px;
}

.hero-subtitle {
    font-size: 16px;
    color: #64748b;
    margin-bottom: 32px;
}

.hero-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
}

.cta-btn {
    padding: 12px 32px;
    font-size: 16px;
}

.secondary-btn {
    padding: 12px 32px;
    font-size: 16px;
    background: white;
    color: #3b82f6;
    border-color: #3b82f6;
}

.content-wrapper {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.quick-access {
    padding: 20px 0;
}

.access-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

.access-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px;
    background: white;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.3s;
}

.access-item:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0,0,0,0.1);
}

.access-icon-wrapper {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
}

.access-icon-wrapper.blue {
    background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.access-icon-wrapper.green {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.access-icon-wrapper.orange {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.access-icon {
    font-size: 28px;
    color: white;
}

.access-text {
    font-size: 14px;
    font-weight: 500;
    color: #334155;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 24px;
    font-weight: 600;
    color: #1e293b;
}

.section-icon {
    color: #3b82f6;
    font-size: 20px;
}

.view-more {
    display: flex;
    align-items: center;
    gap: 4px;
    background: transparent;
    border: none;
    color: #64748b;
    font-size: 14px;
    cursor: pointer;
    transition: color 0.2s;
}

.view-more:hover {
    color: #3b82f6;
}

.news-section {
    padding: 20px 0;
}

.news-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.news-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
}

.news-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0,0,0,0.1);
}

.news-image-wrapper {
    position: relative;
    height: 180px;
    overflow: hidden;
}

.news-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.news-content {
    padding: 16px;
}

.news-tag {
    display: inline-block;
    padding: 4px 12px;
    background: #dbeafe;
    color: #1d4ed8;
    font-size: 12px;
    border-radius: 20px;
    margin-bottom: 8px;
}

.news-title {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.news-summary {
    font-size: 14px;
    color: #64748b;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.news-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #94a3b8;
}

.materials-section {
    padding: 20px 0;
}

.materials-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

.material-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
}

.material-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0,0,0,0.1);
}

.material-image-wrapper {
    position: relative;
    height: 160px;
    overflow: hidden;
}

.material-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.material-price {
    position: absolute;
    bottom: 8px;
    left: 8px;
}

.free-badge {
    display: inline-block;
    padding: 4px 12px;
    background: #10b981;
    color: white;
    font-size: 12px;
    font-weight: 500;
    border-radius: 20px;
}

.price-text {
    display: inline-block;
    padding: 4px 12px;
    background: #ef4444;
    color: white;
    font-size: 12px;
    font-weight: 500;
    border-radius: 20px;
}

.material-content {
    padding: 16px;
}

.material-tag {
    display: inline-block;
    padding: 4px 12px;
    background: #d1fae5;
    color: #059669;
    font-size: 12px;
    border-radius: 20px;
    margin-bottom: 8px;
}

.material-title {
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.material-summary {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.material-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #94a3b8;
}

.guides-section {
    padding: 20px 0;
}

.guides-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

.guide-card {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: white;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s;
}

.guide-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0,0,0,0.1);
}

.guide-icon-wrapper {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.guide-icon {
    font-size: 24px;
    color: white;
}

.guide-content {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.guide-tag {
    display: inline-block;
    padding: 4px 10px;
    background: #ede9fe;
    color: #7c3aed;
    font-size: 12px;
    border-radius: 20px;
    margin-bottom: 8px;
    align-self: flex-start;
}

.guide-title {
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.guide-summary {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    flex: 1;
}

.guide-meta {
    font-size: 12px;
    color: #94a3b8;
}

.forum-section {
    padding: 20px 0;
}

.forum-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.forum-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    background: white;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s;
}

.forum-item:hover {
    background: #f8fafc;
    transform: translateX(4px);
}

.forum-avatar {
    flex-shrink: 0;
}

.forum-content {
    flex: 1;
    min-width: 0;
}

.forum-title-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.forum-title {
    font-size: 15px;
    font-weight: 500;
    color: #1e293b;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.forum-tag {
    display: inline-block;
    padding: 2px 8px;
    background: #dbeafe;
    color: #1d4ed8;
    font-size: 12px;
    border-radius: 4px;
}

.forum-meta {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: #94a3b8;
}

.stats-section {
    padding: 60px 0;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
}

.stat-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 32px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
}

.stat-icon.blue {
    background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
}

.stat-icon.green {
    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.stat-icon.purple {
    background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%);
}

.stat-icon.orange {
    background: linear-gradient(135deg, #fed7aa 0%, #fdba74 100%);
}

.stat-icon el-icon {
    font-size: 24px;
}

.stat-icon.blue el-icon {
    color: #3b82f6;
}

.stat-icon.green el-icon {
    color: #10b981;
}

.stat-icon.purple el-icon {
    color: #8b5cf6;
}

.stat-icon.orange el-icon {
    color: #f59e0b;
}

.stat-info {
    text-align: center;
}

.stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
}

.stat-label {
    font-size: 14px;
    color: #64748b;
    margin-top: 4px;
}
</style>