<template>
  <div class="admin-feedback-list">
    <div class="header">
      <h2>反馈管理</h2>
      <div class="filters">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px; margin-right: 10px">
          <el-option label="待处理" value="pending" />
          <el-option label="已回复" value="replied" />
        </el-select>
        <el-input
          v-model="keyword"
          placeholder="搜索用户名/内容"
          style="width: 200px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-card>
      <el-table :data="feedbackList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <!-- Type column removed as it is not in backend entity -->
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="nickname" label="用户" width="120" />
        <el-table-column prop="createTime" label="提交时间" width="180">
            <template #default="{ row }">
                {{ row.createTime ? row.createTime.replace('T', ' ') : '' }}
            </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已回复' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleReply(row)" v-if="row.status !== 1">
              回复
            </el-button>
            <el-button type="info" link @click="handleView(row)" v-else>
              查看
            </el-button>
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

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      :title="currentFeedback?.status === 1 ? '反馈详情' : '回复反馈'"
      width="500px"
    >
      <div class="feedback-detail" v-if="currentFeedback">
        <p><strong>用户：</strong>{{ currentFeedback.nickname }}</p>
        <p><strong>时间：</strong>{{ currentFeedback.createTime ? currentFeedback.createTime.replace('T', ' ') : '' }}</p>
        <p><strong>内容：</strong></p>
        <div class="content-box">{{ currentFeedback.content }}</div>
        
        <div v-if="currentFeedback.images && currentFeedback.images.length" class="images-box">
             <el-image 
                v-for="(img, index) in currentFeedback.images" 
                :key="index"
                :src="img" 
                :preview-src-list="currentFeedback.images"
                fit="cover"
                style="width: 100px; height: 100px; margin-right: 10px"
             />
        </div>
      </div>
      
      <div class="reply-section" style="margin-top: 20px">
        <p><strong>回复：</strong></p>
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="4"
          placeholder="请输入回复内容"
          :disabled="currentFeedback?.status === 1"
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="submitReply" 
            v-if="currentFeedback?.status !== 1"
            :loading="submitting"
          >
            提交回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getFeedbackList, replyFeedback } from '@/api/feedback'

const loading = ref(false)
const feedbackList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterStatus = ref('')

const replyDialogVisible = ref(false)
const currentFeedback = ref(null)
const replyContent = ref('')
const submitting = ref(false)

const getFeedbackTypeTag = (type) => {
  const map = {
    'bug': 'danger',
    'suggestion': 'primary',
    'complaint': 'warning',
    'other': 'info'
  }
  // If type is not in map, maybe it is a number or Chinese? 
  // Assuming type is string for now, or default to info
  return map[type] || 'info'
}

const getFeedbackTypeText = (type) => {
  const map = {
    'bug': '系统缺陷',
    'suggestion': '功能建议',
    'complaint': '投诉举报',
    'other': '其他'
  }
  return map[type] || '其他'
}

const fetchFeedbacks = async () => {
  loading.value = true
  try {
    const params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value,
        status: filterStatus.value === 'pending' ? 0 : (filterStatus.value === 'replied' ? 1 : undefined)
    }
    const res = await getFeedbackList(params)
    feedbackList.value = res.records || []
    total.value = res.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchFeedbacks()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchFeedbacks()
}

const handleReply = (row) => {
  currentFeedback.value = row
  replyContent.value = ''
  replyDialogVisible.value = true
}

const handleView = (row) => {
  currentFeedback.value = row
  replyContent.value = row.reply || ''
  replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  submitting.value = true
  try {
    await replyFeedback({
        id: currentFeedback.value.id,
        reply: replyContent.value
    })
    
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    
    // Refresh list
    fetchFeedbacks()
  } catch (error) {
    console.error(error)
    ElMessage.error('回复失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchFeedbacks()
})
</script>

<style scoped>
.admin-feedback-list {
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
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.content-box {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin: 5px 0;
  white-space: pre-wrap;
}

.images-box {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
}
</style>
