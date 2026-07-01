import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'
import { useUserStore } from '@/store/user'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/login/index.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            {
                path: '/home',
                name: 'home',
                component: () => import('@/views/home/index.vue'),
                meta: { title: '首页' }
            },
            {
                path: '/user',
                name: 'user',
                component: () => import('@/views/user/index.vue'),
                meta: { title: '用户管理' }
            },
            {
                path: '/category',
                name: 'category',
                component: () => import('@/views/category/index.vue'),
                meta: { title: '社团分类' }
            },
            {
                path: '/club',
                name: 'club',
                component: () => import('@/views/team/index.vue'),
                meta: { title: '社团管理' }
            },
            {
                path: '/member',
                name: 'member',
                component: () => import('@/views/member/index.vue'),
                meta: { title: '团员管理' }
            },
            {
                path: '/activity',
                name: 'activity',
                component: () => import('@/views/activity/index.vue'),
                meta: { title: '新闻活动' }
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

// 路由守卫
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()
    if (to.path === '/login') {
        if (userStore.token) {
            next('/home')
        } else {
            next()
        }
    } else {
        if (userStore.token) {
            next()
        } else {
            next('/login')
        }
    }
})

export default router
