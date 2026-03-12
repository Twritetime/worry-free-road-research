<template>
  <div class="page-container" v-loading="loading">
    <div class="breadcrumb-wrapper">
       <el-breadcrumb separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/forum' }">交流论坛</el-breadcrumb-item>
        <el-breadcrumb-item>帖子详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="detail-content" v-if="post.id">
      <div class="article-container">
         <!-- Header -->
         <div class="article-header">
            <div class="header-top">
                <el-tag size="small" :type="getCategoryTag(post.category)" effect="plain" round>{{ getCategoryLabel(post.category) }}</el-tag>
                <span class="publish-time">{{ formatDate(post.createTime) }}</span>
            </div>
            <h1 class="article-title">{{ post.title }}</h1>
            <div class="article-meta">
               <div class="author-info">
                   <el-avatar :size="32" :src="post.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                   <span class="author-name">{{ post.nickname || '匿名用户' }}</span>
               </div>
               <div class="meta-stats">
                   <span class="meta-item"><el-icon><View /></el-icon> {{ post.viewCount || 0 }} 阅读</span>
                   <span class="meta-item"><el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }} 评论</span>
               </div>
            </div>
         </div>
         
         <el-divider />

         <!-- Content -->
         <div class="article-body markdown-body" v-html="post.content"></div>

         <!-- Actions -->
         <div class="article-footer">
            <div class="actions">
                <el-button 
                  round 
                  size="large"
                  :type="isLiked ? 'primary' : 'default'" 
                  :icon="Star" 
                  @click="handleLike"
                >
                  {{ isLiked ? '已点赞' : '点赞' }} ({{ post.likeCount || 0 }})
                </el-button>
                <el-button v-if="canEditPost" round size="large" icon="Edit" @click="handleEditPost">编辑</el-button>
                <el-button v-if="canDeletePost" round size="large" type="danger" plain icon="Delete" @click="handleDeletePost">删除</el-button>
            </div>
         </div>
      </div>

      <!-- Comments -->
      <div class="comments-container">
          <div class="comments-header">
              <h3>全部评论 ({{ total }})</h3>
          </div>
          
          <div class="comment-input-wrapper">
              <div class="user-avatar">
                  <el-avatar :size="40" :src="currentUser?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              </div>
              <div class="input-box">
                  <div v-if="replyTo" class="reply-target">
                      正在回复 @{{ replyTo.nickname }} <el-button type="primary" link size="small" @click="cancelReply">取消</el-button>
                  </div>
                  <el-input
                    v-model="commentContent"
                    type="textarea"
                    :rows="3"
                    :placeholder="replyTo ? `回复 @${replyTo.nickname}...` : '写下你的想法...'"
                    resize="none"
                  />
                  <div class="input-actions">
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

                  <!-- 子评论 -->
                  <div v-if="item.children && item.children.length > 0" class="sub-comments">
                      <div v-for="child in item.children" :key="child.id" class="sub-comment-item">
                          <div class="sub-comment-header">
                              <span class="sub-comment-author">{{ child.nickname || '匿名用户' }}</span>
                              <span v-if="child.replyToNickname" class="reply-text">
                                  回复 <span class="reply-to">@{{ child.replyToNickname }}</span>
                              </span>
                              <span class="sub-comment-time">{{ formatDate(child.createTime) }}</span>
                          </div>
                          <div class="sub-comment-content">{{ child.content }}</div>
                          <div class="sub-comment-actions">
                              <el-button type="primary" link size="small" @click="handleReply(child)">回复</el-button>
                              <el-button 
                                v-if="canDeleteComment(child)" 
                                type="danger" 
                                link 
                                size="small" 
                                @click="handleDeleteComment(child)"
                              >删除</el-button>
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
    
    <el-empty v-else-if="!loading" description="未找到帖子信息"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostById, updatePost, deletePost } from '@/api/post'
import { getComments, createComment, deleteComment } from '@/api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { View, ChatDotRound, Star, Edit, Delete, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { user: currentUser, isAdmin } = storeToRefs(userStore)

const post = ref({})
const loading = ref(false)
const commentContent = ref('')
const commentList = ref([])
const commentLoading = ref(false)
const commenting = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const isLiked = ref(false)
const replyTo = ref(null) // { id, nickname }

// Category Logic
const categoryMap = {
    1: '公共课交流',
    2: '专业课交流',
    3: '院校复试经验',
    4: '其他'
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

const formatDate = (date) => {
    if (!date) return ''
    return dayjs(date).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
    const id = route.params.id
    if (id) {
        fetchPostDetail(id)
        fetchComments(id)
    }
})

const isPostAuthor = computed(() => {
    return currentUser.value.id && post.value.userId === currentUser.value.id
})

const canEditPost = computed(() => {
    return isPostAuthor.value || isAdmin.value
})

const canDeletePost = computed(() => {
    return isPostAuthor.value || isAdmin.value
})

const canDeleteComment = (comment) => {
    if (!currentUser.value.id) return false
    return isAdmin.value || comment.userId === currentUser.value.id
}

const fetchPostDetail = async (id) => {
    loading.value = true
    try {
        const res = await getPostById(id)
        post.value = res || {}
    } catch (error) {
        console.error(error)
        ElMessage.error('获取帖子详情失败')
    } finally {
        loading.value = false
    }
}

const fetchComments = async (postId) => {
    commentLoading.value = true
    try {
        const res = await getComments({
            targetType: 1,
            targetId: postId,
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

const handleLike = async () => {
    if (!currentUser.value) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    if (isLiked.value) {
        ElMessage.info('您已经点过赞了')
        return
    }
    try {
        const newPost = { ...post.value, likeCount: (post.value.likeCount || 0) + 1 }
        await updatePost(newPost)
        post.value.likeCount++
        isLiked.value = true
        ElMessage.success('点赞成功')
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

const handleEditPost = () => {
    router.push(`/posts/edit/${post.value.id}`)
}

const handleDeletePost = () => {
    ElMessageBox.confirm(
        '确定要删除这篇帖子吗？此操作不可恢复',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
    .then(async () => {
        try {
            await deletePost(post.value.id)
            ElMessage.success('删除成功')
            router.push('/forum')
        } catch (error) {
            console.error(error)
            ElMessage.error('删除失败')
        }
    })
    .catch(() => {})
}

const handleDeleteComment = (comment) => {
    ElMessageBox.confirm(
        '确定要删除这条评论吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
    .then(async () => {
        try {
            await deleteComment(comment.id)
            ElMessage.success('删除成功')
            fetchComments(post.value.id)
        } catch (error) {
            console.error(error)
            ElMessage.error('删除失败')
        }
    })
    .catch(() => {})
}

const handleReply = (comment) => {
    replyTo.value = comment
    commentContent.value = ''
    document.querySelector('.comment-input-wrapper')?.scrollIntoView({ behavior: 'smooth' })
}

const cancelReply = () => {
    replyTo.value = null
    commentContent.value = ''
}

const submitComment = async () => {
    if (!commentContent.value.trim()) {
        ElMessage.warning('请输入评论内容')
        return
    }
    
    if (!currentUser.value) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }

    commenting.value = true
    try {
        let parentId = 0
        if (replyTo.value) {
            // 如果回复的是子评论，parentId应该是子评论的parentId；如果回复的是一级评论，parentId就是一级评论的id
            parentId = replyTo.value.parentId && replyTo.value.parentId !== 0 
                ? replyTo.value.parentId 
                : replyTo.value.id
        }

        // 去掉自动添加的前缀，避免重复
        let content = commentContent.value
        if (replyTo.value && content.startsWith(`回复 @${replyTo.value.nickname} : `)) {
            content = content.replace(`回复 @${replyTo.value.nickname} : `, '')
        }

        const commentData = {
            targetType: 1,
            targetId: post.value.id,
            content: content,
            userId: currentUser.value.id,
            nickname: currentUser.value.nickname || currentUser.value.username,
            avatar: currentUser.value.avatar,
            parentId: parentId,
            replyToUserId: replyTo.value ? replyTo.value.userId : null,
            replyToNickname: replyTo.value ? replyTo.value.nickname : null
        }
        await createComment(commentData)
        ElMessage.success('评论成功')
        commentContent.value = ''
        replyTo.value = null
        // 刷新评论列表，保持在当前页
        fetchComments(post.value.id)
    } catch (error) {
        console.error(error)
        ElMessage.error('评论失败')
    } finally {
        commenting.value = false
    }
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchComments(post.value.id)
}
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    background-color: #f5f7fa;
    padding-bottom: 60px;
}

.breadcrumb-wrapper {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.detail-content {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 20px;
}

.article-container {
    background: white;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    margin-bottom: 24px;
}

.header-top {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
}

.publish-time {
    color: #909399;
    font-size: 14px;
}

.article-title {
    font-size: 32px;
    color: #303133;
    margin: 0 0 24px 0;
    line-height: 1.4;
}

.article-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.author-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.author-name {
    font-size: 16px;
    color: #606266;
    font-weight: 500;
}

.meta-stats {
    display: flex;
    gap: 20px;
    color: #909399;
    font-size: 14px;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
}

.article-body {
    padding: 20px 0;
    font-size: 16px;
    line-height: 1.8;
    color: #303133;
    min-height: 200px;
}

.article-footer {
    margin-top: 40px;
    display: flex;
    justify-content: center;
}

.actions {
    display: flex;
    gap: 16px;
}

.comments-container {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
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

/* Responsive */
@media (max-width: 768px) {
    .article-container {
        padding: 20px;
    }
    
    .article-title {
        font-size: 24px;
    }
    
    .article-meta {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
    
    .comment-input-wrapper {
        flex-direction: column;
        gap: 12px;
    }
    
    .user-avatar {
        display: none;
    }
}

.sub-comments {
    margin-top: 16px;
    background: #f9fafc;
    padding: 16px;
    border-radius: 8px;
}

.sub-comment-item {
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEEF5;
}

.sub-comment-item:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
}

.sub-comment-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 4px;
    font-size: 13px;
}

.sub-comment-author {
    font-weight: 600;
    color: #303133;
}

.reply-text {
    color: #909399;
}

.reply-to {
    color: #409EFF;
    font-weight: 500;
}

.sub-comment-time {
    color: #C0C4CC;
    margin-left: auto;
}

.sub-comment-content {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 4px;
}

.sub-comment-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.reply-target {
    margin-bottom: 8px;
    font-size: 13px;
    color: #606266;
    background: #ecf5ff;
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}
</style>
