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
        <el-table-column prop="university" label="院校" width="150" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="title" label="指南标题" show-overflow-tooltip />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="院校" prop="university">
          <el-input v-model="form.university" placeholder="请输入院校名称" />
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

    <el-dialog title="爬取数据" v-model="crawlDialogVisible" width="500px">
      <el-form :model="crawlForm" label-width="80px">
        <el-form-item label="目标URL">
               <el-input v-model="crawlForm.url" placeholder="请输入列表页URL" />
               <div class="form-tip">推荐: http://www.chinakaoyan.com/zhaosheng/</div>
            </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="crawlForm.category" style="width: 100%" @change="handleCategoryChange">
              <el-option label="招生简章" value="zhaoshengjianzhang" />
              <el-option label="专业目录" value="zhuanyemulu" />
              <el-option label="考试大纲" value="kaoshidagang" />
              <el-option label="复试细则" value="fushixize" />
            </el-select>
          </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="crawlDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCrawl" :loading="crawling">开始爬取</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGuideListAll, createGuide, updateGuide, deleteGuide, updateGuideStatus, crawlGuides } from '@/api/guide'
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
const crawlForm = reactive({
    url: 'http://www.chinakaoyan.com/zhaosheng/',
    category: 'zhaoshengjianzhang'
  })
  
  const handleCategoryChange = (val) => {
        const urlMap = {
            'zhaoshengjianzhang': 'http://www.chinakaoyan.com/zhaosheng/',
            'zhuanyemulu': 'https://www.chinakaoyan.com/info/list/ClassID/52.shtml',
            'kaoshidagang': 'https://www.chinakaoyan.com/info/list/ClassID/97.shtml',
            'fushixize': 'https://www.chinakaoyan.com/info/list/ClassID/23.shtml'
        }
        if (urlMap[val]) {
            crawlForm.url = urlMap[val]
        }
    }

const dialogVisible = ref(false)
const dialogTitle = ref('发布指南')
const formRef = ref(null)
const form = reactive({
  id: null,
  university: '',
  major: '',
  title: '',
  content: ''
})

const rules = {
  university: [{ required: true, message: '请输入院校', trigger: 'blur' }],
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

const handleComments = (row) => {
    currentGuideId.value = row.id
    commentDialogVisible.value = true
}

const handleCrawl = () => {
    crawlDialogVisible.value = true
}

const submitCrawl = async () => {
    if (!crawlForm.url) {
        ElMessage.warning('请输入URL')
        return
    }
    crawling.value = true
    try {
        const res = await crawlGuides(crawlForm.url, crawlForm.category)
        // 假设res返回的是消息或者成功状态，后端返回 Result.success("成功爬取 X 条")
        // 如果后端返回的是 Result 对象，request拦截器可能已经解包了 data，或者直接返回
        // 假设拦截器返回的是 response.data
        // 查看 request.js 确认
        ElMessage.success('爬取任务完成')
        crawlDialogVisible.value = false
        fetchData()
    } catch (error) {
        console.error(error)
        // ElMessage.error('爬取失败') // request拦截器可能已处理
    } finally {
        crawling.value = false
    }
}

const handleAdd = () => {
  dialogTitle.value = '发布指南'
  Object.assign(form, { id: null, university: '', major: '', title: '', content: '' })
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
</style>
