<template>
  <div class="ai-chat-wrapper">
    <div v-if="isOpen" class="ai-chat-dialog" :style="dialogStyle" ref="dialogRef"
         @mousedown="startDrag"
    >
      <div class="chat-header">
        <div class="header-info">
          <el-icon class="ai-icon"><Service /></el-icon>
          <div class="header-text">
            <span class="header-title">研路无忧AI助手</span>
            <span class="header-status">24小时在线</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button text @click="toggleFullscreen" class="action-btn">
            <el-icon><FullScreen /></el-icon>
          </el-button>
          <el-button text @click="minimizeChat" class="action-btn">
            <el-icon><Minus /></el-icon>
          </el-button>
          <el-button text @click="clearHistory" class="action-btn" title="清空对话">
            <el-icon><Delete /></el-icon>
          </el-button>
          <el-button text @click="closeChat" class="action-btn close-btn">
            <el-icon><CloseBold /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="chat-body" ref="chatBody" @click="handleMessageClick">
        <div v-if="!isLoggedIn" class="login-tip">
          <el-icon><Warning /></el-icon>
          <span>请先登录后使用AI助手</span>
          <el-button type="primary" size="small" @click="goLogin">去登录</el-button>
        </div>

        <div v-else class="message-list">
          <div class="welcome-message">
            <el-icon class="welcome-icon"><Service /></el-icon>
            <div class="welcome-text">
              <p>👋 你好！我是研路无忧AI助手</p>
              <p>我可以帮你：</p>
              <ul>
                <li>解答考研政策、备考规划问题</li>
                <li>介绍平台各页面功能</li>
                <li>推荐合适的复习资料</li>
                <li>分享复试调剂经验</li>
              </ul>
            </div>
          </div>

          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role === 1 ? 'user' : 'assistant']"
          >
            <div class="message-avatar">
              <el-avatar v-if="msg.role === 1" :size="32" :src="userAvatar" />
              <el-avatar v-else :size="32" class="ai-avatar">
                <el-icon><Service /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>

          <div v-if="loading" class="message assistant">
            <div class="message-avatar">
              <el-avatar :size="32" class="ai-avatar">
                <el-icon><Service /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text typing">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <div class="quick-questions">
          <el-tag
            v-for="q in quickQuestions"
            :key="q.text"
            class="quick-tag"
            @click="sendQuickQuestion(q.text)"
          >
            {{ q.text }}
          </el-tag>
        </div>
        <div class="input-area">
          <el-input
            v-model="inputMessage"
            placeholder="输入你的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading || !isLoggedIn"
          />
          <el-button
            type="primary"
            @click="sendMessage"
            :disabled="!inputMessage.trim() || loading"
            :loading="loading"
          >
            <el-icon v-if="!loading"><Promotion /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="resize-handle" @mousedown="startResize"></div>
    </div>

    <div v-else class="chat-toggle" @click="toggleChat">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="9">
        <el-button circle size="large" type="primary" class="toggle-btn">
          <el-icon size="24"><Service /></el-icon>
        </el-button>
      </el-badge>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { Service, Close, Promotion, Warning, Minus, FullScreen, CloseBold, Delete } from '@element-plus/icons-vue'
import { sendChatMessage, getChatHistory, clearChatHistory } from '@/api/ai'

const router = useRouter()
const userStore = useUserStore()
const { user: currentUser, isLoggedIn } = storeToRefs(userStore)

const isOpen = ref(false)
const isMinimized = ref(false)
const isFullscreen = ref(false)
const loading = ref(false)
const inputMessage = ref('')
const messages = ref([])
const chatBody = ref(null)
const unreadCount = ref(0)
const sessionId = ref(localStorage.getItem('ai_session_id') || '')

const dialogRef = ref(null)

const position = ref({ x: 0, y: 0 })
const size = ref({ width: 380, height: 520 })
const isDragging = ref(false)
const isResizing = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const resizeStart = ref({ x: 0, y: 0, width: 0, height: 0 })

const userAvatar = computed(() => currentUser.value?.avatar || '')

const dialogStyle = computed(() => {
  if (isFullscreen.value) {
    return {
      left: '0',
      top: '0',
      width: '100vw',
      height: '100vh',
      borderRadius: '0'
    }
  }
  return {
    left: position.value.x + 'px',
    top: position.value.y + 'px',
    width: size.value.width + 'px',
    height: isMinimized.value ? 'auto' : size.value.height + 'px'
  }
})

const quickQuestions = [
  { text: '平台有哪些功能' },
  { text: '如何下载资料' },
  { text: '考研时间节点' },
  { text: '怎么联系客服' }
]

watch(isOpen, async (newVal) => {
  if (newVal) {
    unreadCount.value = 0
    if (isLoggedIn.value) {
      await loadHistory()
    }
  }
})

watch(isLoggedIn, async (newVal) => {
  if (newVal) {
    await loadHistory()
  }
})

onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  document.addEventListener('click', handleClickOutside)
  if (isLoggedIn.value) {
    loadHistory()
  }
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  document.removeEventListener('click', handleClickOutside)
})

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    resetPosition()
  }
}

const closeChat = () => {
  isOpen.value = false
}

const minimizeChat = () => {
  isMinimized.value = !isMinimized.value
}

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const resetPosition = () => {
  if (window.innerWidth > 500) {
    position.value = { x: window.innerWidth - 400, y: window.innerHeight - 540 }
  } else {
    position.value = { x: 10, y: window.innerHeight - 450 }
  }
}

const goLogin = () => {
  isOpen.value = false
  router.push('/login')
}

const loadHistory = async () => {
  try {
    const sid = sessionId.value || undefined
    const res = await getChatHistory({ sessionId: sid, limit: 50 })
    if (res && Array.isArray(res) && res.length > 0) {
      messages.value = res
      if (res[0].sessionId) {
        sessionId.value = res[0].sessionId
        localStorage.setItem('ai_session_id', sessionId.value)
      }
      scrollToBottom()
    }
  } catch (error) {
    console.error('Failed to load chat history:', error)
  }
}

const sendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content || loading.value) return

  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  inputMessage.value = ''
  messages.value.push({
    role: 1,
    content,
    createTime: new Date().toISOString()
  })
  scrollToBottom()

  loading.value = true
  try {
    const res = await sendChatMessage(content)
    if (res) {
      if (!sessionId.value && res.sessionId) {
        sessionId.value = res.sessionId
        localStorage.setItem('ai_session_id', res.sessionId)
      }
      messages.value.push(res)
      scrollToBottom()
    }
  } catch (error) {
    console.error('Failed to send message:', error)
    ElMessage.error('发送失败，请重试')
  } finally {
    loading.value = false
  }
}

const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

const formatMessage = (content) => {
  if (!content) return ''
  let result = content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')

  // 支持两种格式：
  // 1. [名称](path=/materials/123) - 新格式，直接跳转
  // 2. [名称](path=/materials&id=123) - 旧格式，兼容处理
  result = result.replace(/\[([^\]]+)\]\(path=([^)]+)\)/g, (match, text, path) => {
    if (!path.startsWith('/')) {
      path = '/'
    }
    // 新格式：path=/materials/123，直接作为完整路径
    // 旧格式：path=/materials&id=123，解析参数
    if (path.includes('&')) {
      // 旧格式，解析参数
      const paramObj = {}
      const pairs = path.split('&')
      pairs.forEach(pair => {
        const [key, ...rest] = pair.split('=')
        const value = rest.join('=')
        paramObj[key] = value
      })
      let basePath = paramObj.path || '/'
      const id = paramObj.id || ''
      const category = paramObj.category || ''
      if (id) {
        return `<a class="ai-link" data-target="${basePath}/${id}">${text}</a>`
      } else if (category) {
        return `<a class="ai-link" data-target="${basePath}?category=${category}">${text}</a>`
      }
      return `<a class="ai-link" data-target="${basePath}">${text}</a>`
    } else {
      // 新格式，直接使用完整路径
      return `<a class="ai-link" data-target="${path}">${text}</a>`
    }
  })

  return result
}

const handleMessageClick = (e) => {
  const link = e.target.closest('.ai-link')
  if (link) {
    const targetPath = link.dataset.target
    if (targetPath && targetPath.startsWith('/')) {
      router.push(targetPath)
    } else {
      window.location.href = targetPath
    }
    closeChat()
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const startDrag = (e) => {
  if (e.target.closest('.header-actions') || e.target.closest('.chat-footer')) return
  isDragging.value = true
  const rect = dialogRef.value.getBoundingClientRect()
  dragOffset.value = {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top
  }
}

const startResize = (e) => {
  isResizing.value = true
  resizeStart.value = {
    x: e.clientX,
    y: e.clientY,
    width: size.value.width,
    height: size.value.height
  }
  e.stopPropagation()
}

const handleMouseMove = (e) => {
  if (isDragging.value) {
    position.value = {
      x: Math.max(0, Math.min(e.clientX - dragOffset.value.x, window.innerWidth - 400)),
      y: Math.max(0, Math.min(e.clientY - dragOffset.value.y, window.innerHeight - 300))
    }
  }
  if (isResizing.value) {
    const deltaX = e.clientX - resizeStart.value.x
    const deltaY = e.clientY - resizeStart.value.y
    size.value = {
      width: Math.max(300, Math.min(600, resizeStart.value.width + deltaX)),
      height: Math.max(400, Math.min(700, resizeStart.value.height + deltaY))
    }
  }
}

const handleMouseUp = () => {
  isDragging.value = false
  isResizing.value = false
}

const handleClickOutside = (e) => {
  if (isOpen.value && !isFullscreen.value && dialogRef.value && !dialogRef.value.contains(e.target)) {
    const toggleBtn = document.querySelector('.chat-toggle')
    if (toggleBtn && !toggleBtn.contains(e.target)) {
      isOpen.value = false
    }
  }
}

const clearHistory = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await clearChatHistory(sessionId.value)
    messages.value = []
    sessionId.value = ''
    localStorage.removeItem('ai_session_id')
    ElMessage.success('对话历史已清空')
  } catch (error) {
    console.error('Failed to clear history:', error)
    ElMessage.error('清空失败，请重试')
  }
}
</script>

<style scoped>
.ai-chat-wrapper {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
}

.ai-chat-dialog {
  position: fixed;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  pointer-events: auto;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  cursor: move;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-icon {
  font-size: 28px;
  color: #fff;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-status {
  font-size: 12px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  gap: 2px;
  position: relative;
  z-index: 10;
}

.action-btn {
  color: #fff;
  padding: 6px;
  font-size: 16px;
  line-height: 1;
  background: transparent;
  border: none;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.25);
}

.action-btn .el-icon {
  color: #fff;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
  min-height: 0;
  max-height: calc(100vh - 140px);
}

.login-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 12px;
  color: #909399;
}

.welcome-message {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  border-radius: 12px;
  margin-bottom: 16px;
}

.welcome-icon {
  font-size: 32px;
  color: #667eea;
  flex-shrink: 0;
}

.welcome-text {
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
}

.welcome-text ul {
  margin: 8px 0 0 0;
  padding-left: 20px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.message.user {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.message.assistant {
  align-self: flex-start;
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message.user .message-content {
  align-items: flex-end;
}

.message-text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-text {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message.assistant .message-text :deep(strong) {
  color: #667eea;
}

.message-text :deep(.ai-link) {
  display: inline-block;
  margin: 4px 2px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff !important;
  border-radius: 16px;
  text-decoration: none;
  font-size: 13px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.message-text :deep(.ai-link:hover) {
  opacity: 0.85;
}

.message-time {
  font-size: 11px;
  color: #909399;
}

.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
}

.typing .dot {
  width: 6px;
  height: 6px;
  background: #909399;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing .dot:nth-child(1) { animation-delay: -0.32s; }
.typing .dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-footer {
  padding: 12px;
  background: #fff;
  border-top: 1px solid #eee;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-tag {
  cursor: pointer;
  font-size: 12px;
}

.quick-tag:hover {
  opacity: 0.8;
}

.input-area {
  display: flex;
  gap: 8px;
}

.input-area .el-input {
  flex: 1;
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  cursor: se-resize;
  background: linear-gradient(135deg, transparent 50%, #667eea50 50%);
  border-radius: 0 0 16px 0;
}

.chat-toggle {
  position: fixed;
  bottom: 20px;
  right: 20px;
  pointer-events: auto;
}

.toggle-btn {
  width: 56px;
  height: 56px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}
</style>
