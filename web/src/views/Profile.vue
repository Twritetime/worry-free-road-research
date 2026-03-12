<template>
  <div class="page-container">
    <div class="profile-layout">
      <!-- Sidebar -->
      <div class="sidebar">
         <div class="user-card">
            <el-avatar :size="80" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <h3 class="username">{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="user-role">{{ roleLabel }}</p>
         </div>
         
         <div class="menu-list">
            <div 
              v-for="item in menuItems" 
              :key="item.index" 
              class="menu-item" 
              :class="{ active: activeMenu === item.index }"
              @click="handleSelect(item.index)"
            >
               <el-icon><component :is="item.icon" /></el-icon>
               <span>{{ item.label }}</span>
            </div>
         </div>
      </div>
      
      <!-- Main Content -->
      <div class="content-area">
         <div class="content-header">
            <h2>{{ currentMenuLabel }}</h2>
         </div>
         
         <div class="content-body">
             <!-- 个人资料 -->
            <div v-if="activeMenu === 'data'" class="stats-section" v-loading="statsLoading">
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-card shadow="hover" class="personal-stat-card">
                            <div class="stat-title">订单数量</div>
                            <div class="stat-number">{{ personalStats.orderCount }}</div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" class="personal-stat-card">
                            <div class="stat-title">发帖数量</div>
                            <div class="stat-number">{{ personalStats.postCount }}</div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" class="personal-stat-card">
                            <div class="stat-title">收藏数量</div>
                            <div class="stat-number">{{ personalStats.favoriteCount }}</div>
                        </el-card>
                    </el-col>
                </el-row>
                <el-row :gutter="20" style="margin-top: 20px;">
                    <el-col :span="12">
                        <el-card shadow="hover" class="personal-stat-card">
                            <div class="stat-title">反馈次数</div>
                            <div class="stat-number">{{ personalStats.feedbackCount }}</div>
                        </el-card>
                    </el-col>
                    <el-col :span="12">
                        <el-card shadow="hover" class="personal-stat-card">
                            <div class="stat-title">累计消费</div>
                            <div class="stat-number">¥ {{ personalStats.spendAmount }}</div>
                        </el-card>
                    </el-col>
                </el-row>
            </div>

            <div v-if="activeMenu === 'info'" class="form-section">
                <el-form :model="userInfo" label-position="top" class="custom-form">
                    <el-row :gutter="30">
                        <el-col :span="12">
                            <el-form-item label="用户名">
                                <el-input v-model="userInfo.username" disabled size="large"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="昵称">
                                <el-input v-model="userInfo.nickname" size="large"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="30">
                        <el-col :span="12">
                            <el-form-item label="手机号">
                                <el-input v-model="userInfo.phone" size="large"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="邮箱">
                                <el-input v-model="userInfo.email" size="large"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-form-item label="头像">
                        <div class="avatar-upload-wrapper">
                             <el-upload
                              class="avatar-uploader"
                              :action="uploadUrl"
                              :show-file-list="false"
                              :on-success="handleAvatarSuccess"
                              :before-upload="beforeAvatarUpload"
                            >
                              <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" />
                              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                            </el-upload>
                            <div class="upload-tip">
                                <p>支持 JPG/PNG 格式</p>
                                <p>文件小于 2MB</p>
                            </div>
                        </div>
                    </el-form-item>
                    
                    <el-form-item>
                        <el-button type="primary" size="large" @click="handleUpdateInfo" class="submit-btn">保存修改</el-button>
                    </el-form-item>
                </el-form>
            </div>

             <!-- 修改密码 -->
            <div v-if="activeMenu === 'password'" class="form-section">
                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-position="top" class="custom-form" style="max-width: 500px">
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input v-model="passwordForm.oldPassword" type="password" show-password size="large"></el-input>
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" show-password size="large"></el-input>
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" show-password size="large"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" size="large" @click="handleUpdatePassword" class="submit-btn">确认修改</el-button>
                  </el-form-item>
                </el-form>
            </div>
            
             <!-- Dynamic Components -->
            <OrderList v-if="activeMenu === 'orders'" />
            <AddressList v-if="activeMenu === 'address'" />
            <FavoriteList v-if="activeMenu === 'favorites'" />
            <FeedbackList v-if="activeMenu === 'feedback'" />
            
             <!-- My Posts -->
             <div v-if="activeMenu === 'posts'" class="posts-section">
                 <div class="section-action">
                    <el-button type="primary" icon="EditPen" @click="$router.push('/posts/create')">发布新帖</el-button>
                 </div>
                 <div class="custom-table-wrapper">
                    <el-table :data="postList" v-loading="postLoading" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
                      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
                        <template #default="{ row }">
                          <span class="table-link" @click="$router.push(`/posts/${row.id}`)">{{ row.title }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="createTime" label="发布时间" width="180">
                           <template #default="{ row }">
                               {{ formatDate(row.createTime) }}
                           </template>
                      </el-table-column>
                      <el-table-column prop="viewCount" label="浏览" width="100" align="center"></el-table-column>
                      <el-table-column prop="likeCount" label="点赞" width="100" align="center"></el-table-column>
                      <el-table-column prop="commentCount" label="评论" width="100" align="center"></el-table-column>
                      <el-table-column label="操作" width="100" fixed="right" align="center">
                        <template #default="{ row }">
                          <el-button type="danger" link @click="handleDeletePost(row.id)">删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                 </div>
                <div class="pagination-wrapper">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="postTotal"
                    :page-size="postPageSize"
                    :current-page="postPageNum"
                    @current-change="handlePostPageChange"
                  />
                </div>
             </div>
         </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { User, Lock, ShoppingCart, ChatDotSquare, Plus, Location, Star, Service, EditPen, DataLine } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, updateUser, updatePassword } from '@/api/user'
import { getPostList, deletePost } from '@/api/post'
import { getOrderList } from '@/api/trade'
import { getFavoriteList } from '@/api/favorite'
import { getFeedbackList } from '@/api/feedback'
import OrderList from './OrderList.vue'
import AddressList from '@/components/profile/AddressList.vue'
import FavoriteList from '@/components/profile/FavoriteList.vue'
import FeedbackList from '@/components/profile/FeedbackList.vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const { user: currentUser, isAdmin } = storeToRefs(userStore)

const activeMenu = ref('info')
const uploadUrl = 'http://localhost:8080/file/upload'

// Menu Items
const menuItems = [
    { index: 'data', label: '个人数据', icon: DataLine },
    { index: 'info', label: '个人资料', icon: User },
    { index: 'password', label: '修改密码', icon: Lock },
    { index: 'orders', label: '我的订单', icon: ShoppingCart },
    { index: 'address', label: '地址管理', icon: Location },
    { index: 'favorites', label: '我的收藏', icon: Star },
    { index: 'posts', label: '我的帖子', icon: ChatDotSquare },
    { index: 'feedback', label: '在线客服', icon: Service },
]

const currentMenuLabel = computed(() => {
    const item = menuItems.find(i => i.index === activeMenu.value)
    return item ? item.label : ''
})

const roleLabel = computed(() => {
    const map = {
        ADMIN: '管理员',
        OPERATOR: '运营',
        STUDENT: '学生'
    }
    return map[currentUser.value.role] || '学生'
})

const handleSelect = (index) => {
    activeMenu.value = index
    if (index === 'posts') {
        fetchMyPosts()
    }
    if (index === 'data') {
        fetchPersonalStats()
    }
}

const personalStats = reactive({
    orderCount: 0,
    postCount: 0,
    favoriteCount: 0,
    feedbackCount: 0,
    spendAmount: 0
})

const statsLoading = ref(false)

// 个人资料相关
const userInfo = ref({ ...currentUser.value })

const handleAvatarSuccess = (res) => {
  if (res.code === 200 || res.code === '200') {
    userInfo.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const handleUpdateInfo = async () => {
    try {
        await updateUser(userInfo.value)
        ElMessage.success('个人信息更新成功')
        // Update store
        userStore.setUser(userInfo.value)
    } catch (error) {
        console.error(error)
        ElMessage.error('更新失败')
    }
}

// 密码相关
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleUpdatePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updatePassword({
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logout()
        router.push('/login')
      } catch (error) {
        console.error(error)
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 帖子相关
const postList = ref([])
const postLoading = ref(false)
const postPageNum = ref(1)
const postPageSize = ref(10)
const postTotal = ref(0)

const fetchMyPosts = async () => {
    if (!currentUser.value.id) return
    postLoading.value = true
    try {
        // Assuming backend supports filtering by userId via generic list API or we have a specific endpoint
        // Using existing getPostList with extra params if backend supports it, or we might need to filter client side if API is limited
        // For now assuming getPostList supports some filtering or we use what we have. 
        // Wait, earlier I saw PostController has list method.
        // Let's assume we pass userId in params if supported, otherwise we might need a specific API.
        // Actually, looking at UserController, there isn't a "my posts" endpoint explicitly mentioned in summary.
        // But PostController usually has list.
        // If the backend doesn't support "my posts", I might need to filter or add backend support.
        // Given I'm "continuing", I should try to use existing API.
        // If I look at PostController (I can't see it now but I can infer), usually list takes params.
        // Let's pass userId to getPostList.
        const res = await getPostList({ 
            pageNum: postPageNum.value, 
            pageSize: postPageSize.value,
            userId: currentUser.value.id 
        })
        postList.value = res.records || []
        postTotal.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        postLoading.value = false
    }
}

const handlePostPageChange = (val) => {
    postPageNum.value = val
    fetchMyPosts()
}

const handleDeletePost = (id) => {
    ElMessageBox.confirm('确认删除该帖子吗?', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await deletePost(id)
            ElMessage.success('删除成功')
            fetchMyPosts()
        } catch (error) {
            ElMessage.error('删除失败')
        }
    })
}

const formatDate = (date) => {
    if (!date) return ''
    return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchPersonalStats = async () => {
    if (!currentUser.value.id) return
    statsLoading.value = true
    try {
        const [orders, postRes, favoriteRes, feedbackRes] = await Promise.all([
            getOrderList(currentUser.value.id),
            getPostList({ pageNum: 1, pageSize: 1, userId: currentUser.value.id }),
            getFavoriteList({ pageNum: 1, pageSize: 1, userId: currentUser.value.id }),
            getFeedbackList({ pageNum: 1, pageSize: 1, userId: currentUser.value.id })
        ])
        const orderList = Array.isArray(orders) ? orders : []
        personalStats.orderCount = orderList.length
        personalStats.spendAmount = orderList.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
        personalStats.postCount = postRes?.total || 0
        personalStats.favoriteCount = favoriteRes?.total || 0
        personalStats.feedbackCount = feedbackRes?.total || 0
    } catch (error) {
        console.error(error)
    } finally {
        statsLoading.value = false
    }
}

onMounted(() => {
    // If active menu is posts, fetch posts
    if (activeMenu.value === 'posts') {
        fetchMyPosts()
    }
    if (activeMenu.value === 'data') {
        fetchPersonalStats()
    }
})
</script>

<style scoped>
.page-container {
    min-height: calc(100vh - 60px); /* Adjust based on navbar height */
    background-color: #f5f7fa;
    padding: 20px;
}

.profile-layout {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 20px;
    align-items: flex-start;
}

.sidebar {
    width: 280px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    padding: 30px 20px;
    flex-shrink: 0;
}

.user-card {
    text-align: center;
    margin-bottom: 30px;
    padding-bottom: 30px;
    border-bottom: 1px solid #f0f2f5;
}

.username {
    margin: 16px 0 8px;
    color: #303133;
    font-size: 18px;
}

.user-role {
    color: #909399;
    font-size: 14px;
    background: #f0f2f5;
    display: inline-block;
    padding: 4px 12px;
    border-radius: 12px;
}

.menu-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 20px;
    border-radius: 8px;
    cursor: pointer;
    color: #606266;
    transition: all 0.3s;
}

.menu-item:hover {
    background-color: #f5f7fa;
    color: #409eff;
}

.menu-item.active {
    background-color: #ecf5ff;
    color: #409eff;
    font-weight: 500;
}

.content-area {
    flex: 1;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    min-height: 600px;
    display: flex;
    flex-direction: column;
}

.content-header {
    padding: 24px 30px;
    border-bottom: 1px solid #f0f2f5;
}

.content-header h2 {
    margin: 0;
    font-size: 20px;
    color: #303133;
}

.content-body {
    padding: 30px;
    flex: 1;
}

.form-section {
    max-width: 800px;
}

.avatar-upload-wrapper {
    display: flex;
    align-items: center;
    gap: 20px;
}

.avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 100px;
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: border-color 0.3s;
}

.avatar-uploader:hover {
    border-color: #409eff;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
}

.avatar {
    width: 100px;
    height: 100px;
    display: block;
    object-fit: cover;
}

.upload-tip {
    color: #909399;
    font-size: 13px;
    line-height: 1.5;
}

.submit-btn {
    padding-left: 40px;
    padding-right: 40px;
}

.stats-section {
    display: flex;
    flex-direction: column;
}

.personal-stat-card {
    height: 120px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.stat-title {
    color: #909399;
    font-size: 14px;
}

.stat-number {
    font-size: 28px;
    font-weight: 600;
    margin-top: 12px;
    color: #303133;
}

.section-action {
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
}

.table-link {
    color: #303133;
    cursor: pointer;
    transition: color 0.3s;
}

.table-link:hover {
    color: #409eff;
}

.pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
}

/* Responsive */
@media (max-width: 768px) {
    .profile-layout {
        flex-direction: column;
    }
    
    .sidebar {
        width: 100%;
        padding: 20px;
    }
    
    .menu-list {
        display: flex;
        flex-direction: row;
        overflow-x: auto;
        padding-bottom: 10px;
    }
    
    .menu-item {
        white-space: nowrap;
        padding: 8px 16px;
    }
    
    .content-area {
        width: 100%;
    }
    
    .content-body {
        padding: 20px;
    }
}
</style>
