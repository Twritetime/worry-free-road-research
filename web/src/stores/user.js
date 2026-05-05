import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const parseStoredUser = (key) => {
    try {
        const raw = localStorage.getItem(key)
        return raw ? JSON.parse(raw) : {}
    } catch {
        localStorage.removeItem(key)
        return {}
    }
}

const hasUserId = (userData) => {
    if (!userData || typeof userData !== 'object') {
        return false
    }
    return userData.id !== null && userData.id !== undefined && userData.id !== ''
}

export const useUserStore = defineStore('user', () => {
    const frontUser = ref(parseStoredUser('front_user'))
    const adminUser = ref(parseStoredUser('admin_user'))
    const user = computed(() => frontUser.value)
    const admin = computed(() => adminUser.value)

    const isLoggedIn = computed(() => hasUserId(frontUser.value))
    const isAdminLoggedIn = computed(() => hasUserId(adminUser.value))
    const isAdmin = computed(() => adminUser.value.role === 'ADMIN')
    const isFrontAdmin = computed(() => frontUser.value.role === 'ADMIN')
    const isOperator = computed(() => adminUser.value.role === 'OPERATOR')
    const hasAdminAccess = computed(() => ['ADMIN', 'OPERATOR'].includes(adminUser.value.role))
    const canViewDashboard = computed(() => hasAdminAccess.value)
    const canManageUsers = computed(() => isAdmin.value)
    const canManageOrders = computed(() => isAdmin.value)
    const canManageMaterials = computed(() => hasAdminAccess.value)
    const canManageContent = computed(() => hasAdminAccess.value)
    const canManageFeedback = computed(() => hasAdminAccess.value)
    const canManageAi = computed(() => isAdmin.value)
    const userInfo = computed(() => frontUser.value)
    const adminInfo = computed(() => adminUser.value)

    function setFrontUser(userData) {
        if (userData && !userData.token && frontUser.value?.token) {
            userData.token = frontUser.value.token
        }
        frontUser.value = userData || {}
        localStorage.setItem('front_user', JSON.stringify(frontUser.value))
    }

    function setAdminUser(userData) {
        if (userData && !userData.token && adminUser.value?.token) {
            userData.token = adminUser.value.token
        }
        adminUser.value = userData || {}
        localStorage.setItem('admin_user', JSON.stringify(adminUser.value))
    }

    function setUser(userData) {
        setFrontUser(userData)
    }

    function logoutFront() {
        frontUser.value = {}
        localStorage.removeItem('front_user')
    }

    function logoutAdmin() {
        adminUser.value = {}
        localStorage.removeItem('admin_user')
    }

    function logout() {
        logoutFront()
    }

    return {
        user,
        admin,
        frontUser,
        adminUser,
        isLoggedIn,
        isAdminLoggedIn,
        isAdmin,
        isOperator,
        hasAdminAccess,
        canViewDashboard,
        canManageUsers,
        canManageOrders,
        canManageMaterials,
        canManageContent,
        isFrontAdmin,
        canManageFeedback,
        canManageAi,
        userInfo,
        adminInfo,
        setFrontUser,
        setAdminUser,
        setUser,
        logoutFront,
        logoutAdmin,
        logout
    }
})
