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
import Dashboard from '../views/admin/Dashboard.vue'
import UserList from '../views/admin/UserList.vue'
import FeedbackList from '../views/admin/FeedbackList.vue'
import MaterialManage from '../views/admin/MaterialManage.vue'
import NewsManage from '../views/admin/NewsManage.vue'
import GuideManage from '../views/admin/GuideManage.vue'
import ForumManage from '../views/admin/ForumManage.vue'
import OrderManage from '../views/admin/OrderManage.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Login, props: { registerMode: true } },
  { path: '/news', name: 'NewsList', component: NewsList },
  { path: '/news/:id', name: 'NewsDetail', component: NewsDetail },
  { path: '/materials', name: 'MaterialList', component: MaterialList },
  { path: '/materials/:id', name: 'MaterialDetail', component: MaterialDetail },
  { path: '/guides', name: 'GuideList', component: GuideList },
  { path: '/guides/:id', name: 'GuideDetail', component: GuideDetail },
  { path: '/forum', name: 'ForumList', component: ForumList },
  { path: '/posts/create', name: 'PostCreate', component: PostEdit },
  { path: '/posts/edit/:id', name: 'PostEdit', component: PostEdit },
  { path: '/posts/:id', name: 'PostDetail', component: PostDetail },
  { path: '/cart', name: 'Cart', component: Cart },
  { path: '/orders', name: 'OrderList', component: OrderList },
  { path: '/profile', name: 'Profile', component: Profile },
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
      { path: 'feedbacks', name: 'AdminFeedbacks', component: FeedbackList }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const publicRouteNames = [
    'Home', 'Login', 'NewsDetail', 'NewsList',
    'MaterialList', 'MaterialDetail', 
    'GuideList', 'GuideDetail', 
    'ForumList', 'PostDetail'
]

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    
    // Specific check for PostCreate which is under /posts but needs auth
    if (to.path === '/posts/create' || to.name === 'PostEdit') {
        if (!userStore.isLoggedIn) {
            next('/login')
            return
        }
    }

    // Admin route check
    if (to.path.startsWith('/admin')) {
        if (!userStore.isLoggedIn || !userStore.hasAdminAccess) {
            next('/')
            return
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

    if (publicRouteNames.includes(to.name) || userStore.isLoggedIn) {
        next()
    } else {
        next('/login')
    }
})

export default router
