import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import NewsDetail from '../views/NewsDetail.vue'
import NewsList from '../views/NewsList.vue'
import MaterialList from '../views/MaterialList.vue'
import MaterialDetail from '../views/MaterialDetail.vue'
import GuideList from '../views/GuideList.vue'
import GuideDetail from '../views/GuideDetail.vue'
import ForumList from '../views/ForumList.vue'
import PostDetail from '../views/PostDetail.vue'
import PostEdit from '../views/PostEdit.vue'
import Cart from '../views/Cart.vue'
import OrderList from '../views/OrderList.vue'
import Profile from '../views/Profile.vue'
import Admin from '../views/Admin.vue'
import AdminLogin from '../views/AdminLogin.vue'
import Dashboard from '../views/admin/Dashboard.vue'
import UserList from '../views/admin/UserList.vue'
import FeedbackList from '../views/admin/FeedbackList.vue'
import MaterialManage from '../views/admin/MaterialManage.vue'
import NewsManage from '../views/admin/NewsManage.vue'
import GuideManage from '../views/admin/GuideManage.vue'
import ForumManage from '../views/admin/ForumManage.vue'
import OrderManage from '../views/admin/OrderManage.vue'
import AiChatManage from '../views/admin/AiChatManage.vue'
import { useUserStore } from '@/stores/user'
import { getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

const routes = [
  { path: '/', name: 'Home', component: Home, meta: { public: true } },
  { path: '/login', name: 'Login', component: Login, meta: { public: true } },
  { path: '/register', name: 'Register', component: Login, props: { registerMode: true }, meta: { public: true } },
  { path: '/news', name: 'NewsList', component: NewsList, meta: { public: true } },
  { path: '/news/:id', name: 'NewsDetail', component: NewsDetail, meta: { public: true } },
  { path: '/materials', name: 'MaterialList', component: MaterialList, meta: { public: true } },
  { path: '/materials/:id', name: 'MaterialDetail', component: MaterialDetail, meta: { public: true } },
  { path: '/guides', name: 'GuideList', component: GuideList, meta: { public: true } },
  { path: '/guides/:id', name: 'GuideDetail', component: GuideDetail, meta: { public: true } },
  { path: '/forum', name: 'ForumList', component: ForumList, meta: { public: true } },
  { path: '/posts/create', name: 'PostCreate', component: PostEdit, meta: { requiresAuth: true } },
  { path: '/posts/edit/:id', name: 'PostEdit', component: PostEdit, meta: { requiresAuth: true } },
  { path: '/posts/:id', name: 'PostDetail', component: PostDetail, meta: { public: true } },
  { path: '/cart', name: 'Cart', component: Cart, meta: { requiresAuth: true } },
  { path: '/order/success', name: 'OrderSuccess', component: () => import('../views/OrderSuccess.vue'), meta: { requiresAuth: true } },
  { path: '/order/list', name: 'OrderList', component: OrderList, meta: { requiresAuth: true } },
  { path: '/profile', name: 'Profile', component: Profile, meta: { requiresAuth: true } },
  { path: '/admin/login', name: 'AdminLogin', component: AdminLogin, meta: { public: true } },
  { path: '/admin-login', name: 'AdminLoginStandalone', component: AdminLogin, meta: { public: true } },
  {
    path: '/admin',
    component: Admin,
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: Dashboard },
      { path: 'users', name: 'AdminUsers', component: UserList },
      { path: 'materials', name: 'AdminMaterials', component: MaterialManage },
      { path: 'news', name: 'AdminNews', component: NewsManage },
      { path: 'guides', name: 'AdminGuides', component: GuideManage },
      { path: 'forum', name: 'AdminForum', component: ForumManage },
      { path: 'orders', name: 'AdminOrders', component: OrderManage },
      { path: 'feedbacks', name: 'AdminFeedbacks', component: FeedbackList },
      { path: 'ai-chat', name: 'AdminAiChat', component: AiChatManage }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    const isAdminRoute = to.path.startsWith('/admin') || to.path === '/admin-login'
    const isAdminLoginRoute = to.path === '/admin/login' || to.path === '/admin-login'
    const isFrontRoute = !isAdminRoute
    const isFrontLoggedIn = userStore.isLoggedIn
    const isAdminLoggedIn = userStore.isAdminLoggedIn && userStore.hasAdminAccess

    if (isAdminRoute) {
        if (isAdminLoginRoute) {
            if (isAdminLoggedIn) {
                next('/admin/dashboard')
                return
            }
            next()
            return
        }

        if (!isAdminLoggedIn) {
            next('/admin/login')
            return
        }

        try {
            const adminId = userStore.adminInfo?.id
            if (adminId) {
                const latestAdmin = await getUserInfo(adminId)
                if (latestAdmin?.status === 0) {
                    userStore.logoutAdmin()
                    ElMessage.error('账号已被禁用，请联系系统管理员')
                    next('/admin/login')
                    return
                }
                userStore.setAdminUser(latestAdmin)
            }
        } catch {
        }

        if (userStore.isOperator) {
            const operatorAllowed = [
                '/admin/dashboard',
                '/admin/materials',
                '/admin/news',
                '/admin/guides',
                '/admin/forum',
                '/admin/feedbacks'
            ]
            const isAllowed = operatorAllowed.some(path => to.path.startsWith(path))
            if (!isAllowed) {
                next('/admin/dashboard')
                return
            }
        }
    }

    if (isFrontRoute && to.meta.requiresAuth && !isFrontLoggedIn) {
        next('/login')
        return
    }

    if (isFrontRoute && to.meta.requiresAuth && isFrontLoggedIn) {
        try {
            const userId = userStore.userInfo?.id
            if (userId) {
                const latestUser = await getUserInfo(userId)
                if (latestUser?.status === 0) {
                    userStore.logoutFront()
                    ElMessage.error('账号已被禁用，请联系管理员')
                    next('/login')
                    return
                }
                userStore.setFrontUser(latestUser)
            }
        } catch {
        }
    }

    next()
})

export default router
