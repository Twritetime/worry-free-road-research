<template>
  <div class="home-config-manage">
    <div class="header">
      <h2>首页配置管理</h2>
      <div class="filters">
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 150px; margin-right: 10px">
            <el-option v-for="item in CONFIG_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索标题" style="width: 200px; margin-right: 10px" clearable />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">添加配置</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="filteredList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="类型" width="100">
           <template #default="{ row }">
             <el-tag type="primary">{{ getTypeLabel(row.type) }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="imgUrl" label="图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.imgUrl"
              :src="row.imgUrl"
              :preview-src-list="[row.imgUrl]"
              fit="cover"
              style="width: 60px; height: 40px"
              :preview-teleported="true"
            />
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="链接" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleMoveUp(row)">上移</el-button>
            <el-button type="warning" link @click="handleMoveDown(row)">下移</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="filteredList.length > 10">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="filteredList.length"
          :page-size="10"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="650px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option v-for="item in CONFIG_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-section">
            <el-upload
              class="image-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
            >
              <div v-if="form.imgUrl" class="preview-image-wrapper">
                <img :src="form.imgUrl" class="preview-image" />
                <el-button type="text" class="remove-btn" @click.stop="removeImage">移除</el-button>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <span>点击上传图片</span>
              </div>
            </el-upload>
          </div>
          <div class="url-input-wrapper">
            <el-input v-model="form.imgUrl" placeholder="或直接输入图片URL" />
          </div>
        </el-form-item>
        <el-form-item label="链接地址" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接（如 /materials）" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input type="number" v-model="form.sortOrder" placeholder="数字越小越靠前" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
            inline-prompt
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAllConfigs, createConfig, updateConfig, deleteConfig, updateConfigStatus, swapOrder } from '@/api/homeConfig'
import { useUserStore } from '@/stores/user'

const CONFIG_TYPE_OPTIONS = [
  { value: 'banner', label: '轮播图' },
  { value: 'notice', label: '通知公告' }
]

const userStore = useUserStore()
const uploadUrl = '/api/file/upload'

const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.adminInfo.token || ''}`
}))

const filterType = ref('')
const keyword = ref('')
const loading = ref(false)
const configList = ref([])
const pageNum = ref(1)

const dialogVisible = ref(false)
const dialogTitle = ref('添加配置')
const formRef = ref(null)
const form = ref({
  id: null,
  type: 'banner',
  title: '',
  imgUrl: '',
  linkUrl: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

const filteredList = computed(() => {
  let list = configList.value
  
  if (filterType.value) {
    list = list.filter(item => item.type === filterType.value)
  }
  
  if (keyword.value) {
    list = list.filter(item => item.title.includes(keyword.value))
  }
  
  return list
})

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllConfigs(filterType.value)
    configList.value = res || []
  } catch (error) {
    console.error(error)
    ElMessage.error('获取配置失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
}

const handlePageChange = (page) => {
  pageNum.value = page
}

const handleAdd = () => {
  dialogTitle.value = '添加配置'
  form.value = {
    id: null,
    type: 'banner',
    title: '',
    imgUrl: '',
    linkUrl: '',
    sortOrder: 0,
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑配置'
  form.value = { 
    ...row,
    sortOrder: row.sortOrder || 0,
    status: row.status || 1
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = {
      ...form.value
    }
    
    if (form.value.id) {
      await updateConfig(submitData)
      ElMessage.success('更新成功')
    } else {
      await createConfig(submitData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
    ElMessage.error(form.value.id ? '更新失败' : '添加失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateConfigStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch (error) {
    console.error(error)
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('操作失败')
  }
}

const handleMoveUp = async (row) => {
  const index = configList.value.findIndex(item => item.id === row.id)
  if (index <= 0) {
    ElMessage.warning('已经是第一条')
    return
  }
  await swapItems(index, index - 1)
}

const handleMoveDown = async (row) => {
  const index = configList.value.findIndex(item => item.id === row.id)
  if (index >= configList.value.length - 1) {
    ElMessage.warning('已经是最后一条')
    return
  }
  await swapItems(index, index + 1)
}

const swapItems = async (index1, index2) => {
  try {
    const item1 = configList.value[index1]
    const item2 = configList.value[index2]
    
    await swapOrder(item1.id, item2.id)
    
    const temp = configList.value[index1]
    configList.value[index1] = configList.value[index2]
    configList.value[index2] = temp
    
    ElMessage.success('排序已更新')
  } catch (error) {
    console.error(error)
    ElMessage.error('排序失败')
  }
}

const handleDelete = async (row) => {
  try {
    await deleteConfig(row.id)
    configList.value = configList.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('删除失败')
  }
}

const getTypeLabel = (type) => {
  const map = {
    'banner': '轮播图',
    'notice': '通知公告'
  }
  return map[type] || type
}

const handleUploadSuccess = (response) => {
  if (response && response.data) {
    form.value.imgUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const removeImage = () => {
  form.value.imgUrl = ''
}
</script>

<style scoped>
.home-config-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.filters {
  display: flex;
  align-items: center;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.upload-section {
  margin-bottom: 12px;
}

.image-uploader {
  width: 100%;
}

.preview-image-wrapper {
  position: relative;
  display: inline-block;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
}

.preview-image {
  width: 200px;
  height: 150px;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  color: #fff;
  background: rgba(0, 0, 0, 0.5);
  padding: 2px 8px;
  font-size: 12px;
}

.upload-placeholder {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #409eff;
}

.upload-icon {
  font-size: 32px;
  color: #8c939d;
  display: block;
  margin-bottom: 8px;
}

.url-input-wrapper {
  margin-top: 10px;
}

.dialog-footer {
  text-align: right;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
