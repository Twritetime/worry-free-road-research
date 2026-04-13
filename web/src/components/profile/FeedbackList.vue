<template>
  <div class="feedback-list">
    <div class="header-actions">
      <el-button type="primary" icon="EditPen" @click="dialogVisible = true">提交反馈</el-button>
    </div>

    <div class="custom-table-wrapper">
      <el-table :data="feedbackList" style="width: 100%" v-loading="loading" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
        <el-table-column prop="content" label="反馈内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="180" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" effect="plain" round>
              {{ row.status === 1 ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
             <el-empty description="暂无反馈记录" />
        </template>
      </el-table>
    </div>

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

    <!-- 提交反馈对话框 -->
    <el-dialog v-model="dialogVisible" title="提交反馈" width="600px" class="custom-dialog">
      <el-form :model="form" ref="formRef" :rules="rules" label-position="top">
        <el-form-item label="问题类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择问题类型" size="large" style="width: 100%">
            <el-option label="功能建议" value="功能建议" />
            <el-option label="Bug反馈" value="Bug反馈" />
            <el-option label="内容纠错" value="内容纠错" />
            <el-option label="账号问题" value="账号问题" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入手机号或邮箱" size="large" />
        </el-form-item>
        <el-form-item label="问题描述" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您的问题（10-500字）"
            resize="none"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="反馈详情" width="600px" class="custom-dialog">
      <div v-if="currentFeedback" class="feedback-detail">
        <div class="detail-meta">
          <el-tag v-if="currentFeedback.type" type="primary" effect="plain">{{ currentFeedback.type }}</el-tag>
          <span class="contact-info" v-if="currentFeedback.contact">
            <el-icon><Message /></el-icon> {{ currentFeedback.contact }}
          </span>
          <span class="time-info">
            <el-icon><Clock /></el-icon> {{ currentFeedback.createTime }}
          </span>
        </div>
        <div class="detail-content">
          <div class="content-label">问题描述：</div>
          <div class="content-text">{{ currentFeedback.content }}</div>
        </div>

        <el-divider />

        <div class="feedback-chat">
          <div class="chat-item admin" v-if="currentFeedback.status === 1">
              <div class="chat-avatar">
                  <el-avatar :size="36" icon="Service" class="admin-avatar" />
              </div>
              <div class="chat-content">
                  <div class="chat-info">
                      <span class="name">客服回复</span>
                      <span class="time">{{ currentFeedback.replyTime }}</span>
                  </div>
                  <div class="chat-bubble admin-bubble">
                      {{ currentFeedback.reply }}
                  </div>
              </div>
          </div>

          <div v-else class="waiting-reply">
              <el-empty description="等待客服回复中..." :image-size="80" />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFeedbackList, createFeedback } from '@/api/feedback'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { EditPen, Service, Message, Clock } from '@element-plus/icons-vue'

const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const feedbackList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  type: '',
  contact: '',
  content: ''
})
const rules = {
  type: [{ required: true, message: '请选择问题类型', trigger: 'change' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  content: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 10, max: 500, message: '问题描述需要在10-500字之间', trigger: 'blur' }
  ]
}

const detailVisible = ref(false)
const currentFeedback = ref(null)

const fetchFeedbacks = async () => {
  loading.value = true
  try {
    const res = await getFeedbackList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      userId: user.value.id
    })
    feedbackList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchFeedbacks()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await createFeedback({
          userId: user.value.id,
          type: form.type,
          contact: form.contact,
          content: form.content
        })
        ElMessage.success('提交成功')
        dialogVisible.value = false
        form.type = ''
        form.contact = ''
        form.content = ''
        fetchFeedbacks()
      } catch (error) {
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleView = (row) => {
  currentFeedback.value = row
  detailVisible.value = true
}

onMounted(() => {
  if (user.value.id) {
    fetchFeedbacks()
  }
})
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.custom-table-wrapper {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    overflow: hidden;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
}

.feedback-chat {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.chat-item {
    display: flex;
    gap: 15px;
}

.chat-item.user {
    flex-direction: row-reverse;
}

.chat-content {
    max-width: 80%;
    display: flex;
    flex-direction: column;
}

.chat-item.user .chat-content {
    align-items: flex-end;
}

.chat-info {
    font-size: 12px;
    color: #999;
    margin-bottom: 5px;
}

.chat-item.user .chat-info {
    text-align: right;
}

.chat-info .name {
    margin-right: 8px;
    font-weight: bold;
    color: #333;
}

.chat-item.user .chat-info .name {
    margin-right: 0;
    margin-left: 8px;
}

.chat-bubble {
    padding: 10px 15px;
    border-radius: 8px;
    line-height: 1.5;
    position: relative;
    word-break: break-all;
}

.user-bubble {
    background-color: #ecf5ff;
    color: #409eff;
    border-top-right-radius: 0;
}

.admin-bubble {
    background-color: #f4f4f5;
    color: #606266;
    border-top-left-radius: 0;
}

.admin-avatar {
    background-color: #409eff;
    color: white;
}

.waiting-reply {
    padding: 20px 0;
}

.image-upload-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.upload-tip {
    font-size: 12px;
    color: #909399;
}

.feedback-detail {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.detail-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-wrap: wrap;
}

.contact-info, .time-info {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #909399;
    font-size: 14px;
}

.detail-images {
    display: flex;
    align-items: flex-start;
    gap: 8px;
}

.images-label {
    color: #606266;
    font-size: 14px;
    flex-shrink: 0;
}

.image-list {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.detail-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    cursor: pointer;
}

.detail-content {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.content-label {
    color: #606266;
    font-size: 14px;
}

.content-text {
    color: #303133;
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-all;
}
</style>
