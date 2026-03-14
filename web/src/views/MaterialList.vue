<template>
  <div class="page-container">
    <!-- Header / Banner -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">资料商城</h1>
        <p class="page-subtitle">精选优质考研资料，助你一战成硕</p>
        
        <!-- Search Bar -->
        <div class="search-wrapper">
           <el-input
            v-model="keyword"
            placeholder="搜索资料名称、科目..."
            class="search-input"
            size="large"
            @keyup.enter="fetchMaterials"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="fetchMaterials">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
      <div class="header-bg"></div>
    </div>

    <div class="main-content">
       <!-- Filter Tabs -->
       <div class="filter-bar">
          <div class="filter-tabs">
            <div 
                v-for="tab in tabs" 
                :key="tab.name" 
                class="filter-tab" 
                :class="{ active: activeCategory === tab.name }"
                @click="handleCategoryChange(tab.name)"
            >
                {{ tab.label }}
            </div>
          </div>
           <div class="flex-spacer"></div>
           <el-button v-if="isAdmin" type="primary" :icon="Plus" @click="openDialog()">发布资料</el-button>
       </div>

       <!-- Grid -->
       <div class="material-grid" v-loading="loading">
          <div v-for="item in materialList" :key="item.id" class="material-card" @click="viewDetail(item.id)">
             <div class="card-image">
                <img :src="item.coverImg || 'https://placehold.co/300x400?text=Material'" />
                <div class="card-overlay">
                   <el-button type="primary" round>查看详情</el-button>
                </div>
             </div>
             <div class="card-content">
                <div class="card-tags">
                   <el-tag size="small" :type="getCategoryType(item.category)" effect="light">{{ getCategoryLabel(item.category) }}</el-tag>
                   <span class="stock-badge" v-if="item.stock < 10 && item.stock > 0">仅剩 {{ item.stock }} 份</span>
                   <span class="stock-badge out" v-if="item.stock <= 0">已售罄</span>
                </div>
                <h3 class="card-title" :title="item.name">{{ item.name }}</h3>
                <div class="card-footer">
                   <span class="price">¥ {{ item.price }}</span>
                   <span class="specs">{{ item.specs || 'PDF' }}</span>
                </div>
             </div>
              <div v-if="isAdmin" class="admin-actions" @click.stop>
                  <el-button circle size="small" :icon="Edit" @click="openDialog(item)"></el-button>
                  <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(item.id)"></el-button>
              </div>
          </div>
       </div>
       <el-empty v-if="!loading && materialList.length === 0" description="暂无相关资料" />

       <!-- Pagination -->
       <div class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            @current-change="handlePageChange"
          />
       </div>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑资料' : '发布资料'"
      width="600px"
      class="custom-dialog"
    >
      <el-form :model="materialForm" label-width="80px" label-position="top" :rules="rules" ref="formRef">
        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="资料名称" prop="name">
                  <el-input v-model="materialForm.name" placeholder="请输入资料名称"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="分类" prop="category">
                  <el-select v-model="materialForm.category" placeholder="请选择分类" style="width: 100%">
                    <el-option label="公共课" value="public"></el-option>
                    <el-option label="专业课" value="major"></el-option>
                    <el-option label="复试资料" value="interview"></el-option>
                  </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-row :gutter="20">
            <el-col :span="8">
                <el-form-item label="价格" prop="price">
                  <el-input-number v-model="materialForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%"></el-input-number>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="库存" prop="stock">
                  <el-input-number v-model="materialForm.stock" :min="0" :precision="0" style="width: 100%"></el-input-number>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="规格" prop="specs">
                    <el-input v-model="materialForm.specs" placeholder="例如: PDF"></el-input>
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="materialForm.description" rows="4" placeholder="请输入描述"></el-input>
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverImg">
          <div class="upload-container">
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
              >
                <img v-if="materialForm.coverImg" :src="materialForm.coverImg" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">建议尺寸: 600x800px</div>
          </div>
        </el-form-item>
        
        <el-form-item label="资料文件" prop="fileUrl">
            <el-upload
                class="file-uploader"
                :action="uploadUrl"
                :limit="1"
                :on-success="handleFileSuccess"
                :file-list="fileList"
            >
                <el-button type="primary" plain :icon="Upload">点击上传资料</el-button>
                <template #tip>
                    <div class="el-upload__tip">
                        支持 PDF, Doc, Zip 等格式
                    </div>
                </template>
            </el-upload>
            <div v-if="materialForm.fileUrl" class="file-link-preview">
                <el-icon><Document /></el-icon>
                <span>已上传文件</span>
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
import { ref, onMounted, reactive, watch } from 'vue'
import { getMaterialList, saveMaterial, updateMaterial, deleteMaterial } from '@/api/material'
import { useRouter, useRoute } from 'vue-router'
import { Plus, Edit, Delete, Search, Upload, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { isAdmin } = storeToRefs(userStore)

const materialList = ref([])
const loading = ref(false)
const keyword = ref('')
const activeCategory = ref('all')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const uploadUrl = 'http://localhost:8080/file/upload'

const tabs = [
    { name: 'all', label: '全部资料' },
    { name: 'public', label: '公共课' },
    { name: 'major', label: '专业课' },
    { name: 'interview', label: '复试资料' }
]

// Dialog
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const materialForm = reactive({
    id: null,
    name: '',
    category: '',
    price: 0,
    stock: 0,
    specs: '',
    description: '',
    coverImg: '',
    fileUrl: '',
    status: 1
})
const fileList = ref([])

const rules = {
    name: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
    category: [{ required: true, message: '请选择分类', trigger: 'change' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

onMounted(() => {
    // Check URL params for category
    if (route.query.category) {
        activeCategory.value = route.query.category
    }
    fetchMaterials()
})

watch(() => route.query.category, (newVal) => {
    if (newVal) {
        activeCategory.value = newVal
        fetchMaterials()
    }
})

const handleCategoryChange = (name) => {
    activeCategory.value = name
    pageNum.value = 1
    fetchMaterials()
}

const getCategoryLabel = (val) => {
    const map = { 'public': '公共课', 'major': '专业课', 'interview': '复试' }
    return map[val] || val
}

const getCategoryType = (val) => {
    const map = { 'public': 'primary', 'major': 'success', 'interview': 'warning' }
    return map[val] || 'info'
}

const fetchMaterials = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value,
            category: activeCategory.value === 'all' ? undefined : activeCategory.value
        }
        const res = await getMaterialList(params)
        materialList.value = res.records || []
        total.value = res.total
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchMaterials()
}

const viewDetail = (id) => {
    router.push(`/materials/${id}`)
}

const openDialog = (item = null) => {
    if (item) {
        isEdit.value = true
        Object.assign(materialForm, item)
        fileList.value = item.fileUrl ? [{ name: '资料文件', url: item.fileUrl }] : []
    } else {
        isEdit.value = false
        Object.assign(materialForm, {
            id: null,
            name: '',
            category: 'public',
            price: 0,
            stock: 999,
            specs: 'PDF',
            description: '',
            coverImg: '',
            fileUrl: '',
            status: 1
        })
        fileList.value = []
    }
    dialogVisible.value = true
}

const handleCoverSuccess = (res) => {
    materialForm.coverImg = res.data
}

const handleFileSuccess = (res) => {
    materialForm.fileUrl = res.data
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (isEdit.value) {
                    await updateMaterial(materialForm)
                    ElMessage.success('更新成功')
                } else {
                    await saveMaterial(materialForm)
                    ElMessage.success('发布成功')
                }
                dialogVisible.value = false
                fetchMaterials()
            } catch (error) {
                ElMessage.error('操作失败')
            }
        }
    })
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确认删除该资料吗?', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await deleteMaterial(id)
            ElMessage.success('删除成功')
            fetchMaterials()
        } catch (error) {
            ElMessage.error('删除失败')
        }
    })
}
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    background:
      radial-gradient(circle at 0% -15%, rgba(99, 102, 241, 0.16), transparent 45%),
      radial-gradient(circle at 100% 12%, rgba(59, 130, 246, 0.14), transparent 38%),
      linear-gradient(180deg, #f9fbff 0%, #f4f8ff 44%, #f8fbff 100%);
}

.page-header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    padding: 60px 0 40px;
    text-align: center;
    position: relative;
    overflow: hidden;
    margin-bottom: 30px;
}

.header-content {
    position: relative;
    z-index: 2;
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
}

.page-title {
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 10px;
    color: var(--text-primary);
}

.page-subtitle {
    font-size: 16px;
    color: var(--text-secondary);
    margin-bottom: 30px;
}

.search-wrapper {
    max-width: 600px;
    margin: 0 auto;
    box-shadow: 0 10px 30px rgba(0,0,0,0.05);
    border-radius: 4px;
}

.header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: radial-gradient(circle at 20% 150%, rgba(99,102,241,0.12) 0%, transparent 52%),
                      radial-gradient(circle at 80% -50%, rgba(59,130,246,0.12) 0%, transparent 52%);
    z-index: 1;
}

.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;
}

.filter-bar {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    flex-wrap: wrap;
    gap: 15px;
}

.filter-tabs {
    display: flex;
    background: white;
    padding: 5px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.03);
}

.filter-tab {
    padding: 8px 20px;
    cursor: pointer;
    border-radius: 6px;
    color: var(--text-secondary);
    font-weight: 500;
    transition: all 0.3s;
}

.filter-tab.active {
    background: var(--primary-color);
    color: white;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.flex-spacer {
    flex: 1;
}

.material-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 25px;
    margin-bottom: 40px;
}

.material-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
    border: 1px solid rgba(0,0,0,0.05);
    cursor: pointer;
    display: flex;
    flex-direction: column;
}

.material-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0,0,0,0.08);
    border-color: transparent;
}

.card-image {
    height: 320px;
    position: relative;
    overflow: hidden;
    background: #f8f9fa;
}

.card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.material-card:hover .card-image img {
    transform: scale(1.05);
}

.card-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
}

.material-card:hover .card-overlay {
    opacity: 1;
}

.card-content {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.card-tags {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.stock-badge {
    font-size: 12px;
    color: #ef4444;
    font-weight: 500;
}

.stock-badge.out {
    color: #9ca3af;
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 12px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    flex: 1;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
}

.price {
    font-size: 18px;
    font-weight: 700;
    color: #ef4444;
}

.specs {
    font-size: 12px;
    color: var(--text-secondary);
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 4px;
}

.admin-actions {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 5;
    opacity: 0;
    transition: opacity 0.3s;
}

.material-card:hover .admin-actions {
    opacity: 1;
}

.pagination-wrapper {
    display: flex;
    justify-content: center;
}

/* Dialog Styling */
.upload-container {
    display: flex;
    align-items: center;
    gap: 15px;
}

.avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 100px;
    height: 133px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.avatar-uploader:hover {
    border-color: var(--primary-color);
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
}

.avatar {
    width: 100px;
    height: 133px;
    display: block;
    object-fit: cover;
}

.upload-tip {
    font-size: 12px;
    color: var(--text-secondary);
}

.file-link-preview {
    display: flex;
    align-items: center;
    gap: 5px;
    margin-top: 10px;
    font-size: 13px;
    color: var(--primary-color);
}
</style>
