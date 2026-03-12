<template>
  <div class="news-manage">
    <div class="header">
      <h2>资讯管理</h2>
      <div class="filters">
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 150px; margin-right: 10px" @change="handleSearch">
            <el-option label="考研动态" value="考研动态" />
            <el-option label="招生简章" value="招生简章" />
            <el-option label="考试大纲" value="考试大纲" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索资讯标题" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">发布资讯</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="newsList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
           <template #default="{ row }">
             <el-tag>{{ row.type }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="views" label="阅读量" width="100" />
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="考研动态" value="考研动态" />
            <el-option label="招生简章" value="招生简章" />
            <el-option label="考试大纲" value="考试大纲" />
          </el-select>
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
        :target-id="currentNewsId"
        :target-type="3"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNewsListAll, createNews, updateNews, deleteNews, updateNewsStatus } from '@/api/news'
import AdminCommentDialog from '@/components/AdminCommentDialog.vue'

const loading = ref(false)
const newsList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref('')

const commentDialogVisible = ref(false)
const currentNewsId = ref(null)

const dialogVisible = ref(false)
const dialogTitle = ref('发布资讯')
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  type: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getNewsListAll({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      type: filterType.value || undefined
    })
    newsList.value = res.records
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
    await updateNewsStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleComments = (row) => {
    currentNewsId.value = row.id
    commentDialogVisible.value = true
}

const handleAdd = () => {
  dialogTitle.value = '发布资讯'
  Object.assign(form, { id: null, title: '', type: '', content: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资讯'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该资讯吗?', '提示', { type: 'warning' })
    .then(async () => {
      await deleteNews(row.id)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateNews(form)
        ElMessage.success('修改成功')
      } else {
        await createNews(form)
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
.news-manage {
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
</style>
