<template>
  <div class="forum-manage">
    <div class="header">
      <h2>论坛管理</h2>
      <div class="filters">
        <el-input v-model="keyword" placeholder="搜索帖子标题/作者" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-select v-model="category" placeholder="板块筛选" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
          <el-option label="公共课交流" :value="1" />
          <el-option label="专业课交流" :value="2" />
          <el-option label="院校复试经验" :value="3" />
          <el-option label="其他" :value="4" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="postList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="category" label="板块" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryTag(row.category)">{{ getCategoryText(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="作者" width="120">
          <template #default="{ row }">
            {{ row.nickname || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column prop="viewCount" label="阅读" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="likeCount" label="收藏" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.isTop"
              :active-value="1"
              :inactive-value="0"
              @change="handlePinChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="success" link @click="handleComments(row)">评论</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" type="warning" link @click="handleAudit(row, 2)">拒绝</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 帖子详情对话框 -->
    <el-dialog title="帖子详情" v-model="dialogVisible" width="600px">
        <div v-if="currentPost">
            <h3>{{ currentPost.title }}</h3>
            <div class="post-meta">
                <span>作者: {{ currentPost.nickname || '未知' }}</span>
                <span>时间: {{ currentPost.createTime }}</span>
                <span>板块: {{ getCategoryText(currentPost.category) }}</span>
            </div>
            <div class="post-content" v-html="currentPost.content"></div>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">关闭</el-button>
                <el-button v-if="currentPost && currentPost.status === 0" type="success" @click="handleAudit(currentPost, 1)">通过</el-button>
                <el-button v-if="currentPost && currentPost.status === 0" type="warning" @click="handleAudit(currentPost, 2)">拒绝</el-button>
                <el-button type="danger" @click="handleDelete(currentPost)">删除此贴</el-button>
            </span>
        </template>
    </el-dialog>
    
    <AdminCommentDialog
        v-model="commentDialogVisible"
        :target-id="currentPostId"
        :target-type="1"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostListAll, deletePost, toggleTop, auditPost } from '@/api/post'
import AdminCommentDialog from '@/components/AdminCommentDialog.vue'

const loading = ref(false)
const postList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const category = ref('')

const commentDialogVisible = ref(false)
const currentPostId = ref(null)

const dialogVisible = ref(false)
const currentPost = ref(null)

const getCategoryText = (cat) => {
  const map = { 1: '公共课交流', 2: '专业课交流', 3: '院校复试经验', 4: '其他' }
  return map[cat] || '未知'
}

const getCategoryTag = (cat) => {
  const map = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
  return map[cat] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '已发布', 2: '已拒绝' }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPostListAll({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      category: category.value === '' ? undefined : category.value
    })
    postList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchData()
}

const handlePinChange = async (row) => {
  try {
    await toggleTop(row.id, row.isTop)
    ElMessage.success(row.isTop === 1 ? '已置顶' : '已取消置顶')
  } catch (error) {
    row.isTop = row.isTop === 1 ? 0 : 1
  }
}

const handleAudit = async (row, status) => {
  try {
    await auditPost(row.id, status)
    ElMessage.success(status === 1 ? '审核通过' : '已拒绝')
    if (dialogVisible.value) dialogVisible.value = false
    fetchData()
  } catch (error) {
    // error handled by request interceptor
  }
}

const handleComments = (row) => {
    currentPostId.value = row.id
    commentDialogVisible.value = true
}

const handleView = (row) => {
    currentPost.value = row
    dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该帖子吗? 删除后无法恢复', '警告', { type: 'warning' })
    .then(async () => {
      await deletePost(row.id)
      ElMessage.success('删除成功')
      if (dialogVisible.value) dialogVisible.value = false
      fetchData()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.forum-manage {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.filters {
  display: flex;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.post-meta {
    margin: 10px 0;
    color: #999;
    font-size: 14px;
    display: flex;
    gap: 15px;
}
.post-content {
    line-height: 1.6;
    padding: 10px 0;
    border-top: 1px solid #eee;
}
</style>
