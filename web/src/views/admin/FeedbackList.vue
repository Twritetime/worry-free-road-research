<template>
  <div class="admin-feedback-list">
    <div class="header">
      <h2>反馈管理</h2>
      <div class="filters">
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
          <el-option label="功能建议" value="suggest" />
          <el-option label="Bug反馈" value="bug" />
          <el-option label="内容纠错" value="complaint" />
          <el-option label="账号问题" value="account" />
          <el-option label="其他" value="other" />
        </el-select>
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
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0" style="margin-left: 10px">
          <el-icon><Delete /></el-icon> 批量删除
        </el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="feedbackList" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="问题类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type" :type="getTypeTagType(row.type)" effect="plain">{{ getTypeLabel(row.type) }}</el-tag>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系方式" width="150">
          <template #default="{ row }">
            <span v-if="row.contact">{{ row.contact }}</span>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
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
      width="600px"
    >
      <div class="feedback-detail" v-if="currentFeedback">
        <div class="detail-row">
          <span class="label">用户：</span>
          <span class="value">{{ currentFeedback.nickname }}</span>
        </div>
        <div class="detail-row" v-if="currentFeedback.type">
          <span class="label">问题类型：</span>
          <el-tag :type="getTypeTagType(currentFeedback.type)" effect="plain">{{ getTypeLabel(currentFeedback.type) }}</el-tag>
        </div>
        <div class="detail-row" v-if="currentFeedback.contact">
          <span class="label">联系方式：</span>
          <span class="value">{{ currentFeedback.contact }}</span>
        </div>
        <div class="detail-row">
          <span class="label">时间：</span>
          <span class="value">{{ currentFeedback.createTime ? currentFeedback.createTime.replace('T', ' ') : '' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">内容：</span>
          <div class="content-box">{{ currentFeedback.content }}</div>
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
import { Search, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeedbackList, replyFeedback, batchDeleteFeedback } from '@/api/feedback'

const loading = ref(false)
const feedbackList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterStatus = ref('')
const filterType = ref('')
const selectedRows = ref([])

const replyDialogVisible = ref(false)
const currentFeedback = ref(null)
const replyContent = ref('')
const submitting = ref(false)

const fetchFeedbacks = async () => {
  loading.value = true
  try {
    const params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value,
        type: filterType.value || undefined,
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

const typeMap = {
  suggest: '功能建议',
  bug: 'Bug反馈',
  complaint: '内容纠错',
  account: '账号问题',
  other: '其他'
}

const typeTagMap = {
  suggest: 'primary',
  bug: 'danger',
  complaint: 'warning',
  account: 'info',
  other: ''
}

const getTypeLabel = (type) => {
  return typeMap[type] || type
}

const getTypeTagType = (type) => {
  return typeTagMap[type] || 'primary'
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

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的反馈')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条反馈吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteFeedback(ids)
    ElMessage.success('删除成功')
    fetchFeedbacks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
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

.feedback-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.detail-row .label {
  color: #606266;
  font-size: 14px;
  flex-shrink: 0;
}

.detail-row .value {
  color: #303133;
  font-size: 14px;
}

.image-preview {
  display: flex;
  align-items: center;
  gap: 4px;
  position: relative;
}

.table-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  cursor: pointer;
}

.image-count {
  position: absolute;
  right: -8px;
  top: -4px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 4px;
}
</style>
