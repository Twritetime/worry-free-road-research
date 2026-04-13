<template>
  <div class="page-container" v-loading="loading">
    <div class="breadcrumb-wrapper">
       <el-breadcrumb separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/materials' }">资料商城</el-breadcrumb-item>
        <el-breadcrumb-item>{{ material.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="detail-content" v-if="material.id">
      <div class="product-showcase">
         <div class="product-image-box">
             <el-image
               :src="material.coverImg || 'https://placehold.co/400x500?text=Material'"
               :preview-src-list="[material.coverImg]"
               fit="cover"
               :preview-teleported="true"
               class="product-image"
               style="width: 100%; height: 100%"
             />
         </div>
         <div class="product-info-box">
             <div class="product-header">
                <el-tag :type="getCategoryType(material.category)" effect="dark" class="category-tag">{{ formatCategory(material.category) }}</el-tag>
                <h1 class="product-title">{{ material.name }}</h1>
                <div class="product-meta">
                   <span class="meta-item">销量: {{ material.sales || 0 }}</span>
                   <span class="meta-separator">|</span>
                   <span class="meta-item">库存: {{ material.stock }}</span>
                </div>
             </div>
             
             <div class="product-price-area">
                <span class="currency">¥</span>
                <span class="price">{{ material.price }}</span>
             </div>

             <div class="product-specs">
                <div class="spec-row">
                    <span class="spec-label">文件格式</span>
                    <span class="spec-value">{{ material.specs || 'PDF' }}</span>
                </div>
                <div class="spec-row">
                    <span class="spec-label">发货方式</span>
                    <span class="spec-value">管理员手动发货</span>
                </div>
             </div>

             <el-divider />

             <div class="product-actions">
                <div v-if="hasPurchased" class="purchased-state">
                    <el-alert title="您已购买此资料，可直接下载" type="success" :closable="false" show-icon style="margin-bottom: 20px;" />
                    <el-button type="success" size="large" :icon="Download" class="action-btn full-width" @click="handleDownload">立即下载</el-button>
                </div>
                <div v-else-if="pendingShipment" class="buying-state">
                     <el-alert title="您已完成支付，等待管理员发货后可下载" type="info" :closable="false" show-icon />
                </div>
                <div v-else-if="material.stock <= 0" class="buying-state">
                     <el-alert title="该资料暂时缺货" type="warning" :closable="false" show-icon />
                </div>
                <div v-else class="buying-state">
                    <div class="quantity-selector">
                        <span class="label">购买数量</span>
                        <el-input-number v-model="quantity" :min="1" :max="material.stock" />
                    </div>
                    <div class="action-buttons">
                        <el-button type="primary" size="large" class="action-btn buy-btn" @click="handleBuy">立即购买</el-button>
                        <el-button type="warning" plain size="large" class="action-btn cart-btn" :icon="ShoppingCart" @click="handleAddToCart">加入购物车</el-button>
                    </div>
                </div>
                <div class="favorite-action">
                    <el-button link :type="isFavorite ? 'danger' : 'info'" @click="handleToggleFavorite">
                        <el-icon style="margin-right: 4px; font-size: 18px;">
                            <StarFilled v-if="isFavorite" />
                            <Star v-else />
                        </el-icon>
                        {{ isFavorite ? '已收藏' : '收藏商品' }}
                    </el-button>
                </div>
             </div>
         </div>
      </div>

      <div class="detail-section">
          <div class="section-tabs">
              <span class="active-tab">商品详情</span>
          </div>
          <div class="section-content" v-html="material.description || '暂无描述'"></div>
      </div>
    </div>
    
    <el-empty v-else-if="!loading" description="未找到资料信息"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMaterialById } from '@/api/material'
import { addToCart, createOrder, getOrderList } from '@/api/trade'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Download, ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const material = ref({})
const quantity = ref(1)
const hasPurchased = ref(false)
const pendingShipment = ref(false)
const isFavorite = ref(false)
const autoDownloadTriggered = ref(false)

const categoryMap = {
  'public': '公共课',
  'major': '专业课',
  'interview': '复试资料'
}

const formatCategory = (category) => {
    return categoryMap[category] || category
}

const getCategoryType = (val) => {
    const map = { 'public': 'primary', 'major': 'success', 'interview': 'warning' }
    return map[val] || 'info'
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    fetchMaterialDetail(id)
  }
})

const fetchMaterialDetail = async (id) => {
  loading.value = true
  try {
    const res = await getMaterialById(id)
    if (res) {
      material.value = res
      if (user.value.id) {
          checkPurchasedStatus(id)
          checkFavoriteStatus(id)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取资料详情失败')
  } finally {
    loading.value = false
  }
}

const checkPurchasedStatus = async (materialId) => {
    try {
        const orders = await getOrderList(user.value.id)
        const matchedOrders = (orders || []).filter(order =>
            (order.items || []).some(item => Number(item.materialId) === Number(materialId))
        )
        hasPurchased.value = matchedOrders.some(order => order.status === 3)
        pendingShipment.value = !hasPurchased.value && matchedOrders.some(order => order.status === 1)
        if (hasPurchased.value && material.value.fileUrl && !autoDownloadTriggered.value) {
            autoDownloadTriggered.value = true
            ElMessage.success('订单已发货，正在跳转下载链接')
            window.location.assign(material.value.fileUrl)
        }
    } catch (error) {
        console.error(error)
    }
}

const checkFavoriteStatus = async (targetId) => {
    try {
        const res = await checkFavorite({
            userId: user.value.id,
            targetId: targetId,
            type: 3 // 3 is material
        })
        isFavorite.value = res
    } catch (error) {
        console.error(error)
    }
}

const handleToggleFavorite = async () => {
    if (!user.value.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    try {
        await toggleFavorite({
            userId: user.value.id,
            targetId: material.value.id,
            targetType: 3, // Material type
            targetTitle: material.value.name,
            targetCover: material.value.coverImg
        })
        isFavorite.value = !isFavorite.value
        ElMessage.success(isFavorite.value ? '已收藏' : '已取消收藏')
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

const handleAddToCart = async () => {
    if (!user.value.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    try {
        await addToCart({
            userId: user.value.id,
            materialId: material.value.id,
            quantity: quantity.value
        })
        ElMessage.success('已加入购物车')
    } catch (error) {
        console.error(error)
    }
}

const handleBuy = async () => {
    if (!user.value.id) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    try {
        // Direct buy usually creates an order and redirects to payment
        // For simplicity reusing cart or create order directly
        const order = await createOrder({
            userId: user.value.id,
            materialId: material.value.id,
            quantity: quantity.value
        })
        // Assuming createOrder returns orderId or order object
        ElMessage.success('订单创建成功')
        router.push('/order/list') 
    } catch (error) {
        console.error(error)
    }
}

const handleDownload = () => {
    if (material.value.fileUrl) {
        window.open(material.value.fileUrl, '_blank')
    } else {
        ElMessage.warning('该资料暂无文件可下载')
    }
}
</script>

<style scoped>
.page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    min-height: 100vh;
}

.breadcrumb-wrapper {
    margin: 20px 0 30px;
}

.detail-content {
    background: transparent;
}

.product-showcase {
    background: white;
    border-radius: 20px;
    padding: 30px;
    display: flex;
    gap: 40px;
    margin-bottom: 30px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.03);
}

.product-image-box {
    width: 400px;
    height: 500px;
    background: #f8f9fa;
    border-radius: 12px;
    overflow: hidden;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.product-image {
    max-width: 100%;
    max-height: 100%;
    object-fit: cover;
}

.product-info-box {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.category-tag {
    margin-bottom: 15px;
}

.product-title {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 15px;
    line-height: 1.3;
}

.product-meta {
    display: flex;
    gap: 15px;
    color: var(--text-secondary);
    font-size: 14px;
    margin-bottom: 25px;
}

.meta-separator {
    color: #e2e8f0;
}

.product-price-area {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
}

.currency {
    font-size: 20px;
    color: #ef4444;
    font-weight: 600;
    margin-right: 4px;
}

.price {
    font-size: 36px;
    color: #ef4444;
    font-weight: 700;
}

.product-specs {
    margin-bottom: 25px;
}

.spec-row {
    display: flex;
    margin-bottom: 12px;
    font-size: 14px;
}

.spec-label {
    width: 80px;
    color: var(--text-secondary);
}

.spec-value {
    color: var(--text-primary);
    font-weight: 500;
}

.product-actions {
    margin-top: auto;
}

.buying-state {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.quantity-selector {
    display: flex;
    align-items: center;
    gap: 15px;
}

.quantity-selector .label {
    font-size: 14px;
    color: var(--text-secondary);
}

.action-buttons {
    display: flex;
    gap: 15px;
}

.action-btn {
    flex: 1;
    height: 50px;
    font-size: 16px;
}

.full-width {
    width: 100%;
}

.favorite-action {
    margin-top: 15px;
    text-align: right;
}

.detail-section {
    background: white;
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.03);
}

.section-tabs {
    border-bottom: 1px solid #e2e8f0;
    margin-bottom: 30px;
}

.active-tab {
    display: inline-block;
    padding: 15px 30px;
    font-size: 18px;
    font-weight: 600;
    color: var(--primary-color);
    border-bottom: 3px solid var(--primary-color);
    margin-bottom: -2px;
}

.section-content {
    line-height: 1.8;
    color: var(--text-primary);
    font-size: 16px;
}

@media (max-width: 900px) {
    .product-showcase {
        flex-direction: column;
        padding: 20px;
    }
    
    .product-image-box {
        width: 100%;
        height: 300px;
    }
}
</style>
