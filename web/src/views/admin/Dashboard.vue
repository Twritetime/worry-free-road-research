<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>总用户数</span>
            <el-tag type="success">实时</el-tag>
          </div>
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>资料营收</span>
            <el-tag type="warning">月</el-tag>
          </div>
          <div class="stat-value">¥ {{ stats.salesAmount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>帖子数量</span>
            <el-tag type="primary">总</el-tag>
          </div>
          <div class="stat-value">{{ stats.postCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>待处理反馈</span>
            <el-tag type="danger">急</el-tag>
          </div>
          <div class="stat-value">{{ stats.pendingFeedback || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="viewChart" style="height: 360px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="salesChart" style="height: 360px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <div ref="revenueChart" style="height: 360px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'

const stats = reactive({
  userCount: 0,
  salesAmount: 0,
  postCount: 0,
  pendingFeedback: 0
})

const viewChart = ref(null)
const salesChart = ref(null)
const revenueChart = ref(null)

onMounted(async () => {
  await fetchStats()
  initCharts()
})

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res) {
      Object.assign(stats, res)
    }
  } catch (error) {
    console.error(error)
  }
}

const initCharts = () => {
  const weekLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

  const viewInstance = echarts.init(viewChart.value)
  viewInstance.setOption({
    title: { text: '近七日资料浏览量' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: weekLabels },
    yAxis: { type: 'value' },
    series: [{
      data: [3200, 4100, 3800, 4600, 5200, 6100, 5800],
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.15 }
    }]
  })

  const salesInstance = echarts.init(salesChart.value)
  salesInstance.setOption({
    title: { text: '近七日资料销量' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: weekLabels },
    yAxis: { type: 'value' },
    series: [{
      data: [120, 156, 142, 168, 210, 198, 230],
      type: 'bar',
      barWidth: 26,
      itemStyle: { borderRadius: [6, 6, 0, 0] }
    }]
  })

  const revenueInstance = echarts.init(revenueChart.value)
  revenueInstance.setOption({
    title: { text: '近七日资料营收' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: weekLabels },
    yAxis: { type: 'value' },
    series: [{
      data: [8200, 9100, 8600, 9900, 11200, 12400, 11800],
      type: 'line',
      smooth: true
    }]
  })
  
  window.addEventListener('resize', () => {
      viewInstance.resize()
      salesInstance.resize()
      revenueInstance.resize()
  })
}
</script>

<style scoped>
.stat-card {
  height: 120px;
}
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 15px;
  color: #303133;
}
</style>
