<template>
  <div class="guide-manage">
    <div class="header">
      <h2>指南管理</h2>
      <div class="filters">
        <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
            <el-option label="招生简章" value="zhaoshengjianzhang" />
            <el-option label="专业目录" value="zhuanyemulu" />
            <el-option label="考试大纲" value="kaoshidagang" />
            <el-option label="复试细则" value="fushixize" />
        </el-select>
        <el-input v-model="filterInstitution" placeholder="按院校筛选" style="width: 150px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-input v-model="filterMajor" placeholder="按专业筛选" style="width: 150px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-input v-model="keyword" placeholder="搜索指南标题" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">发布指南</el-button>
        <el-button type="warning" @click="handleCrawl">一键爬取</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="guideList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="institution" label="院校" width="150" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="title" label="指南标题" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="上架"
              inactive-text="下架"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row, $index }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :disabled="$index === 0" @click="handleMove($index, -1)">上移</el-button>
            <el-button type="warning" link :disabled="$index === guideList.length - 1" @click="handleMove($index, 1)">下移</el-button>
            <el-button type="success" link @click="handleComments(row)">评论管理</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="院校" prop="institution">
          <el-input v-model="form.institution" placeholder="请输入院校名称" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入指南标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <AdminCommentDialog
        v-model="commentDialogVisible"
        :target-id="currentGuideId"
        :target-type="2"
    />

    <el-dialog title="爬取数据" v-model="crawlDialogVisible" width="600px">
      <el-tabs v-model="activeCrawlTab" class="crawl-tabs">
        <el-tab-pane label="手动爬取" name="manual">
          <el-alert 
            title="爬取说明" 
            type="info" 
            :closable="false" 
            style="margin-bottom: 20px"
          >
            <p>1. 仅支持研招网及高校官网（chsi.cn/chsi.com.cn/edu.cn/gov.cn）</p>
            <p>2. 系统会自动过滤当年和次年的最新数据</p>
            <p>3. 重复的标题会自动跳过</p>
            <p>4. 爬取过程可能需要几分钟，请耐心等待</p>
          </el-alert>
          
          <el-form :model="crawlForm" label-width="100px">
            <el-form-item label="目标URL">
              <el-input v-model="crawlForm.url" placeholder="请输入列表页URL" />
              <div class="form-tip">示例: https://yz.chsi.com.cn/kyzx/</div>
            </el-form-item>
            
            <el-form-item label="分类">
              <el-select v-model="crawlForm.category" style="width: 100%" @change="handleCategoryChange">
                <el-option label="招生简章" value="zhaoshengjianzhang" />
                <el-option label="专业目录" value="zhuanyemulu" />
                <el-option label="考试大纲" value="kaoshidagang" />
                <el-option label="复试细则" value="fushixize" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="推荐URL">
              <el-select v-model="quickUrl" placeholder="快速选择" @change="handleQuickUrlSelect" style="width: 100%">
                <el-option label="研招网 - 考研资讯" value="https://yz.chsi.com.cn/kyzx/" />
                <el-option label="研招网 - 招生简章" value="https://yz.chsi.com.cn/kyzx/zcdh/" />
                <el-option label="研招网 - 专业目录" value="https://yz.chsi.com.cn/zsml/" />
              </el-select>
            </el-form-item>
          </el-form>
          
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="crawlDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitCrawl" :loading="crawling">
                {{ crawling ? '爬取中...' : '开始爬取' }}
              </el-button>
            </span>
          </template>
        </el-tab-pane>
        
        <el-tab-pane label="定时任务" name="schedule">
          <el-alert 
            title="定时任务说明" 
            type="success" 
            :closable="false" 
            style="margin-bottom: 20px"
          >
            <p>系统已配置自动定时爬取任务，无需人工干预</p>
            <p>您也可以手动触发定时任务立即执行</p>
          </el-alert>
          
          <el-table :data="scheduleTasks" style="width: 100%" v-loading="scheduleLoading">
            <el-table-column prop="name" label="任务名称" width="120" />
            <el-table-column prop="description" label="执行时间" width="120" />
            <el-table-column prop="url" label="目标URL" show-overflow-tooltip />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="handleRunSchedule(row.key)"
                  :loading="runningTask === row.key"
                >
                  立即执行
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGuideListAll, createGuide, updateGuide, deleteGuide, updateGuideStatus, crawlGuides, swapGuideOrder, getScheduleConfig, runScheduleTask } from '@/api/guide'
import AdminCommentDialog from '@/components/AdminCommentDialog.vue'

const loading = ref(false)
const guideList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterCategory = ref('')
const filterInstitution = ref('')
const filterMajor = ref('')

const commentDialogVisible = ref(false)
const currentGuideId = ref(null)

const crawlDialogVisible = ref(false)
const crawling = ref(false)
const quickUrl = ref('')
const activeCrawlTab = ref('manual')
const scheduleTasks = ref([])
const scheduleLoading = ref(false)
const runningTask = ref('')
const crawlForm = reactive({
    url: 'https://yz.chsi.com.cn/kyzx/',
    category: 'zhaoshengjianzhang'
  })
  
  const handleCategoryChange = (val) => {
        const urlMap = {
            'zhaoshengjianzhang': 'https://yz.chsi.com.cn/kyzx/',
            'zhuanyemulu': 'https://yz.chsi.com.cn/zsml/',
            'kaoshidagang': 'https://yz.chsi.com.cn/kyzx/',
            'fushixize': 'https://yz.chsi.com.cn/kyzx/'
        }
        if (urlMap[val]) {
            crawlForm.url = urlMap[val]
        }
    }
    
    const handleQuickUrlSelect = (val) => {
        if (val) {
            crawlForm.url = val
        }
    }

const dialogVisible = ref(false)
const dialogTitle = ref('发布指南')
const formRef = ref(null)
const form = reactive({
  id: null,
  institution: '',
  major: '',
  title: '',
  content: ''
})

const rules = {
  institution: [{ required: true, message: '请输入院校', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getGuideListAll({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      category: filterCategory.value || undefined,
      institution: filterInstitution.value || undefined,
      major: filterMajor.value || undefined
    })
    guideList.value = res.records
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

const handleStatusChange = async (row) => {
  try {
    await updateGuideStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleMove = async (index, offset) => {
  const targetIndex = index + offset
  if (targetIndex < 0 || targetIndex >= guideList.value.length) {
    return
  }
  const current = guideList.value[index]
  const target = guideList.value[targetIndex]
  await swapGuideOrder(current.id, target.id)
  ElMessage.success('排序已更新')
  fetchData()
}

const handleComments = (row) => {
    currentGuideId.value = row.id
    commentDialogVisible.value = true
}

const handleCrawl = () => {
    crawlDialogVisible.value = true
    activeCrawlTab.value = 'manual'
    fetchScheduleConfig()
}

const fetchScheduleConfig = async () => {
    scheduleLoading.value = true
    try {
        const res = await getScheduleConfig()
        scheduleTasks.value = Object.entries(res).map(([key, value]) => ({
            key,
            ...value
        }))
    } catch (error) {
        console.error('获取定时任务配置失败:', error)
    } finally {
        scheduleLoading.value = false
    }
}

const handleRunSchedule = async (category) => {
    runningTask.value = category
    try {
        const res = await runScheduleTask(category)
        const successMessage = typeof res === 'string' ? res : (res?.message || '任务执行成功')
        ElMessage.success(successMessage)
        fetchData()
    } catch (error) {
        ElMessage.error('任务执行失败: ' + (error.message || '未知错误'))
    } finally {
        runningTask.value = ''
    }
}

const submitCrawl = async () => {
    if (!crawlForm.url) {
        ElMessage.warning('请输入URL')
        return
    }
    
    if (!crawlForm.url.startsWith('http://') && !crawlForm.url.startsWith('https://')) {
        ElMessage.warning('URL必须以http://或https://开头')
        return
    }
    
    crawling.value = true
    try {
        const res = await crawlGuides(crawlForm.url, crawlForm.category)
        const successMessage = typeof res === 'string' ? res : (res?.message || '爬取任务完成')
        ElMessage.success(successMessage)
        crawlDialogVisible.value = false
        quickUrl.value = ''
        fetchData()
    } catch (error) {
        ElMessage.error('爬取失败: ' + (error.message || '未知错误'))
        console.error(error)
    } finally {
        crawling.value = false
    }
}

const handleAdd = () => {
  dialogTitle.value = '发布指南'
  Object.assign(form, { id: null, institution: '', major: '', title: '', content: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑指南'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该指南吗?', '提示', { type: 'warning' })
    .then(async () => {
      await deleteGuide(row.id)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateGuide(form)
        ElMessage.success('修改成功')
      } else {
        await createGuide(form)
        ElMessage.success('发布成功')
      }
      dialogVisible.value = false
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.guide-manage {
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
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.crawl-tabs {
  margin-top: 10px;
}
.crawl-tabs :deep(.el-tabs__content) {
  padding-top: 20px;
}
</style>
