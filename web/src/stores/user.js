import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
    const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

    const isLoggedIn = computed(() => !!user.value.id)
    const isAdmin = computed(() => user.value.role === 'ADMIN')
    const isOperator = computed(() => user.value.role === 'OPERATOR')
    const hasAdminAccess = computed(() => ['ADMIN', 'OPERATOR'].includes(user.value.role))
    const canViewDashboard = computed(() => hasAdminAccess.value)
    const canManageUsers = computed(() => isAdmin.value)
    const canManageOrders = computed(() => isAdmin.value)
    const canManageMaterials = computed(() => hasAdminAccess.value)
    const canManageContent = computed(() => hasAdminAccess.value)
    const canManageFeedback = computed(() => hasAdminAccess.value)
    const userInfo = computed(() => user.value)

    function setUser(userData) {
        user.value = userData
        localStorage.setItem('user', JSON.stringify(userData))
    }

    function logout() {
        user.value = {}
        localStorage.removeItem('user')
    }

    return {
        user,
        isLoggedIn,
        isAdmin,
        isOperator,
        hasAdminAccess,
        canViewDashboard,
        canManageUsers,
        canManageOrders,
        canManageMaterials,
        canManageContent,
        canManageFeedback,
        userInfo,
        setUser,
        logout
    }
})
