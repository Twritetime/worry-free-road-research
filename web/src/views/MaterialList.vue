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
          <el-button v-if="isFrontAdmin" type="primary" :icon="Plus" @click="openDialog()">发布资料</el-button>
       </div>

       <div class="sort-toolbar">
          <span class="sort-label">排序</span>
          <button
            v-for="item in sortTabs"
            :key="item.value"
            type="button"
            class="sort-item"
            :class="{ active: sortField === item.value }"
            @click="toggleSort(item.value)"
          >
            {{ item.label }}
            <span class="sort-arrow">{{ getSortArrow(item.value) }}</span>
          </button>
       </div>

       <div class="content-layout">
          <div class="material-section">
            <div class="material-grid" v-loading="loading">
                <div v-for="item in materialList" :key="item.id" class="material-card" @click="viewDetail(item.id)">
                  <div class="card-image">
                      <el-image
                        :src="item.coverImg || 'https://placehold.co/300x400?text=Material'"
                        :preview-src-list="[item.coverImg]"
                        fit="cover"
                        :preview-teleported="true"
                        class="material-img"
                        @click.stop
                      />
                      <div class="card-overlay">
                        <el-button type="primary" round>查看详情</el-button>
                      </div>
                  </div>
                  <div class="card-content">
                      <div class="card-tags">
                        <el-tag size="small" :type="getCategoryType(item.category)" effect="light">{{ getCategoryLabel(item.category) }}</el-tag>
                        <el-tag v-if="item.fileFormat" size="small" effect="plain" class="format-tag">{{ item.fileFormat }}</el-tag>
                        <span class="stock-badge" v-if="item.stock < 10 && item.stock > 0">仅剩 {{ item.stock }} 份</span>
                        <span class="stock-badge out" v-if="item.stock <= 0">已售罄</span>
                      </div>
                      <h3 class="card-title" :title="item.name">{{ item.name }}</h3>
                      <div class="card-meta">
                        <span class="meta-item" v-if="item.author">
                          <el-icon><User /></el-icon>
                          {{ item.author }}
                        </span>
                        <span class="meta-item" v-if="item.applyYear">
                          <el-icon><Calendar /></el-icon>
                          {{ item.applyYear }}考研
                        </span>
                      </div>
                      <div class="card-stats">
                        <span class="stat-item">销量 {{ item.sales || 0 }}</span>
                        <span class="stat-item" v-if="item.downloadCount">下载 {{ item.downloadCount }}</span>
                        <span class="stat-item" v-if="item.fileSize">{{ item.fileSize }}</span>
                      </div>
                      <div v-if="item.rating" class="card-rating">
                        <el-rate v-model="item.rating" disabled :max="5" size="small" />
                        <span class="rating-score">{{ item.rating.toFixed(1) }}</span>
                      </div>
                      <div class="card-footer">
                        <div class="price-wrap">
                          <span class="price">¥ {{ formatPrice(item.price) }}</span>
                          <span v-if="hasOriginalPrice(item)" class="origin-price">¥ {{ formatPrice(item.originalPrice) }}</span>
                        </div>
                      </div>
                      <el-button
                        class="add-cart-btn"
                        type="warning"
                        plain
                        :disabled="item.stock <= 0"
                        @click.stop="handleQuickAddToCart(item)"
                      >
                        加入购物车
                      </el-button>
                  </div>
                    <div v-if="isFrontAdmin" class="admin-actions" @click.stop>
                        <el-button circle size="small" :icon="Edit" @click="openDialog(item)"></el-button>
                        <el-button circle size="small" type="danger" :icon="Delete" @click="handleDelete(item.id)"></el-button>
                    </div>
                </div>
            </div>
            <el-empty v-if="!loading && materialList.length === 0" description="暂无相关资料" />
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
          <div class="flash-sidebar" v-if="flashSaleList.length">
            <div class="flash-sale-section">
                <div class="flash-header">
                  <h3 class="flash-title">限时特惠</h3>
                  <div class="flash-countdown">最近结束 {{ formatCountdown(flashRemainingSeconds) }}</div>
                </div>
                <div class="flash-grid">
                  <div v-for="item in flashSaleList" :key="item.id" class="flash-card" @click="viewDetail(item.id)">
                    <div class="flash-image" @click.stop="viewDetail(item.id)">
                      <el-image
                        :src="item.coverImg || 'https://placehold.co/220x160?text=Material'"
                        :preview-src-list="[item.coverImg]"
                        fit="cover"
                        :preview-teleported="true"
                        class="material-img"
                      />
                      <span class="discount-tag">{{ getDiscountText(item) }}</span>
                    </div>
                    <div class="flash-info">
                      <h4 class="flash-name">{{ item.name }}</h4>
                      <div class="flash-price-row">
                        <span class="flash-price">¥{{ formatPrice(item.price) }}</span>
                        <span class="flash-origin">¥{{ formatPrice(item.originalPrice) }}</span>
                      </div>
                      <div class="flash-item-countdown">剩余 {{ formatCountdown(getRemainingSeconds(item.flashEndTime)) }}</div>
                    </div>
                  </div>
                </div>
            </div>
          </div>
          <div class="recommend-section" v-if="user?.id && recommendedList.length > 0">
            <div class="recommend-banner">
              <div class="recommend-header">
                <span class="recommend-icon">✨</span>
                <span class="recommend-title">猜你喜欢</span>
                <span class="recommend-desc">根据您的浏览记录推荐</span>
              </div>
              <div class="recommend-scroll">
                <div v-for="item in recommendedList" :key="'rec-' + item.id" class="recommend-card" @click="viewDetail(item.id)">
                  <div class="recommend-image">
                    <el-image :src="item.coverImg || 'https://placehold.co/200x150?text=Material'" fit="cover" class="recommend-img" />
                  </div>
                  <div class="recommend-info">
                    <h4 class="recommend-name">{{ item.name }}</h4>
                    <div class="recommend-price">¥ {{ formatPrice(item.price) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
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
                <el-form-item label="划线价" prop="originalPrice">
                  <el-input-number v-model="materialForm.originalPrice" :precision="2" :step="0.1" :min="0" style="width: 100%"></el-input-number>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="库存" prop="stock">
                  <el-input-number v-model="materialForm.stock" :min="0" :precision="0" style="width: 100%"></el-input-number>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="8">
                <el-form-item label="规格" prop="specs">
                    <el-input v-model="materialForm.specs" placeholder="例如: PDF"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="活动开始" prop="flashStartTime">
                  <el-date-picker
                    v-model="materialForm.flashStartTime"
                    type="datetime"
                    value-format="YYYY-MM-DDTHH:mm:ss"
                    placeholder="选填"
                    style="width: 100%"
                  />
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="活动结束" prop="flashEndTime">
                  <el-date-picker
                    v-model="materialForm.flashEndTime"
                    type="datetime"
                    value-format="YYYY-MM-DDTHH:mm:ss"
                    placeholder="选填"
                    style="width: 100%"
                  />
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
                drag
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
                ref="fileUploadRef"
                class="file-uploader"
                :action="uploadUrl"
                :limit="1"
                :on-success="handleFileSuccess"
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
import { ref, onMounted, reactive, watch, computed, onUnmounted } from 'vue'
import { getMaterialList, getFlashMaterialList, saveMaterial, updateMaterial, deleteMaterial } from '@/api/material'
import { addToCart } from '@/api/trade'
import { getRecommendedMaterials } from '@/api/recommend'
import { useRouter, useRoute } from 'vue-router'
import { Plus, Edit, Delete, Search, Upload, Document, User, Calendar } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { isFrontAdmin, user } = storeToRefs(userStore)

const materialList = ref([])
const recommendedList = ref([])
const flashMaterialList = ref([])
const loading = ref(false)
const keyword = ref('')
const activeCategory = ref('all')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const uploadUrl = 'http://localhost:8080/file/upload'
const sortField = ref('comprehensive')
const sortOrder = ref('desc')
const nowTimestamp = ref(Date.now())
let countdownTimer = null

const tabs = [
    { name: 'all', label: '全部资料' },
    { name: 'public', label: '公共课' },
    { name: 'major', label: '专业课' },
    { name: 'interview', label: '复试资料' }
]
const sortTabs = [
    { value: 'comprehensive', label: '综合' },
    { value: 'sales', label: '销量' },
    { value: 'price', label: '价格' }
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
    originalPrice: 0,
    stock: 0,
    specs: '',
    description: '',
    coverImg: '',
    fileUrl: '',
    flashStartTime: '',
    flashEndTime: '',
    status: 1
})
const fileUploadRef = ref(null)

const rules = {
    name: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
    category: [{ required: true, message: '请选择分类', trigger: 'change' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

onMounted(() => {
    if (route.query.category) {
        activeCategory.value = route.query.category
    }
    startCountdown()
    fetchMaterials()
    fetchFlashMaterials()
    fetchRecommendations()
})

onUnmounted(() => {
    if (countdownTimer) {
        clearInterval(countdownTimer)
    }
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
    const map = {
        'public': '公共课',
        'major': '专业课',
        'interview': '复试',
        '公共课': '公共课',
        '专业课': '专业课',
        '复试资料': '复试'
    }
    return map[val] || val
}

const getCategoryType = (val) => {
    const map = {
        'public': 'primary',
        'major': 'success',
        'interview': 'warning',
        '公共课': 'primary',
        '专业课': 'success',
        '复试资料': 'warning'
    }
    return map[val] || 'info'
}

const flashSaleList = computed(() => {
    return (flashMaterialList.value || [])
        .filter(item => isFlashSaleActive(item))
        .slice()
        .sort((a, b) => getTimeValue(a.flashEndTime) - getTimeValue(b.flashEndTime))
        .slice(0, 4)
})

const flashRemainingSeconds = computed(() => {
    if (!flashSaleList.value.length) {
        return 0
    }
    const nearestEnd = flashSaleList.value
        .map(item => getTimeValue(item.flashEndTime))
        .filter(time => time > 0)
        .sort((a, b) => a - b)[0]
    if (!nearestEnd) {
        return 0
    }
    return Math.max(Math.floor((nearestEnd - nowTimestamp.value) / 1000), 0)
})

const fetchMaterials = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: keyword.value,
            category: activeCategory.value === 'all' ? undefined : activeCategory.value,
            sortBy: sortField.value,
            sortOrder: sortOrder.value
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

const fetchFlashMaterials = async () => {
    try {
        const res = await getFlashMaterialList(8)
        flashMaterialList.value = Array.isArray(res) ? res : []
    } catch (error) {
        flashMaterialList.value = []
    }
}

const fetchRecommendations = async () => {
    try {
        if (user.value?.id) {
            const res = await getRecommendedMaterials({ limit: 6 })
            recommendedList.value = Array.isArray(res) ? res : (res.records || [])
        } else {
            recommendedList.value = []
        }
    } catch (error) {
        console.error('获取推荐失败', error)
        recommendedList.value = []
    }
}

const handlePageChange = (val) => {
    pageNum.value = val
    fetchMaterials()
}

const viewDetail = (id) => {
    router.push(`/materials/${id}`)
}

const toggleSort = (field) => {
    if (sortField.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
    } else {
        sortField.value = field
        sortOrder.value = 'desc'
    }
    pageNum.value = 1
    fetchMaterials()
}

const getSortArrow = (field) => {
    if (sortField.value !== field) {
        return '↕'
    }
    return sortOrder.value === 'asc' ? '↑' : '↓'
}

const hasOriginalPrice = (item) => {
    const original = Number(item.originalPrice || 0)
    const current = Number(item.price || 0)
    return original > current && current > 0
}

const getDiscountText = (item) => {
    const original = Number(item.originalPrice || 0)
    const current = Number(item.price || 0)
    if (!(original > current && current > 0)) {
        return '限时价'
    }
    return `${((current / original) * 10).toFixed(1)}折`
}

const getTimeValue = (time) => {
    if (!time) {
        return 0
    }
    const raw = String(time).trim()
    if (!raw) {
        return 0
    }
    const direct = new Date(raw).getTime()
    if (!Number.isNaN(direct)) {
        return direct
    }
    const normalized = raw.replace('T', ' ')
    const match = normalized.match(/^(\d{4})-(\d{2})-(\d{2})\s(\d{2}):(\d{2})(?::(\d{2}))?$/)
    if (!match) {
        return 0
    }
    const value = new Date(
        Number(match[1]),
        Number(match[2]) - 1,
        Number(match[3]),
        Number(match[4]),
        Number(match[5]),
        Number(match[6] || 0)
    ).getTime()
    return Number.isNaN(value) ? 0 : value
}

const normalizeDateTimeString = (time) => {
    if (!time) {
        return ''
    }
    return String(time).replace(' ', 'T')
}

const isFlashSaleActive = (item) => {
    if (!item || item.stock <= 0 || !hasOriginalPrice(item)) {
        return false
    }
    const start = getTimeValue(item.flashStartTime)
    const end = getTimeValue(item.flashEndTime)
    if (!start || !end) {
        return false
    }
    const now = nowTimestamp.value
    return start <= now && now <= end
}

const getRemainingSeconds = (endTime) => {
    const end = getTimeValue(endTime)
    if (!end) {
        return 0
    }
    return Math.max(Math.floor((end - nowTimestamp.value) / 1000), 0)
}

const formatPrice = (price) => {
    return Number(price || 0).toFixed(2)
}

const formatCountdown = (seconds) => {
    const safeSeconds = Math.max(Number(seconds) || 0, 0)
    const day = Math.floor(safeSeconds / 86400)
    const hour = String(Math.floor((safeSeconds % 86400) / 3600)).padStart(2, '0')
    const minute = String(Math.floor((safeSeconds % 3600) / 60)).padStart(2, '0')
    const second = String(safeSeconds % 60).padStart(2, '0')
    if (day > 0) {
        return `${day}天 ${hour}:${minute}:${second}`
    }
    return `${hour}:${minute}:${second}`
}

const startCountdown = () => {
    if (countdownTimer) {
        clearInterval(countdownTimer)
    }
    countdownTimer = setInterval(() => {
        nowTimestamp.value = Date.now()
    }, 1000)
}

const handleQuickAddToCart = async (item) => {
    if (!user.value.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    if (item.stock <= 0) {
        ElMessage.warning('该商品已售罄')
        return
    }
    try {
        await addToCart({
            userId: user.value.id,
            materialId: item.id,
            quantity: 1
        })
        ElMessage.success('已加入购物车')
    } catch (error) {
        ElMessage.error('加入购物车失败')
    }
}

const openDialog = (item = null) => {
    if (item) {
        isEdit.value = true
        Object.assign(materialForm, {
            ...item,
            flashStartTime: normalizeDateTimeString(item.flashStartTime),
            flashEndTime: normalizeDateTimeString(item.flashEndTime)
        })
    } else {
        isEdit.value = false
        Object.assign(materialForm, {
            id: null,
            name: '',
            category: 'public',
            price: 0,
            originalPrice: 0,
            stock: 999,
            specs: 'PDF',
            description: '',
            coverImg: '',
            fileUrl: '',
            flashStartTime: '',
            flashEndTime: '',
            status: 1
        })
    }
    dialogVisible.value = true
}

const handleCoverSuccess = (res) => {
    const url = typeof res === 'string' ? res : (res?.data || '')
    materialForm.coverImg = url
}

const handleFileSuccess = (res, file) => {
    const url = typeof res === 'string' ? res : (res?.data || '')
    materialForm.fileUrl = url
    const fileName = file?.name || ''
    const extension = fileName.split('.').pop().toLowerCase()
    materialForm.fileSize = file?.size ? (file.size / 1024 / 1024).toFixed(2) + 'MB' : ''
    materialForm.fileFormat = getFileFormat(extension)
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

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (isEdit.value) {
                    await updateMaterial({
                        ...materialForm,
                        originalPrice: materialForm.originalPrice || null,
                        flashStartTime: materialForm.flashStartTime || null,
                        flashEndTime: materialForm.flashEndTime || null
                    })
                    ElMessage.success('更新成功')
                } else {
                    await saveMaterial({
                        ...materialForm,
                        originalPrice: materialForm.originalPrice || null,
                        flashStartTime: materialForm.flashStartTime || null,
                        flashEndTime: materialForm.flashEndTime || null
                    })
                    ElMessage.success('发布成功')
                }
                dialogVisible.value = false
                fetchMaterials()
                fetchFlashMaterials()
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
            fetchFlashMaterials()
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
    max-width: 1560px;
    margin: 0 auto;
    padding: 0 20px 60px;
}

.content-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 300px;
    gap: 18px;
    align-items: start;
}

.material-section {
    min-width: 0;
}

.filter-bar {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
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

.sort-toolbar {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 22px;
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    padding: 10px 14px;
}

.sort-label {
    font-size: 14px;
    color: #64748b;
    margin-right: 6px;
}

.sort-item {
    border: none;
    background: transparent;
    color: #475569;
    border-radius: 8px;
    padding: 6px 12px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s;
}

.sort-item:hover {
    background: #f1f5f9;
}

.sort-item.active {
    background: #e0ecff;
    color: #2563eb;
}

.sort-arrow {
    margin-left: 4px;
    font-size: 12px;
}

.flash-sale-section {
    background: linear-gradient(135deg, #fff6ee 0%, #ffe6e6 100%);
    border: 1px solid #ffd8d8;
    border-radius: 14px;
    padding: 18px;
}

.flash-sidebar {
    position: sticky;
    top: 16px;
}

.flash-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.flash-title {
    font-size: 18px;
    font-weight: 700;
    color: #b91c1c;
}

.flash-countdown {
    padding: 6px 10px;
    border-radius: 999px;
    background: #b91c1c;
    color: #ffffff;
    font-size: 13px;
    font-weight: 600;
}

.flash-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.flash-card {
    background: #ffffff;
    border-radius: 10px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid #ffe2e2;
    transition: all 0.2s;
}

.flash-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(239, 68, 68, 0.15);
}

.flash-image {
    height: 160px;
    position: relative;
}

.flash-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.discount-tag {
    position: absolute;
    top: 10px;
    left: 10px;
    background: #ef4444;
    color: #ffffff;
    font-size: 12px;
    border-radius: 999px;
    padding: 4px 8px;
    font-weight: 600;
}

.flash-info {
    padding: 10px;
}

.flash-name {
    font-size: 14px;
    color: #1e293b;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.flash-price-row {
    display: flex;
    align-items: center;
    gap: 8px;
}

.flash-price {
    color: #dc2626;
    font-size: 18px;
    font-weight: 700;
}

.flash-origin {
    color: #94a3b8;
    font-size: 12px;
    text-decoration: line-through;
}

.flash-item-countdown {
    margin-top: 8px;
    font-size: 12px;
    color: #b91c1c;
    font-weight: 600;
}

.recommend-section {
    margin-top: 20px;
}

.recommend-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 15px 20px;
    margin-bottom: 20px;
}

.recommend-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
}

.recommend-icon {
    font-size: 18px;
}

.recommend-title {
    font-size: 16px;
    font-weight: 600;
    color: white;
}

.recommend-desc {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
}

.recommend-scroll {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    padding-bottom: 5px;
}

.recommend-scroll::-webkit-scrollbar {
    height: 4px;
}

.recommend-scroll::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 2px;
}

.recommend-card {
    flex-shrink: 0;
    width: 140px;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s ease;
}

.recommend-card:hover {
    transform: scale(1.03);
}

.recommend-image {
    width: 100%;
    height: 100px;
    background: #f5f5f5;
}

.recommend-img {
    width: 100%;
    height: 100%;
}

.recommend-info {
    padding: 8px 10px;
}

.recommend-name {
    font-size: 12px;
    font-weight: 500;
    color: #333;
    margin: 0 0 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.recommend-price {
    font-size: 13px;
    font-weight: 600;
    color: #f56c6c;
}

.material-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 18px;
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
    height: 200px;
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
    flex-wrap: wrap;
    gap: 4px;
}

.format-tag {
    font-size: 11px;
    padding: 0 6px;
    height: 20px;
    line-height: 20px;
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
    margin-bottom: 8px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    flex: 1;
}

.card-meta {
    display: flex;
    gap: 12px;
    margin-bottom: 6px;
    font-size: 12px;
    color: #64748b;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
}

.meta-item .el-icon {
    font-size: 12px;
}

.card-stats {
    display: flex;
    gap: 12px;
    margin-bottom: 6px;
    font-size: 12px;
    color: #94a3b8;
}

.stat-item {
    white-space: nowrap;
}

.card-rating {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 8px;
}

.rating-score {
    font-size: 12px;
    color: #666;
    font-weight: 500;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
    margin-bottom: 10px;
}

.price {
    font-size: 18px;
    font-weight: 700;
    color: #ef4444;
}

.price-wrap {
    display: flex;
    align-items: center;
    gap: 8px;
}

.origin-price {
    font-size: 12px;
    color: #94a3b8;
    text-decoration: line-through;
}

.specs {
    font-size: 12px;
    color: var(--text-secondary);
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 4px;
}

.add-cart-btn {
    width: 100%;
    border-radius: 8px;
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

@media (max-width: 1440px) {
    .material-grid {
        grid-template-columns: repeat(3, minmax(0, 1fr));
    }
}

@media (max-width: 1200px) {
    .material-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 1024px) {
    .content-layout {
        grid-template-columns: 1fr;
    }

    .flash-sidebar {
        position: static;
    }

    .material-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 720px) {
    .material-grid {
        grid-template-columns: 1fr;
    }
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
