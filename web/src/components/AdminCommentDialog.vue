<template>
  <el-dialog
    v-model="visible"
    title="评论管理"
    width="800px"
    @close="handleClose"
  >
    <el-table :data="commentList" v-loading="loading" row-key="id" default-expand-all>
      <el-table-column prop="nickname" label="用户" width="120" />
      <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request' // Assuming generic request usage or I can import API if exists

const props = defineProps({
  modelValue: Boolean,
  targetId: [Number, String],
  targetType: [Number, String]
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const commentList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.targetId) {
    pageNum.value = 1
    fetchComments()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await request.get('/comment/list', {
      params: {
        targetType: props.targetType,
        targetId: props.targetId,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    commentList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchComments()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该评论及其子评论吗?', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/comment/${row.id}`)
      ElMessage.success('删除成功')
      fetchComments()
    })
    .catch(() => {})
}

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
