<template>
  <div class="user-list-container">
    <div class="toolbar">
      <h3>用户管理</h3>
      <div class="filter-row">
        <el-input v-model="filters.username" placeholder="用户名" clearable class="filter-item" />
        <el-input v-model="filters.phone" placeholder="手机号" clearable class="filter-item" />
        <el-date-picker
          v-model="registerTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="注册开始时间"
          end-placeholder="注册结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          class="filter-item filter-date"
          clearable
        />
        <el-select v-model="filters.status" placeholder="用户状态" clearable class="filter-item filter-status">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <el-table :data="userList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="90">
        <template #default="{ row }">
          <el-avatar :size="36" :src="row.avatar || defaultAvatar" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="getRoleTag(row.role)">{{ getRoleText(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
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
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="avatarSrc" :src="avatarSrc" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
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
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { getUserList, deleteUser, updateUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const uploadUrl = 'http://localhost:8080/file/upload'
const userList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive({})
const formAvatar = ref('')

const avatarSrc = computed(() => {
  const avatar = formAvatar.value || form.avatar
  if (!avatar) return defaultAvatar
  if (/^https?:\/\//i.test(avatar)) return avatar
  if (avatar.startsWith('/')) return `http://localhost:8080${avatar}`
  return avatar
})

const handleAvatarSuccess = (res) => {
  const url = res?.data || res
  form.avatar = url
  formAvatar.value = url
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}
const filters = reactive({
  username: '',
  phone: '',
  status: undefined
})
const registerTimeRange = ref([])

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

const getStatusText = (status) => {
  const map = {
    1: '正常',
    0: '禁用'
  }
  return map[status] || '正常'
}

const getStatusTag = (status) => {
  const map = {
    1: 'success',
    0: 'danger'
  }
  return map[status] || 'info'
}

const fetchUsers = async () => {
  loading.value = true
  try {
      const res = await getUserList({
          pageNum: pageNum.value,
          pageSize: pageSize.value,
          username: filters.username || undefined,
          phone: filters.phone || undefined,
          status: filters.status,
          registerStartTime: registerTimeRange.value?.[0],
          registerEndTime: registerTimeRange.value?.[1]
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

const handleSearch = () => {
  pageNum.value = 1
  fetchUsers()
}

const handleReset = () => {
  filters.username = ''
  filters.phone = ''
  filters.status = undefined
  registerTimeRange.value = []
  pageNum.value = 1
  fetchUsers()
}

const handleEdit = (row) => {
  Object.assign(form, row)
  formAvatar.value = row.avatar || ''
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
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}
.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.filter-item {
  width: 150px;
}
.filter-date {
  width: 360px;
}
.filter-status {
  width: 120px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.avatar-uploader {
  width: 80px;
  height: 80px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.avatar {
  width: 80px;
  height: 80px;
  object-fit: cover;
}
</style>
