<template>
  <div class="user-list-container">
    <div class="header">
      <h3>用户管理</h3>
      <div class="actions">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名/昵称..."
          class="search-input"
          @keyup.enter="fetchUsers"
        >
          <template #append>
            <el-button @click="fetchUsers">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-table :data="userList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="getRoleTag(row.role)">{{ getRoleText(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)" v-if="row.role !== 'ADMIN'">删除</el-button>
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

    <!-- Edit Dialog -->
    <el-dialog v-model="dialogVisible" title="编辑用户" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
           <el-select v-model="form.role">
               <el-option label="学生" value="STUDENT"></el-option>
               <el-option label="运营" value="OPERATOR"></el-option>
               <el-option label="管理员" value="ADMIN"></el-option>
           </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserList, deleteUser, updateUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive({})

const getRoleText = (role) => {
    const map = {
        'ADMIN': '管理员',
        'OPERATOR': '运营',
        'STUDENT': '学生'
    }
    return map[role] || '学生'
}

const getRoleTag = (role) => {
    const map = {
        'ADMIN': 'danger',
        'OPERATOR': 'warning',
        'STUDENT': 'primary'
    }
    return map[role] || 'primary'
}

const fetchUsers = async () => {
  loading.value = true
  try {
      const res = await getUserList({
          pageNum: pageNum.value,
          pageSize: pageSize.value,
          keyword: keyword.value
      })
      userList.value = res.records
      total.value = res.total
  } finally {
      loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchUsers()
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗?', '提示', { type: 'warning' })
  .then(async () => {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUsers()
  })
}

const handleSubmit = async () => {
  try {
      await updateUser(form)
      ElMessage.success('更新成功')
      dialogVisible.value = false
      fetchUsers()
  } catch (error) {
      // Error handled by request interceptor
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-input {
  width: 300px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
