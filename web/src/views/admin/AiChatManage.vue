<template>
  <div class="admin-ai-chat">
    <div class="header">
      <h2>AI助手管理</h2>
      <div class="filters">
        <el-input
          v-model="keyword"
          placeholder="搜索内容/用户名"
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

    <div class="stats-cards" v-loading="statsLoading">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalMessages || 0 }}</div>
          <div class="stat-label">总消息数</div>
        </div>
        <el-icon class="stat-icon" color="#409EFF"><ChatDotRound /></el-icon>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-number">{{ stats.todayMessages || 0 }}</div>
          <div class="stat-label">今日消息</div>
        </div>
        <el-icon class="stat-icon" color="#67C23A"><Calendar /></el-icon>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-number">{{ stats.activeUsers || 0 }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
        <el-icon class="stat-icon" color="#E6A23C"><User /></el-icon>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalSessions || 0 }}</div>
          <div class="stat-label">会话总数</div>
        </div>
        <el-icon class="stat-icon" color="#F56C6C"><Connection /></el-icon>
      </el-card>
    </div>

    <el-card>
      <el-table :data="chatList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nickname" label="用户" width="120">
          <template #default="{ row }">
            <span>{{ row.nickname || '未知用户' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="用户消息" show-overflow-tooltip min-width="250">
          <template #default="{ row }">
            <span>{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sessionId" label="会话ID" width="200">
          <template #default="{ row }">
            <span style="font-size: 12px; color: #909399">{{ row.sessionId ? row.sessionId.substring(0, 16) + '...' : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="对话时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewSession(row)">
              查看对话
            </el-button>
            <el-button type="danger" link @click="deleteSession(row)">
              删除
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

    <el-dialog v-model="dialogVisible" title="查看完整对话" width="720px" top="5vh">
      <div class="session-header" v-if="sessionMessages.length > 0">
        <div class="session-info">
          <span><strong>用户：</strong>{{ sessionMessages[0]?.nickname || '未知' }}</span>
          <span><strong>消息数：</strong>{{ sessionMessages.length }} 条</span>
          <span><strong>时间范围：</strong>{{ sessionMessages.length > 0 ? formatTime(sessionMessages[0].createTime) + ' ~ ' + formatTime(sessionMessages[sessionMessages.length - 1].createTime) : '' }}</span>
        </div>
        <el-button type="danger" size="small" @click="confirmDeleteSession">
          删除整个会话
        </el-button>
      </div>
      <div class="session-chat" v-if="sessionMessages.length > 0">
        <div class="chat-messages">
          <div
            v-for="(msg, index) in sessionMessages"
            :key="index"
            :class="['message-item', msg.role === 1 ? 'user' : 'assistant']"
          >
            <div class="message-role">
              <el-tag :type="msg.role === 1 ? 'primary' : 'success'" size="small">
                {{ msg.role === 1 ? '用户' : 'AI助手' }}
              </el-tag>
              <span class="message-time">{{ formatShortTime(msg.createTime) }}</span>
            </div>
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无对话记录" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, ChatDotRound, Calendar, User, Connection } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminChatList,
  getAdminChatSession,
  deleteAdminChatSession,
  deleteAdminChatMessage,
  getAdminChatStats
} from '@/api/ai'

const loading = ref(false)
const statsLoading = ref(false)
const chatList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const sessionMessages = ref([])
const currentSessionId = ref('')
const currentRow = ref(null)

const stats = ref({
  totalMessages: 0,
  todayMessages: 0,
  activeUsers: 0,
  totalSessions: 0
})

const fetchStats = async () => {
  statsLoading.value = true
  try {
    const res = await getAdminChatStats()
    if (res) {
      stats.value = res
    }
  } catch (error) {
    console.error(error)
  } finally {
    statsLoading.value = false
  }
}

const fetchChatList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined
    }
    const res = await getAdminChatList(params)
    chatList.value = res.records || []
    total.value = res.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取AI聊天记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchChatList()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchChatList()
}

const viewSession = async (row) => {
  if (!row.sessionId) {
    ElMessage.warning('该消息没有关联的会话')
    return
  }
  currentRow.value = row
  currentSessionId.value = row.sessionId
  dialogVisible.value = true

  try {
    const messages = await getAdminChatSession(row.sessionId)
    sessionMessages.value = messages || []
  } catch (error) {
    console.error(error)
    ElMessage.error('获取对话详情失败')
    sessionMessages.value = []
  }
}

const deleteSession = async (row) => {
  if (!row.sessionId) {
    ElMessage.warning('该消息没有关联的会话')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除该用户的整个对话记录吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteAdminChatSession(row.sessionId)
    ElMessage.success('删除成功')
    fetchChatList()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const confirmDeleteSession = async () => {
  if (!currentSessionId.value) return

  try {
    await ElMessageBox.confirm(
      '确定要删除整个会话的所有消息吗？此操作不可恢复！',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteAdminChatSession(currentSessionId.value)
    ElMessage.success('删除成功')
    dialogVisible.value = false
    fetchChatList()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

const formatShortTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

onMounted(() => {
  fetchStats()
  fetchChatList()
})
</script>

<style scoped>
.admin-ai-chat {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: default;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  font-size: 40px;
  opacity: 0.8;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.session-info {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: #606266;
}

.chat-messages {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
}

.message-item {
  margin-bottom: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  background: #fafafa;
}

.message-item.user {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.message-item.assistant {
  background: #f0f9eb;
  border-left: 3px solid #67c23a;
}

.message-role {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-content {
  font-size: 14px;
  line-height: 1.7;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
