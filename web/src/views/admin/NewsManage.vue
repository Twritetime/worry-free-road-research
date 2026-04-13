<template>
  <div class="news-manage">
    <div class="header">
      <h2>资讯管理</h2>
      <div class="filters">
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 150px; margin-right: 10px" @change="handleSearch">
            <el-option v-for="item in NEWS_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索资讯标题" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">发布资讯</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="newsList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coverImg" label="封面" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.coverImg"
              :preview-src-list="[row.coverImg]"
              fit="cover"
              style="width: 50px; height: 50px"
              :preview-teleported="true"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
           <template #default="{ row }">
             <el-tag>{{ getTypeLabel(row.type) }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column prop="viewCount" label="阅读量" width="100" />
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
            <el-button type="warning" link :disabled="$index === newsList.length - 1" @click="handleMove($index, 1)">下移</el-button>
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
            <el-option v-for="item in NEWS_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面" prop="coverImg">
          <el-upload
            class="avatar-uploader"
            action="#"
            drag
            :http-request="uploadRequest"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :on-error="handleCoverError"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="form.coverImg" :src="form.coverImg" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <Editor
            v-model="form.content"
            :init="editorInit"
            @onEditorChange="handleEditorChange"
          />
        </el-form-item>
        <el-form-item label="上传时间">
          <el-date-picker
            v-model="form.createTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="选填"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="编辑时间">
          <el-date-picker
            v-model="form.updateTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="选填"
            style="width: 100%"
          />
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
import { Plus } from '@element-plus/icons-vue'
import Editor from '@tinymce/tinymce-vue'
import { getNewsListAll, createNews, updateNews, deleteNews, updateNewsStatus, swapNewsOrder } from '@/api/news'
import AdminCommentDialog from '@/components/AdminCommentDialog.vue'
import request from '@/utils/request'
import 'tinymce/tinymce'
import 'tinymce/icons/default'
import 'tinymce/models/dom'
import 'tinymce/themes/silver'
import 'tinymce/plugins/advlist'
import 'tinymce/plugins/autolink'
import 'tinymce/plugins/lists'
import 'tinymce/plugins/link'
import 'tinymce/plugins/image'
import 'tinymce/plugins/charmap'
import 'tinymce/plugins/preview'
import 'tinymce/plugins/anchor'
import 'tinymce/plugins/searchreplace'
import 'tinymce/plugins/visualblocks'
import 'tinymce/plugins/code'
import 'tinymce/plugins/fullscreen'
import 'tinymce/plugins/insertdatetime'
import 'tinymce/plugins/media'
import 'tinymce/plugins/table'
import 'tinymce/plugins/help'
import 'tinymce/plugins/wordcount'
import 'tinymce/skins/ui/oxide/skin.min.css'
import 'tinymce/skins/content/default/content.min.css'

const loading = ref(false)
const newsList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref('')
const NEWS_TYPE_OPTIONS = [
  { label: '报考指南', value: '报考' },
  { label: '政策解读', value: '政策' },
  { label: '备考经验', value: '经验' },
  { label: '复试调剂', value: '复试调剂' }
]
const NEWS_TYPE_LABEL_MAP = NEWS_TYPE_OPTIONS.reduce((map, item) => {
  map[item.value] = item.label
  return map
}, {})

const commentDialogVisible = ref(false)
const currentNewsId = ref(null)

const dialogVisible = ref(false)
const dialogTitle = ref('发布资讯')
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  type: '',
  coverImg: '',
  content: '',
  createTime: '',
  updateTime: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'change' }]
}

const uploadEditorImage = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await request.post('/file/upload', formData)
  if (typeof res === 'string') {
    return res
  }
  if (res && typeof res.url === 'string') {
    return res.url
  }
  if (res && typeof res.data === 'string') {
    return res.data
  }
  throw new Error('图片上传失败')
}

const editorInit = {
  height: 420,
  menubar: 'file edit insert format table tools help',
  language: 'zh_CN',
  plugins: 'advlist autolink lists link image charmap preview anchor searchreplace visualblocks code fullscreen insertdatetime media table help wordcount',
  toolbar: 'undo redo | blocks | bold italic underline strikethrough forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image media table | removeformat code fullscreen',
  toolbar_mode: 'sliding',
  branding: false,
  statusbar: true,
  content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px } img { max-width: 100%; height: auto; }',
  automatic_uploads: true,
  paste_data_images: true,
  images_file_types: 'jpg,jpeg,png,gif,webp,bmp',
  images_upload_handler: (blobInfo) => {
    return uploadEditorImage(blobInfo.blob())
  }
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

const getTypeLabel = (type) => {
  return NEWS_TYPE_LABEL_MAP[type] || type
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

const handleMove = async (index, offset) => {
  const targetIndex = index + offset
  if (targetIndex < 0 || targetIndex >= newsList.value.length) {
    return
  }
  const current = newsList.value[index]
  const target = newsList.value[targetIndex]
  await swapNewsOrder(current.id, target.id)
  ElMessage.success('排序已更新')
  fetchData()
}

const handleComments = (row) => {
    currentNewsId.value = row.id
    commentDialogVisible.value = true
}

const handleAdd = () => {
  dialogTitle.value = '发布资讯'
  Object.assign(form, { id: null, title: '', type: NEWS_TYPE_OPTIONS[0].value, coverImg: '', content: '', createTime: '', updateTime: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资讯'
  Object.assign(form, {
    ...row,
    createTime: normalizeDateTime(row.createTime),
    updateTime: normalizeDateTime(row.updateTime)
  })
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

const uploadRequest = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await request.post('/file/upload', formData)
    options.onSuccess(res)
  } catch (error) {
    options.onError(error)
  }
}

const handleCoverSuccess = (res) => {
  if (typeof res === 'string') {
    form.coverImg = res
    return
  }
  if (res && typeof res.data === 'string') {
    form.coverImg = res.data
    return
  }
  if (res && typeof res.url === 'string') {
    form.coverImg = res.url
    return
  }
  ElMessage.error('封面上传失败')
}

const handleCoverError = () => {
  ElMessage.error('封面上传失败')
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传封面只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传封面图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

const normalizeCoverImg = (coverImg) => {
  if (typeof coverImg === 'string') {
    return coverImg
  }
  if (coverImg && typeof coverImg.url === 'string') {
    return coverImg.url
  }
  if (coverImg && typeof coverImg.data === 'string') {
    return coverImg.data
  }
  return ''
}

const normalizeDateTime = (time) => {
  if (!time) {
    return ''
  }
  return String(time).replace(' ', 'T')
}

const handleEditorChange = () => {
  formRef.value?.validateField('content')
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const payload = {
        ...form,
        coverImg: normalizeCoverImg(form.coverImg),
        createTime: form.createTime || null,
        updateTime: form.updateTime || null
      }
      if (form.id) {
        await updateNews(payload)
        ElMessage.success('修改成功')
      } else {
        await createNews(payload)
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
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.news-manage :deep(.tox-tinymce) {
  border-radius: 8px;
}
</style>
