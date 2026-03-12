<template>
  <div class="order-list-container">
    <div class="order-list" v-loading="loading">
      <div v-for="order in orderList" :key="order.id" class="order-item">
        <div class="order-header">
          <div class="header-left">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
          </div>
          <el-tag :type="getStatusTag(order.status)" effect="plain" round>{{ getStatusText(order.status) }}</el-tag>
        </div>
        
        <div class="order-content">
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="item-row">
              <div class="item-info">
                  <span class="item-name" @click="goToMaterial(item.materialId)">{{ item.materialName }}</span>
                  <div class="item-meta">
                      <span class="price">¥ {{ item.price }}</span>
                      <span class="quantity">x {{ item.quantity }}</span>
                  </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
            <div class="total-info">
                <span>共 {{ order.items.length }} 件商品</span>
                <span class="total-amount">
                    合计: <span class="amount">¥ {{ order.totalAmount }}</span>
                </span>
            </div>
            <div class="actions" v-if="order.status === 0">
                <el-button type="primary" size="default" round @click="handlePay(order.id)">立即支付</el-button>
            </div>
        </div>
      </div>
      
      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, payOrder } from '@/api/trade'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const orderList = ref([])
const loading = ref(false)

onMounted(() => {
    fetchOrders()
})

const fetchOrders = async () => {
    if (!user.value.id) {
        router.push('/login')
        return
    }
    
    loading.value = true
    try {
        const res = await getOrderList(user.value.id)
        orderList.value = res || []
    } catch (error) {
        console.error(error)
        ElMessage.error('获取订单失败')
    } finally {
        loading.value = false
    }
}

const goToMaterial = (id) => {
    router.push(`/materials/${id}`)
}

const handlePay = (id) => {
    ElMessageBox.confirm('确认支付该订单吗?', '支付确认', {
        confirmButtonText: '支付',
        cancelButtonText: '取消',
        type: 'success',
        center: true
    }).then(async () => {
        try {
            await payOrder(id)
            ElMessage.success('支付成功')
            fetchOrders()
        } catch (error) {
            ElMessage.error('支付失败')
        }
    })
}

const getStatusTag = (status) => {
    const map = {
        0: 'warning',
        1: 'success',
        2: 'info'
    }
    return map[status] || 'info'
}

const getStatusText = (status) => {
    const map = {
        0: '待付款',
        1: '已付款',
        2: '已取消'
    }
    return map[status] || '未知状态'
}
</script>

<style scoped>
.order-list-container {
    padding-bottom: 20px;
}

.order-item {
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 20px;
    transition: all 0.3s;
    overflow: hidden;
}

.order-item:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border-color: #dcdfe6;
}

.order-header {
    padding: 15px 20px;
    background: #f9fafc;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    gap: 20px;
    font-size: 13px;
    color: #909399;
}

.order-content {
    padding: 20px;
}

.item-row {
    margin-bottom: 15px;
}

.item-row:last-child {
    margin-bottom: 0;
}

.item-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.item-name {
    color: #303133;
    font-size: 14px;
    cursor: pointer;
    transition: color 0.3s;
    flex: 1;
    margin-right: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.item-name:hover {
    color: var(--el-color-primary);
}

.item-meta {
    display: flex;
    gap: 20px;
    align-items: center;
    width: 150px;
    justify-content: flex-end;
}

.item-meta .price {
    color: #606266;
    font-weight: 500;
}

.item-meta .quantity {
    color: #909399;
    font-size: 13px;
}

.order-footer {
    padding: 15px 20px;
    border-top: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
}

.total-info {
    font-size: 14px;
    color: #606266;
    display: flex;
    gap: 15px;
    align-items: baseline;
}

.total-amount .amount {
    color: #f56c6c;
    font-size: 18px;
    font-weight: 600;
    margin-left: 5px;
}
</style>
