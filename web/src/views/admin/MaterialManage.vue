<template>
  <div class="material-manage">
    <div class="header">
      <h2>资料管理</h2>
      <div class="filters">
        <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
            <el-option label="公共课" value="public" />
            <el-option label="专业课" value="major" />
            <el-option label="复试资料" value="interview" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索资料名称" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">发布资料</el-button>
        <el-button type="warning" @click="handleAutoClassify">智能分类</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="materialList" v-loading="loading" style="width: 100%">
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
        <el-table-column prop="name" label="资料名称" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryLabel(row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileFormat" label="格式" width="100" />
        <el-table-column prop="fileSize" label="大小" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="downloadCount" label="下载" width="100" />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled :max="5" />
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row, $index }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :disabled="$index === 0" @click="handleMove($index, -1)">上移</el-button>
            <el-button type="warning" link :disabled="$index === materialList.length - 1" @click="handleMove($index, 1)">下移</el-button>
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

    <!-- 编辑/新增对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入资料名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="公共课" value="public" />
                <el-option label="专业课" value="major" />
                <el-option label="复试资料" value="interview" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :precision="2" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="划线价" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :precision="2" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销量" prop="sales">
              <el-input-number v-model="form.sales" :step="1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资料格式" prop="fileFormat">
              <el-select v-model="form.fileFormat" placeholder="请选择格式" style="width: 100%">
                <el-option label="PDF" value="PDF" />
                <el-option label="视频" value="视频" />
                <el-option label="音频" value="音频" />
                <el-option label="压缩包" value="压缩包" />
                <el-option label="Word" value="Word" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文件大小" prop="fileSize">
              <el-input v-model="form.fileSize" placeholder="如: 15.2MB" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适用年份" prop="applyYear">
              <el-input v-model="form.applyYear" placeholder="如: 2026" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="作者/上传者" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="空格分隔，如: 政治 英语 数学" />
        </el-form-item>
        
        <el-form-item label="规格" prop="specs">
          <el-input v-model="form.specs" placeholder="请输入规格 (如: PDF, 视频课)" />
        </el-form-item>
        
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入资料简介" />
        </el-form-item>
        
        <el-form-item label="预览内容" prop="previewContent">
          <el-input v-model="form.previewContent" type="textarea" :rows="3" placeholder="目录或部分预览内容" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动开始" prop="flashStartTime">
              <el-date-picker
                v-model="form.flashStartTime"
                type="datetime"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="选填"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动结束" prop="flashEndTime">
              <el-date-picker
                v-model="form.flashEndTime"
                type="datetime"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="选填"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
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
        
        <el-form-item label="资料文件" prop="fileUrl">
          <el-upload
            ref="fileUploadRef"
            class="file-uploader"
            action="#"
            drag
            :http-request="uploadFileRequest"
            :show-file-list="true"
            :on-success="handleFileSuccess"
            :on-error="handleFileError"
            :before-upload="beforeFileUpload"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF, Word, Zip, 视频, 音频等格式，文件大小不超过 50MB
              </div>
            </template>
          </el-upload>
          <div v-if="form.fileUrl" class="file-link-preview">
            <el-icon><Document /></el-icon>
            <span>已上传: {{ form.fileUrl }}</span>
            <el-button type="danger" link size="small" @click="clearFileUpload">删除</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Document, UploadFilled } from '@element-plus/icons-vue'
import { getMaterialListAll, saveMaterial, updateMaterial, deleteMaterial, updateMaterialStatus, swapMaterialOrder } from '@/api/material'
import request from '@/utils/request'

const loading = ref(false)
const materialList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterCategory = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('新增资料')
const formRef = ref(null)
const isUploading = ref(false)
const coverUploadProcessed = ref(false)
const form = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  originalPrice: 0,
  sales: 0,
  stock: 0,
  specs: '',
  description: '',
  coverImg: '',
  fileUrl: '',
  flashStartTime: '',
  flashEndTime: '',
  status: 1,
  fileSize: '',
  downloadCount: 0,
  tags: '',
  applyYear: '',
  author: '',
  rating: 0,
  previewContent: '',
  fileFormat: ''
})

const fileUploadRef = ref(null)

const clearFileUpload = () => {
  form.fileUrl = ''
  fileUploadProcessed.value = false
  if (fileUploadRef.value) {
    fileUploadRef.value.clearFiles()
  }
}

const rules = {
  name: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMaterialListAll({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      category: filterCategory.value || undefined
    })
    materialList.value = res.records
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

const handleAutoClassify = async () => {
  try {
    await ElMessageBox.confirm(
      '此操作将对所有资料根据名称和描述进行智能分类，确定要继续吗？',
      '智能分类',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  loading.value = true
  try {
    const res = await getMaterialListAll({ pageNum: 1, pageSize: 1000 })
    const materials = res.records || res
    let updatedCount = 0

    for (const material of materials) {
      const newCategory = analyzeCategory(material.name, material.description, material.tags)
      if (newCategory && newCategory !== material.category) {
        try {
          await updateMaterial({
            id: material.id,
            name: material.name,
            description: material.description,
            price: material.price,
            originalPrice: material.originalPrice,
            stock: material.stock,
            category: newCategory,
            coverImg: material.coverImg,
            fileUrl: material.fileUrl,
            specs: material.specs,
            sales: material.sales || 0,
            fileSize: material.fileSize,
            downloadCount: material.downloadCount || 0,
            tags: material.tags,
            applyYear: material.applyYear,
            author: material.author,
            rating: material.rating || 0,
            previewContent: material.previewContent,
            fileFormat: material.fileFormat,
            flashStartTime: material.flashStartTime,
            flashEndTime: material.flashEndTime,
            status: material.status,
            sortOrder: material.sortOrder
          })
          updatedCount++
        } catch (e) {
          console.error(`更新资料 ${material.id} 分类失败:`, e)
        }
      }
    }

    ElMessage.success(`智能分类完成！共更新了 ${updatedCount} 条资料`)
    fetchData()
  } catch (error) {
    ElMessage.error('智能分类失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const analyzeCategory = (name, description, tags) => {
  const text = `${name || ''} ${description || ''} ${tags || ''}`.toLowerCase()

  const interviewKeywords = ['复试', '面试', '口语', '调剂', '导师', '研究生面试', '英语口语', '口试', '面试技巧']
  for (const kw of interviewKeywords) {
    if (text.includes(kw)) return 'interview'
  }

  const publicKeywords = ['政治', '英语', '数学', '公共课', '考研政治', '考研英语', '考研数学', '大学英语', '线代', '高数', '概率论']
  for (const kw of publicKeywords) {
    if (text.includes(kw)) return 'public'
  }

  const majorKeywords = ['计算机', '数据结构', '408', '操作系统', '网络', '组成原理', '数据库', '算法', '软件工程', '专业课', '编程', '程序设计', '离散数学', '计算机组成']
  for (const kw of majorKeywords) {
    if (text.includes(kw)) return 'major'
  }

  return null
}

const handleStatusChange = async (row) => {
  try {
    await updateMaterialStatus(row.id, row.status)
    ElMessage.success(`已${row.status === 1 ? '上架' : '下架'}`)
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // revert
  }
}

const handleMove = async (index, offset) => {
  const targetIndex = index + offset
  if (targetIndex < 0 || targetIndex >= materialList.value.length) {
    return
  }
  const current = materialList.value[index]
  const target = materialList.value[targetIndex]
  await swapMaterialOrder(current.id, target.id)
  ElMessage.success('排序已更新')
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增资料'
  Object.assign(form, {
    id: null,
    name: '',
    category: '',
    price: 0,
    originalPrice: 0,
    sales: 0,
    stock: 0,
    specs: '',
    description: '',
    coverImg: '',
    flashStartTime: '',
    flashEndTime: '',
    status: 1,
    fileSize: '',
    downloadCount: 0,
    tags: '',
    applyYear: '',
    author: '',
    rating: 0,
    previewContent: '',
    fileFormat: ''
  })
  isUploading.value = false
  coverUploadProcessed.value = false
  fileUploadProcessed.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资料'
  const categoryMap = {
    '公共课': 'public',
    '专业课': 'major',
    '复试资料': 'interview'
  }
  Object.assign(form, {
    ...row,
    category: categoryMap[row.category] || row.category,
    flashStartTime: normalizeDateTime(row.flashStartTime),
    flashEndTime: normalizeDateTime(row.flashEndTime)
  })
  isUploading.value = false
  coverUploadProcessed.value = false
  fileUploadProcessed.value = false
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该资料吗?', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteMaterial(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const uploadRequest = async (options) => {
  if (isUploading.value) {
    return
  }
  isUploading.value = true
  coverUploadProcessed.value = false
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await request.post('/file/upload', formData)
    options.onSuccess(res)
  } catch (error) {
    options.onError(error)
  } finally {
    isUploading.value = false
  }
}

const handleCoverSuccess = (res) => {
  if (coverUploadProcessed.value) {
    return
  }
  coverUploadProcessed.value = true

  if (typeof res === 'string') {
    form.coverImg = res
    ElMessage.success('封面上传成功')
    return
  }
  if (res && typeof res.data === 'string') {
    form.coverImg = res.data
    ElMessage.success('封面上传成功')
    return
  }
  if (res && typeof res.url === 'string') {
    form.coverImg = res.url
    ElMessage.success('封面上传成功')
    return
  }
  if (res && res.code === 200) {
    form.coverImg = res.data
    ElMessage.success('封面上传成功')
    return
  }
  ElMessage.error('封面上传失败')
}

const handleCoverError = () => {
  ElMessage.error('封面上传失败')
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('上传封面只能是图片格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传封面图片大小不能超过 2MB!');
  }
  return isImage && isLt2M;
}

const normalizeCoverImg = (coverImg) => {
  if (typeof coverImg === 'string') {
    return coverImg
  }
  if (coverImg && typeof coverImg.url === 'string') {
    return coverImg.url
  }
  return ''
}

const fileUploadProcessed = ref(false)

const uploadFileRequest = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await request.post('/file/upload', formData)
    options.onSuccess(res)
  } catch (error) {
    options.onError(error)
  }
}

const handleFileSuccess = (res) => {
  if (fileUploadProcessed.value) return
  fileUploadProcessed.value = true
  
  let url = extractUrlFromResponse(res)
  if (url) {
    form.fileUrl = url
  }
  ElMessage.success('资料文件上传成功')
}

const extractUrlFromResponse = (res) => {
  if (!res) return null
  
  if (typeof res === 'string') return res
  
  if (typeof res !== 'object' || Array.isArray(res)) return null
  
  const possibleKeys = ['url', 'data', 'fileUrl', 'filePath', 'path', 'location', 'fileName']
  for (const key of possibleKeys) {
    const value = res[key]
    if (typeof value === 'string' && value && (value.startsWith('http') || value.startsWith('/') || value.includes('.'))) {
      return value
    }
  }
  
  const firstStringValue = Object.values(res).find(v => typeof v === 'string' && v.length > 10)
  if (firstStringValue) return firstStringValue
  
  return null
}

const handleFileError = () => {
  ElMessage.error('资料文件上传失败')
}

const beforeFileUpload = (file) => {
  const allowedTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/zip',
    'application/x-zip-compressed',
    'application/x-rar-compressed',
    'video/mp4',
    'video/avi',
    'video/mpeg',
    'audio/mpeg',
    'audio/wav',
    'audio/ogg'
  ]
  const allowedExtensions = ['pdf', 'doc', 'docx', 'zip', 'rar', 'mp4', 'avi', 'mp3', 'wav', 'ogg']
  
  const extension = file.name.split('.').pop().toLowerCase()
  const isAllowed = allowedTypes.includes(file.type) || allowedExtensions.includes(extension)
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isAllowed) {
    ElMessage.error('不支持的文件格式!')
  }
  if (!isLt50M) {
    ElMessage.error('上传文件大小不能超过 50MB!')
  }
  
  if (isAllowed) {
    form.fileSize = (file.size / 1024 / 1024).toFixed(2) + 'MB'
    form.fileFormat = getFileFormat(extension)
  }
  
  return isAllowed && isLt50M
}

const getCategoryLabel = (val) => {
  const map = {
    'public': '公共课',
    'major': '专业课',
    'interview': '复试资料',
    '公共课': '公共课',
    '专业课': '专业课',
    '复试资料': '复试资料'
  }
  return map[val] || val || '未分类'
}

const getFileFormat = (extension) => {
  const formatMap = {
    'pdf': 'PDF',
    'doc': 'Word',
    'docx': 'Word',
    'zip': '压缩包',
    'rar': '压缩包',
    'mp4': '视频',
    'avi': '视频',
    'mp3': '音频',
    'wav': '音频',
    'ogg': '音频'
  }
  return formatMap[extension] || '其他'
}

const normalizeDateTime = (time) => {
  if (!time) {
    return ''
  }
  return String(time).replace(' ', 'T')
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const payload = {
        ...form,
        coverImg: normalizeCoverImg(form.coverImg),
        flashStartTime: form.flashStartTime || null,
        flashEndTime: form.flashEndTime || null
      }
      if (form.id) {
        await updateMaterial(payload)
        ElMessage.success('修改成功')
      } else {
        await saveMaterial(payload)
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

.file-uploader {
  width: 100%;
}

.file-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 100%;
  padding: 20px;
}

.file-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.file-uploader .el-icon--upload {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 10px;
}

.file-uploader .el-upload__text {
  color: #606266;
  font-size: 14px;
}

.file-uploader .el-upload__text em {
  color: #409eff;
  font-style: normal;
}

.file-link-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
}

.file-link-preview .el-icon {
  font-size: 16px;
  color: #409eff;
}

.material-manage {
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
