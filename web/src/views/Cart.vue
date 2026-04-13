<template>
  <div class="page-container">
    <div class="cart-header">
      <h2>我的购物车</h2>
      <span class="cart-count">共 {{ cartList.length }} 件商品</span>
    </div>

    <div class="cart-content" v-loading="loading">
      <div class="custom-table-wrapper" v-if="cartList.length > 0">
        <el-table 
            :data="cartList" 
            style="width: 100%" 
            @selection-change="handleSelectionChange"
            :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
        >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="商品信息" min-width="400">
            <template #default="{ row }">
                <div class="material-info" v-if="row.material">
                <div class="thumb-wrapper">
                    <el-image
                      :src="row.material.coverImg || 'https://placehold.co/100x100?text=Material'"
                      :preview-src-list="[row.material.coverImg]"
                      fit="cover"
                      :preview-teleported="true"
                      class="thumb"
                    />
                </div>
                <div class="info">
                    <div class="name" @click="$router.push(`/materials/${row.material.id}`)">{{ row.material.name }}</div>
                    <div class="meta">
                        <span class="price-tag">¥ {{ row.material.price }}</span>
                    </div>
                </div>
                </div>
                <div v-else class="invalid-item">
                    <el-tag type="info">已失效</el-tag>
                    <span class="invalid-text">该商品已下架或删除</span>
                </div>
            </template>
            </el-table-column>
            <el-table-column label="单价" width="150" align="center">
            <template #default="{ row }">
                <span class="unit-price">¥ {{ row.material ? row.material.price : 0 }}</span>
            </template>
            </el-table-column>
            <el-table-column label="数量" width="200" align="center">
            <template #default="{ row }">
                <el-input-number 
                    v-model="row.quantity" 
                    :min="1" 
                    :max="row.material ? row.material.stock : 1" 
                    size="default" 
                    @change="updateQuantity(row)" 
                />
            </template>
            </el-table-column>
            <el-table-column label="小计" width="150" align="center">
            <template #default="{ row }">
                <span class="subtotal">¥ {{ (row.material ? row.material.price * row.quantity : 0).toFixed(2) }}</span>
            </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right" align="center">
            <template #default="{ row }">
                <el-button type="danger" icon="Delete" circle plain @click="handleDelete(row.id)"></el-button>
            </template>
            </el-table-column>
        </el-table>
      </div>
      
      <el-empty v-else description="购物车是空的，快去选购吧~">
          <el-button type="primary" @click="$router.push('/materials')">去逛逛</el-button>
      </el-empty>

      <div class="cart-footer" v-if="cartList.length > 0">
        <div class="left">
          <span class="selection-info">已选择 <span class="count">{{ multipleSelection.length }}</span> 件商品</span>
        </div>
        <div class="right">
          <div class="total-info">
              <span class="label">合计：</span>
              <span class="amount">¥ {{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" class="checkout-btn" :disabled="multipleSelection.length === 0" @click="handleCheckout">
              去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartItem, deleteCartItem, createOrder } from '@/api/trade'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const cartList = ref([])
const loading = ref(false)
const multipleSelection = ref([])

onMounted(() => {
    fetchCart()
})

const fetchCart = async () => {
    if (!user.value.id) {
        router.push('/login')
        return
    }
    
    loading.value = true
    try {
        const res = await getCartList(user.value.id)
        cartList.value = res || []
    } catch (error) {
        console.error(error)
        ElMessage.error('获取购物车失败')
    } finally {
        loading.value = false
    }
}

const updateQuantity = async (row) => {
    try {
        await updateCartItem(row)
    } catch (error) {
        console.error(error)
        ElMessage.error('更新失败')
    }
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确认删除该商品吗?', '提示', { 
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning' 
    })
    .then(async () => {
        await deleteCartItem(id)
        ElMessage.success('删除成功')
        fetchCart()
    })
}

const handleSelectionChange = (val) => {
    multipleSelection.value = val
}

const totalPrice = computed(() => {
    return multipleSelection.value.reduce((total, item) => {
        const price = item.material ? item.material.price : 0
        return total + price * item.quantity
    }, 0)
})

const handleCheckout = async () => {
    if (multipleSelection.value.length === 0) return
    
    try {
        const orderData = {
            userId: user.value.id,
            totalAmount: totalPrice.value,
            items: multipleSelection.value.map(item => ({
                materialId: item.materialId,
                quantity: item.quantity,
                price: item.material.price
            })),
            cartIds: multipleSelection.value.map(item => item.id)
        }
        await createOrder(orderData)
        ElMessage.success('下单成功')
        // Redirect to profile orders tab instead of profile main
        // We need to handle this navigation or pass a query param
        // But for now simply redirecting to profile is okay, user can click orders.
        // Or better: router.push({ path: '/profile', query: { tab: 'orders' } }) if Profile supports it.
        // My Profile.vue implementation uses internal state activeMenu defaulting to 'info'.
        // I should probably make Profile.vue aware of query params, but for now simple redirect.
        router.push('/profile') 
    } catch (error) {
        ElMessage.error('下单失败')
    }
}
</script>

<style scoped>
.page-container {
    max-width: 1200px;
    margin: 40px auto;
    padding: 0 20px;
}

.cart-header {
    display: flex;
    align-items: baseline;
    gap: 15px;
    margin-bottom: 30px;
}

.cart-header h2 {
    font-size: 28px;
    color: #303133;
    margin: 0;
}

.cart-count {
    color: #909399;
    font-size: 14px;
}

.cart-content {
    background: #fff;
    border-radius: 12px;
    min-height: 400px;
}

.custom-table-wrapper {
    border: 1px solid #ebeef5;
    border-radius: 12px;
    overflow: hidden;
}

.material-info {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px 0;
}

.thumb-wrapper {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid #f0f0f0;
    flex-shrink: 0;
}

.thumb {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.info {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.name {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
    cursor: pointer;
    transition: color 0.3s;
}

.name:hover {
    color: var(--el-color-primary);
}

.price-tag {
    color: #909399;
    font-size: 14px;
}

.unit-price {
    color: #606266;
}

.subtotal {
    color: #f56c6c;
    font-weight: 600;
    font-size: 16px;
}

.invalid-item {
    display: flex;
    align-items: center;
    gap: 10px;
}

.invalid-text {
    color: #909399;
    font-size: 13px;
}

.cart-footer {
    margin-top: 30px;
    padding: 20px 30px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: sticky;
    bottom: 20px;
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
    z-index: 10;
}

.selection-info {
    color: #606266;
}

.selection-info .count {
    color: var(--el-color-primary);
    font-weight: bold;
    font-size: 18px;
    margin: 0 4px;
}

.right {
    display: flex;
    align-items: center;
    gap: 30px;
}

.total-info {
    display: flex;
    align-items: baseline;
}

.total-info .label {
    color: #606266;
    font-size: 14px;
}

.total-info .amount {
    color: #f56c6c;
    font-size: 28px;
    font-weight: bold;
    margin-left: 5px;
}

.checkout-btn {
    padding: 12px 40px;
    font-size: 16px;
    border-radius: 25px;
}
</style>
