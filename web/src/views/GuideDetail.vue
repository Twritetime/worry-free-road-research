<template>
  <div class="page-container" v-loading="loading">
    <div class="breadcrumb-wrapper">
       <el-breadcrumb separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/guides' }">报考指南</el-breadcrumb-item>
        <el-breadcrumb-item>{{ guide.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="detail-content" v-if="guide.id">
      <div class="article-container">
         <div class="article-header">
            <h1 class="article-title">{{ guide.title }}</h1>
            <div class="article-meta">
               <span class="meta-item"><el-icon><Calendar /></el-icon> {{ formatDate(guide.createTime) }}</span>
               <span class="meta-separator">|</span>
               <span v-if="guide.institution" class="meta-item">{{ guide.institution }}</span>
               <span v-if="guide.institution" class="meta-separator">|</span>
               <span v-if="guide.major" class="meta-item">{{ guide.major }}</span>
               <span v-if="guide.major" class="meta-separator">|</span>
               <span class="meta-item"><el-icon><View /></el-icon> {{ guide.viewCount || 0 }} 阅读</span>
               <span class="meta-separator">|</span>
               <el-tag size="small" :type="getCategoryType(guide.category)" effect="plain" round>{{ formatCategory(guide.category) }}</el-tag>
            </div>
         </div>
         
         <el-divider />

         <div class="article-body markdown-body" v-html="renderContent(guide.content)"></div>

         <div class="article-footer">
            <div class="actions">
                <el-button
                  round
                  :type="isFavorited ? 'warning' : 'default'"
                  @click="handleToggleFavorite"
                >
                  <el-icon style="margin-right: 6px;">
                    <StarFilled v-if="isFavorited" />
                    <Star v-else />
                  </el-icon>
                  {{ isFavorited ? '已收藏' : '收藏指南' }}
                </el-button>
                <el-button round @click="$router.back()">返回列表</el-button>
                <el-button type="primary" round icon="Share" @click="handleShare">分享</el-button>
            </div>
         </div>
      </div>

      <!-- Comments -->
      <div class="comments-container">
          <div class="comments-header">
              <h3>全部评论 ({{ total }})</h3>
          </div>
          
          <div class="comment-input-wrapper">
              <div class="user-avatar" v-if="currentUser?.id">
                  <el-avatar :size="40" :src="currentUser.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              </div>
              <div class="input-box">
                  <el-input
                    v-model="commentContent"
                    type="textarea"
                    :rows="3"
                    :placeholder="replyTo ? `回复 @${replyTo.nickname}...` : '写下你的想法...'"
                    resize="none"
                  />
                  <div class="input-actions">
                      <el-button v-if="replyTo" @click="replyTo = null; commentContent = ''">取消回复</el-button>
                      <el-button type="primary" @click="submitComment" :loading="commenting">发表评论</el-button>
                  </div>
              </div>
          </div>

          <div class="comment-list" v-loading="commentLoading">
              <div v-for="(item, index) in commentList" :key="item.id" class="comment-item">
                <div class="comment-avatar">
                  <el-avatar :size="40" :src="item.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                </div>
                <div class="comment-content">
                  <div class="comment-info">
                    <span class="comment-author">{{ item.nickname || '匿名用户' }}</span>
                    <span class="comment-floor">#{{ (pageNum - 1) * pageSize + index + 1 }}</span>
                  </div>
                  <div class="comment-text">{{ item.content }}</div>
                  <div class="comment-footer">
                      <span class="comment-time">{{ formatDate(item.createTime) }}</span>
                      <el-button type="primary" link size="small" @click="handleReply(item)">回复</el-button>
                      <el-button 
                        v-if="canDeleteComment(item)" 
                        type="danger" 
                        link 
                        size="small" 
                        @click="handleDeleteComment(item)"
                      >删除</el-button>
                  </div>
                  
                  <div v-if="item.children && item.children.length > 0" class="sub-comments">
                      <div v-for="child in item.children" :key="child.id" class="sub-comment-item">
                          <span class="sub-comment-user">{{ child.nickname }}</span>
                          <span v-if="child.replyToNickname" class="reply-text"> 回复 {{ child.replyToNickname }}</span>
                          : {{ child.content }}
                          <div class="sub-comment-footer">
                              <span class="sub-comment-time">{{ formatDate(child.createTime) }}</span>
                              <el-button type="primary" link size="small" @click="handleReply(child)">回复</el-button>
                              <el-button v-if="canDeleteComment(child)" type="danger" link size="small" @click="handleDeleteComment(child)">删除</el-button>
                          </div>
                      </div>
                  </div>
                </div>
              </div>
              <el-empty v-if="!commentLoading && commentList.length === 0" description="暂无评论，快来抢沙发吧~" />
              
              <div class="pagination-wrapper" v-if="total > 0">
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
    </div>
    
    <el-empty v-else-if="!loading" description="未找到指南信息"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGuideById } from '@/api/guide'
import { getComments, createComment, deleteComment } from '@/api/comment'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, View, ArrowRight, Share, ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { user: currentUser, isAdmin } = storeToRefs(userStore)

const guide = ref({})
const loading = ref(false)
const commentContent = ref('')
const commentList = ref([])
const commentLoading = ref(false)
const commenting = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const isFavorited = ref(false)
const replyTo = ref(null) // { id, nickname }

const categoryMap = {
  'zhaoshengjianzhang': '招生简章',
  'zhuanyemulu': '专业目录',
  'kaoshidagang': '考试大纲',
  'fushixize': '复试细则'
}

onMounted(() => {
    const id = route.params.id
    if (id) {
        fetchGuideDetail(id)
        fetchComments(id)
    }
})

const fetchGuideDetail = async (id) => {
    loading.value = true
    try {
        const res = await getGuideById(id)
        guide.value = res || {}
        if (currentUser.value?.id) {
            await checkFavoriteStatus(id)
        } else {
            isFavorited.value = false
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('获取指南详情失败')
    } finally {
        loading.value = false
    }
}

const checkFavoriteStatus = async (id) => {
    try {
        const res = await checkFavorite({
            userId: currentUser.value.id,
            targetId: id,
            type: 2
        })
        isFavorited.value = !!res
    } catch (error) {
        console.error(error)
    }
}

const handleToggleFavorite = async () => {
    if (!currentUser.value?.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    try {
        await toggleFavorite({
            userId: currentUser.value.id,
            targetId: guide.value.id,
            targetType: 2,
            targetTitle: guide.value.title
        })
        isFavorited.value = !isFavorited.value
        ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏')
    } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
    }
}

const fetchComments = async (id) => {
    commentLoading.value = true
    try {
        const res = await getComments({
            targetType: 2,
            targetId: id,
            pageNum: pageNum.value,
            pageSize: pageSize.value
        })
        commentList.value = res.records || []
        total.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        commentLoading.value = false
    }
}

const submitComment = async () => {
    if (!commentContent.value.trim()) {
        ElMessage.warning('请输入评论内容')
        return
    }
    
    if (!currentUser.value?.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }

    commenting.value = true
    try {
        const commentData = {
            targetType: 2,
            targetId: guide.value.id,
            content: commentContent.value,
            userId: currentUser.value.id,
            nickname: currentUser.value.nickname || currentUser.value.username,
            avatar: currentUser.value.avatar
        }
        
        if (replyTo.value) {
            // Logic: parentId must be the top-level comment ID
            commentData.parentId = replyTo.value.parentId || replyTo.value.id
            commentData.replyToUserId = replyTo.value.userId
            commentData.replyToNickname = replyTo.value.nickname
        }
        
        await createComment(commentData)
        ElMessage.success('评论成功')
        commentContent.value = ''
        replyTo.value = null
        fetchComments(guide.value.id)
    } catch (error) {
        console.error(error)
        ElMessage.error('评论失败')
    } finally {
        commenting.value = false
    }
}

const handleReply = (comment) => {
    replyTo.value = comment
    commentContent.value = ''
    // Optional: scroll to input
    document.querySelector('.comment-input-wrapper')?.scrollIntoView({ behavior: 'smooth' })
}

const handleDeleteComment = (comment) => {
    ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' })
    .then(async () => {
        await deleteComment(comment.id)
        ElMessage.success('删除成功')
        fetchComments(guide.value.id)
    })
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchComments(guide.value.id)
}

const canDeleteComment = (comment) => {
    if (!currentUser.value.id) return false
    return isAdmin.value || comment.userId === currentUser.value.id
}

const formatCategory = (key) => {
    return categoryMap[key] || key
}

const getCategoryType = (key) => {
    const map = {
        'zhaoshengjianzhang': 'primary',
        'zhuanyemulu': 'success',
        'kaoshidagang': 'warning',
        'fushixize': 'danger'
    }
    return map[key] || 'info'
}

const formatDate = (date) => {
    if (!date) return ''
    return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// Simple markdown rendering or just text formatting
const renderContent = (content) => {
    if (!content) return ''
    if (!content.includes('<') && content.includes('\n')) {
        return content.replace(/\n/g, '<br>')
    }
    const parser = new DOMParser()
    const doc = parser.parseFromString(content, 'text/html')
    doc.querySelectorAll('a').forEach((anchor) => {
        anchor.setAttribute('target', '_blank')
        anchor.setAttribute('rel', 'noopener noreferrer')
    })
    doc.querySelectorAll('img').forEach((img) => {
        img.setAttribute('loading', 'lazy')
        img.setAttribute('onerror', 'this.style.display="none"')
    })
    return doc.body.innerHTML
}

const handleShare = () => {
    ElMessage.success('链接已复制到剪贴板')
    navigator.clipboard.writeText(window.location.href)
}
</script>

<style scoped>
.page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    min-height: 80vh;
}

.breadcrumb-wrapper {
    margin-bottom: 24px;
    padding: 12px 0;
}

.article-container {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    padding: 40px 60px;
    transition: all 0.3s ease;
}

.article-header {
    text-align: center;
    margin-bottom: 30px;
}

.article-title {
    font-size: 32px;
    font-weight: 700;
    color: #1a1a1a;
    margin-bottom: 20px;
    line-height: 1.4;
}

.article-meta {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    color: #909399;
    font-size: 14px;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
}

.meta-separator {
    color: #e4e7ed;
}

.article-body {
    font-size: 16px;
    line-height: 1.8;
    color: #303133;
    margin: 40px 0;
    min-height: 300px;
}

/* Add some basic markdown-like styles */
.article-body :deep(h2) {
    font-size: 24px;
    margin-top: 30px;
    margin-bottom: 16px;
    border-left: 4px solid var(--el-color-primary);
    padding-left: 12px;
}

.article-body :deep(p) {
    margin-bottom: 16px;
}

.article-body :deep(img) {
    max-width: 100%;
    border-radius: 8px;
    margin: 16px 0;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.article-footer {
    margin-top: 60px;
    display: flex;
    justify-content: center;
}

.actions {
    display: flex;
    gap: 16px;
}

@media (max-width: 768px) {
    .page-container {
        padding: 10px;
    }
    
    .article-container {
        padding: 20px;
    }
    
    .article-title {
        font-size: 24px;
    }
    
    .article-meta {
        flex-direction: column;
        gap: 10px;
    }
    
    .meta-separator {
        display: none;
    }
}

.comments-container {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    margin-top: 24px;
}

.comments-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEEF5;
}

.comments-header h3 {
    margin: 0;
    font-size: 18px;
    color: #303133;
}

.comment-input-wrapper {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
}

.input-box {
    flex: 1;
}

.input-actions {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.comment-item {
    display: flex;
    gap: 20px;
    padding: 24px 0;
    border-bottom: 1px solid #f0f2f5;
}

.comment-item:last-child {
    border-bottom: none;
}

.comment-content {
    flex: 1;
}

.comment-info {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
}

.comment-author {
    font-weight: 600;
    color: #303133;
    font-size: 14px;
}

.comment-floor {
    color: #909399;
    font-size: 12px;
    background: #f0f2f5;
    padding: 2px 6px;
    border-radius: 4px;
}

.comment-text {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
}

.comment-footer {
    display: flex;
    align-items: center;
    gap: 16px;
}

.comment-time {
    font-size: 12px;
    color: #909399;
}

.pagination-wrapper {
    margin-top: 30px;
    display: flex;
    justify-content: center;
}

.sub-comments {
    background: #f9fafc;
    padding: 15px;
    border-radius: 8px;
    margin-top: 10px;
}

.sub-comment-item {
    font-size: 13px;
    margin-bottom: 8px;
    color: #606266;
}

.sub-comment-user {
    color: var(--el-color-primary);
    font-weight: 500;
}

.reply-text {
    color: #909399;
    margin: 0 4px;
}

.sub-comment-footer {
    display: flex;
    gap: 10px;
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
}
</style>
