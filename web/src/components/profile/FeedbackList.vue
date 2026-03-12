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
    <el-dialog v-model="dialogVisible" title="提交反馈" width="500px" class="custom-dialog">
      <el-form :model="form" ref="formRef" :rules="rules">
        <el-form-item prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入您的反馈或建议，我们会尽快回复"
            resize="none"
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
      <div v-if="currentFeedback" class="feedback-chat">
        <div class="chat-item user">
            <div class="chat-avatar">
                <el-avatar :size="36" :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            </div>
            <div class="chat-content">
                <div class="chat-info">
                    <span class="name">我</span>
                    <span class="time">{{ currentFeedback.createTime }}</span>
                </div>
                <div class="chat-bubble user-bubble">
                    {{ currentFeedback.content }}
                </div>
            </div>
        </div>
        
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
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFeedbackList, createFeedback } from '@/api/feedback'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { EditPen, Service } from '@element-plus/icons-vue'

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
  content: ''
})
const rules = {
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
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
          content: form.content
        })
        ElMessage.success('提交成功')
        dialogVisible.value = false
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
</style>
