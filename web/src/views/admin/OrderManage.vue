<template>
  <div class="order-manage">
    <div class="header">
      <h2>订单管理</h2>
      <div class="filters">
        <el-input v-model="keyword" placeholder="搜索订单号/用户" class="filter-control keyword-input" clearable @clear="handleSearch" />
        <el-select v-model="status" placeholder="状态筛选" clearable class="filter-control status-select" @change="handleSearch">
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已取消" :value="2" />
          <el-option label="已发货" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="warning" :loading="exporting" @click="handleExport">导出订单</el-button>
        <el-button type="danger" @click="handleCleanCart">清空购物车</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="orderList" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="220" />
        <el-table-column prop="username" label="用户" min-width="120" />
        <el-table-column prop="totalAmount" label="金额" min-width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="260">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="success" link :disabled="row.status !== 1" @click="updateHandler(row)">发货</el-button>
            <el-dropdown trigger="click" @command="(cmd) => handleStatusChange(row, cmd)" style="margin-left: 10px">
              <span class="el-dropdown-link">
                变更状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="row.status === 1 || row.status === 3 || row.status === 2">设为已支付</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.status === 3 || row.status === 2">取消订单</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <el-dialog title="订单详情" v-model="dialogVisible" width="600px">
        <div v-if="currentOrder" class="order-detail">
            <div class="info-row">
                <span class="label">订单号:</span>
                <span class="value">{{ currentOrder.orderNo }}</span>
            </div>
             <div class="info-row">
                <span class="label">下单用户:</span>
                <span class="value">{{ currentOrder.username }}</span>
            </div>
             <div class="info-row">
                <span class="label">订单金额:</span>
                <span class="value price">¥{{ currentOrder.totalAmount }}</span>
            </div>
             <div class="info-row">
                <span class="label">当前状态:</span>
                <span class="value">{{ getStatusText(currentOrder.status) }}</span>
            </div>
            <div class="info-row">
                <span class="label">下单时间:</span>
                <span class="value">{{ currentOrder.createTime }}</span>
            </div>
            
            <el-divider content-position="left">商品列表</el-divider>
            <el-table :data="currentOrder.items" border style="width: 100%">
                <el-table-column prop="materialName" label="商品名称" />
                <el-table-column prop="price" label="单价" width="100" />
                <el-table-column prop="quantity" label="数量" width="80" />
            </el-table>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">关闭</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getAllOrders, updateOrderStatus, clearCart } from '@/api/trade'

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const status = ref('')
const exporting = ref(false)

const dialogVisible = ref(false)
const currentOrder = ref(null)

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已发货' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success' }
  return map[status] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllOrders({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      status: status.value === '' ? undefined : status.value
    })
    orderList.value = res.records
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

const handleView = (row) => {
    currentOrder.value = row
    dialogVisible.value = true
}

const updateHandler = (row) => {
    ElMessageBox.confirm('确认发货吗？确认后订单状态将更新为“已发货”', '发货确认', { type: 'warning' })
    .then(async () => {
        await updateOrderStatus(row.id, 3)
        ElMessage.success('发货成功')
        fetchData()
    })
    .catch(() => {})
}

const handleStatusChange = (row, newStatus) => {
    ElMessageBox.confirm(`确认将订单状态更为 ${getStatusText(newStatus)} 吗?`, '提示', { type: 'warning' })
    .then(async () => {
        await updateOrderStatus(row.id, newStatus)
        ElMessage.success('状态更新成功')
        fetchData()
    })
    .catch(() => {})
}

const handleExport = () => {
    exporting.value = true
    const fetchAllOrderData = async () => {
      const exportPageSize = 200
      const result = []
      let current = 1
      let totalCount = 0
      do {
        const res = await getAllOrders({
          pageNum: current,
          pageSize: exportPageSize,
          keyword: keyword.value,
          status: status.value === '' ? undefined : status.value
        })
        const records = res.records || []
        totalCount = Number(res.total || 0)
        result.push(...records)
        if (records.length === 0) {
          break
        }
        current += 1
      } while (result.length < totalCount)
      return result
    }
    const escapeCsvValue = (value) => {
      if (value === null || value === undefined) {
        return ''
      }
      const str = String(value).replace(/"/g, '""')
      return `"${str}"`
    }
    fetchAllOrderData()
      .then((allOrders) => {
        if (allOrders.length === 0) {
          ElMessage.warning('暂无数据可导出')
          return
        }
        const headers = ['订单号', '用户', '金额', '状态', '创建时间', '商品明细']
        const rows = allOrders.map(item => {
          const itemsText = (item.items || []).map(i => `${i.materialName} x${i.quantity}`).join('；')
          return [
            item.orderNo,
            item.username,
            item.totalAmount,
            getStatusText(item.status),
            item.createTime,
            itemsText
          ]
        })
        let csvContent = '\uFEFF' + headers.map(escapeCsvValue).join(',') + '\n'
        rows.forEach((row) => {
          csvContent += row.map(escapeCsvValue).join(',') + '\n'
        })
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `orders_${new Date().getTime()}.csv`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
        ElMessage.success(`已导出 ${allOrders.length} 条订单`)
      })
      .catch(() => {
        ElMessage.error('导出失败，请重试')
      })
      .finally(() => {
        exporting.value = false
      })
}

const handleCleanCart = () => {
    ElMessageBox.prompt('请输入要清空购物车的用户ID', '清空购物车', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: 'ID必须为数字'
    }).then(async ({ value }) => {
        await clearCart(value)
        ElMessage.success('清理成功')
    }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.order-manage {
  padding: 20px;
}
.header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}
.filters {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  flex-wrap: wrap;
}
.filter-control {
  width: 180px;
}
.keyword-input {
  width: 240px;
}
.status-select {
  width: 130px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  display: flex;
  align-items: center;
}
.order-detail .info-row {
    margin-bottom: 10px;
    display: flex;
}
.order-detail .label {
    width: 80px;
    font-weight: bold;
    color: #606266;
}
.order-detail .price {
    color: #f56c6c;
    font-size: 16px;
}
</style>
