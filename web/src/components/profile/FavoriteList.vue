<template>
  <div class="favorite-list">
    <div class="filter-header">
      <el-radio-group v-model="activeType" @change="handleTypeChange" size="large">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button :label="1">资讯</el-radio-button>
        <el-radio-button :label="2">攻略</el-radio-button>
        <el-radio-button :label="3">资料</el-radio-button>
      </el-radio-group>
    </div>

    <div class="custom-table-wrapper">
      <el-table :data="favoriteList" style="width: 100%" v-loading="loading" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
        <el-table-column label="标题" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="table-link" @click="handleNavigate(row)">{{ row.targetTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.targetType)" effect="plain" round>{{ getTypeLabel(row.targetType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="收藏时间" width="180" align="center" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleCancelFavorite(row)">取消收藏</el-button>
          </template>
        </el-table-column>
        <template #empty>
             <el-empty description="暂无收藏内容" />
        </template>
      </el-table>
    </div>

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavoriteList, toggleFavorite } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const favoriteList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeType = ref('')

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      userId: user.value.id,
      type: activeType.value || undefined
    })
    favoriteList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleTypeChange = () => {
  pageNum.value = 1
  fetchFavorites()
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchFavorites()
}

const getTypeLabel = (type) => {
  const map = {
    1: '资讯',
    2: '攻略',
    3: '资料'
  }
  return map[type] || '未知'
}

const getTypeTag = (type) => {
  const map = {
    1: 'info',
    2: 'success',
    3: 'warning'
  }
  return map[type] || ''
}

const handleNavigate = (row) => {
  const routes = {
    1: `/news/${row.targetId}`,
    2: `/guides/${row.targetId}`,
    3: `/materials/${row.targetId}`
  }
  if (routes[row.targetType]) {
    router.push(routes[row.targetType])
  }
}

const handleCancelFavorite = (row) => {
  ElMessageBox.confirm('确认取消收藏吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await toggleFavorite({
        userId: user.value.id,
        targetId: row.targetId,
        targetType: row.targetType
      })
      ElMessage.success('已取消收藏')
      fetchFavorites()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  if (user.value.id) {
    fetchFavorites()
  }
})
</script>

<style scoped>
.filter-header {
  margin-bottom: 20px;
}

.custom-table-wrapper {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    overflow: hidden;
}

.table-link {
    color: var(--el-text-color-primary);
    cursor: pointer;
    transition: color 0.3s;
}

.table-link:hover {
    color: var(--el-color-primary);
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
