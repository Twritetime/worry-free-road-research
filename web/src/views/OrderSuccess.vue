<template>
  <div class="order-success-container" v-loading="loading">
    <el-result
      icon="success"
      title="支付成功"
      sub-title="您的订单已支付成功，感谢您的购买！"
    >
      <template #extra>
        <el-button type="primary" @click="goToOrderList">查看订单列表</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </template>
    </el-result>

    <div class="order-detail-card" v-if="order">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>订单详情</span>
          </div>
        </template>
        <div class="info-row">
          <span class="label">订单号:</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">订单金额:</span>
          <span class="value price">¥{{ order.totalAmount }}</span>
        </div>
        <div class="info-row">
          <span class="label">下单时间:</span>
          <span class="value">{{ order.createTime }}</span>
        </div>
        
        <el-divider content-position="left">商品列表</el-divider>
        <el-table :data="order.items" border style="width: 100%">
            <el-table-column prop="materialName" label="商品名称" />
            <el-table-column prop="price" label="单价" width="100" />
            <el-table-column prop="quantity" label="数量" width="80" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderByNo } from '@/api/trade'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref(null)

onMounted(() => {
  const orderNo = route.query.out_trade_no
  if (orderNo) {
    fetchOrderDetail(orderNo)
  } else {
    loading.value = false
    ElMessage.error('无效的订单号')
  }
})

const fetchOrderDetail = async (orderNo) => {
  try {
    loading.value = true
    const res = await getOrderByNo(orderNo)
    order.value = res
    setTimeout(() => {
      router.push('/order/list')
    }, 2000)
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const goToOrderList = () => {
  const rawOrderNo = route.query.out_trade_no
  const orderNo = Array.isArray(rawOrderNo) ? rawOrderNo[0] : rawOrderNo
  if (orderNo) {
    router.push({ path: '/order/list', query: { out_trade_no: orderNo } })
    return
  }
  router.push('/order/list')
}

const goToHome = () => {
  router.push('/')
}

const goBack = () => {
  const returnUrl = sessionStorage.getItem('orderReturnUrl')
  if (returnUrl) {
    sessionStorage.removeItem('orderReturnUrl')
    router.push(returnUrl)
  } else {
    const referrer = document.referrer
    if (referrer && referrer.includes(window.location.host)) {
      window.history.back()
    } else {
      router.push('/')
    }
  }
}
</script>

<style scoped>
.order-success-container {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
}
.order-detail-card {
  margin-top: 30px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
}
.label {
  color: #606266;
}
.value {
  color: #303133;
}
.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
