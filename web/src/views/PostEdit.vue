<template>
  <div class="page-container">
    <div class="edit-card">
      <div class="header">
        <h2>{{ isEdit ? '编辑帖子' : '发布新帖' }}</h2>
        <p class="subtitle">分享你的考研经验与心得</p>
      </div>
      
      <el-form :model="form" ref="formRef" :rules="rules" label-position="top" class="custom-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入帖子标题（2-100字）" size="large"></el-input>
        </el-form-item>
        
        <el-form-item label="板块" prop="category">
          <el-select v-model="form.category" placeholder="请选择所属板块" size="large" style="width: 100%">
            <el-option label="公共课交流" :value="1" />
            <el-option label="专业课交流" :value="2" />
            <el-option label="院校复试经验" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="15" 
            placeholder="请输入帖子详细内容..."
            resize="none"
          ></el-input>
        </el-form-item>
        
        <div class="form-actions">
          <el-button @click="$router.back()" size="large">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="loading" size="large" class="submit-btn">
            {{ isEdit ? '保存修改' : '立即发布' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createPost, updatePost, getPostById } from '@/api/post'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user, isAdmin } = storeToRefs(userStore)

const formRef = ref(null)
const loading = ref(false)
const postId = ref(null)

const isEdit = computed(() => !!postId.value)

const form = reactive({
    title: '',
    content: '',
    category: 1
})

const rules = {
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    category: [
        { required: true, message: '请选择板块', trigger: 'change' }
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' }
    ]
}

onMounted(async () => {
    if (route.name === 'PostEdit' && route.params.id) {
        postId.value = route.params.id
        await fetchPost()
    }
})

const fetchPost = async () => {
    try {
        const res = await getPostById(postId.value)
        form.title = res.title
        form.content = res.content
        form.category = res.category || 1
        
        if (user.value.id !== res.userId && !isAdmin.value) {
            ElMessage.error('无权编辑此贴')
            router.push('/forum')
        }
    } catch (error) {
        ElMessage.error('获取帖子失败')
        router.push('/forum')
    }
}

const submitForm = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
        if (valid) {
            if (!user.value.id) {
                ElMessage.error('请先登录')
                router.push('/login')
                return
            }

            loading.value = true
            try {
                if (isEdit.value) {
                    await updatePost({ id: postId.value, ...form })
                    ElMessage.success('修改成功')
                } else {
                    const postData = {
                        ...form,
                        userId: user.value.id,
                        nickname: user.value.nickname || user.value.username,
                        avatar: user.value.avatar
                    }
                    await createPost(postData)
                    ElMessage.success('发布成功')
                }
                router.push('/forum')
            } catch (error) {
                ElMessage.error(error.message || (isEdit.value ? '修改失败' : '发布失败'))
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style scoped>
.page-container {
    max-width: 800px;
    margin: 40px auto;
    padding: 0 20px;
}

.edit-card {
    background: #fff;
    padding: 40px;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.header {
    margin-bottom: 30px;
    text-align: center;
}

.header h2 {
    font-size: 24px;
    color: #1a1a1a;
    margin-bottom: 8px;
}

.subtitle {
    color: #909399;
    font-size: 14px;
}

.custom-form {
    margin-top: 20px;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    margin-top: 40px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
}

.submit-btn {
    min-width: 120px;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    padding: 8px 12px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
    box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
    box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}
</style>
