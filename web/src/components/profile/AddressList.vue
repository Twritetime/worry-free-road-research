<template>
  <div class="address-list">
    <div class="header-actions">
      <el-button type="primary" icon="Plus" @click="handleAdd">新增地址</el-button>
    </div>

    <div class="custom-table-wrapper">
      <el-table :data="addressList" style="width: 100%" v-loading="loading" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="手机号" width="150" />
        <el-table-column label="地址" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.province }} {{ row.city }} {{ row.district }} {{ row.detailAddress }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="success" size="small" effect="plain">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
            <el-empty description="暂无收货地址" />
        </template>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑地址' : '新增地址'"
      width="500px"
      destroy-on-close
      class="custom-dialog"
    >
      <el-form :model="form" label-position="top" class="custom-form">
        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="收货人">
                  <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="手机号">
                  <el-input v-model="form.receiverPhone" placeholder="请输入手机号码" />
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-form-item label="所在地区">
            <div class="area-inputs">
               <el-input v-model="form.province" placeholder="省" />
               <el-input v-model="form.city" placeholder="市" />
               <el-input v-model="form.district" placeholder="区/县" />
            </div>
        </el-form-item>
        
        <el-form-item label="详细地址">
          <el-input type="textarea" v-model="form.detailAddress" rows="3" placeholder="街道、楼牌号等" resize="none" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getAddressList, saveAddress, updateAddress, deleteAddress } from '@/api/address'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const addressList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const fetchAddress = async () => {
  if (!user.value.id) return
  loading.value = true
  try {
    const res = await getAddressList(user.value.id)
    addressList.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.id = null
  form.receiverName = ''
  form.receiverPhone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.detailAddress = ''
  form.isDefault = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该地址吗?', '提示', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' })
    .then(async () => {
      await deleteAddress(row.id)
      ElMessage.success('删除成功')
      fetchAddress()
    })
}

const handleSubmit = async () => {
  try {
    const data = { ...form, userId: user.value.id }
    if (form.id) {
      await updateAddress(data)
    } else {
      await saveAddress(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchAddress()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  fetchAddress()
})
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.custom-table-wrapper {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    overflow: hidden;
}

.area-inputs {
    display: flex;
    gap: 10px;
}

.area-inputs .el-input {
    flex: 1;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
}
</style>
