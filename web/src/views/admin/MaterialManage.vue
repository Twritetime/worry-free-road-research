<template>
  <div class="material-manage">
    <div class="header">
      <h2>资料管理</h2>
      <div class="filters">
        <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
            <el-option label="公共课" value="公共课" />
            <el-option label="专业课" value="专业课" />
            <el-option label="复试资料" value="复试资料" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索资料名称" style="width: 200px; margin-right: 10px" clearable @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">发布资料</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="materialList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coverImg" label="封面" width="100">
          <template #default="{ row }">
            <el-image :src="row.coverImg" style="width: 50px; height: 50px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="资料名称" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资料名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="公共课" value="公共课" />
            <el-option label="专业课" value="专业课" />
            <el-option label="复试资料" value="复试资料" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="销量" prop="sales">
          <el-input-number v-model="form.sales" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="规格" prop="specs">
          <el-input v-model="form.specs" placeholder="请输入规格 (如: PDF, 视频课)" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入资料简介" />
        </el-form-item>
        <el-form-item label="封面" prop="coverImg">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/file/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="form.coverImg" :src="form.coverImg" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
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
import { Plus } from '@element-plus/icons-vue'
import { getMaterialListAll, saveMaterial, updateMaterial, deleteMaterial, updateMaterialStatus } from '@/api/material'

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
const form = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  sales: 0,
  stock: 0,
  specs: '',
  description: '',
  coverImg: '',
  status: 1
})

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

const handleStatusChange = async (row) => {
  try {
    await updateMaterialStatus(row.id, row.status)
    ElMessage.success(`已${row.status === 1 ? '上架' : '下架'}`)
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // revert
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增资料'
  Object.assign(form, { id: null, name: '', category: '', price: 0, sales: 0, stock: 0, specs: '', description: '', coverImg: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资料'
  Object.assign(form, { ...row })
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

const handleCoverSuccess = (res) => {
  form.coverImg = res
}

const beforeCoverUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateMaterial(form)
        ElMessage.success('修改成功')
      } else {
        await saveMaterial(form)
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
