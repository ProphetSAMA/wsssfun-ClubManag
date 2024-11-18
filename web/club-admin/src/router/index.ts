import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        component: Layout,
        // 重定向到首页
        redirect: '/home',
        children: [
            {
                path: '/home',
                name: 'home',
                component: () => import('@/views/home/index.vue'),
                meta: {
                    title: '首页'
                }
            },
            {
                path: '/user',
                name: 'user',
                component: () => import('@/views/user/index.vue'),
                meta: {
                    title: '用户管理'
                }
            },
            {
                path: '/menu',
                name: 'menu',
                component: () => import('@/views/menu/index.vue'),
                meta: {
                    title: '菜单管理'
                }
            },
            {
                path: '/category',
                name: 'category',
                component: () => import('@/views/category/index.vue'),
                meta: {
                    title: '社团分类'
                }
            },
            {
                path: '/club',
                name: 'club',
                component: () => import('@/views/team/index.vue'),
                meta: {
                    title: '社团管理'
                }
            },
            {
                path: '/member',
                name: 'member',
                component: () => import('@/views/member/index.vue'),
                meta: {
                    title: '团员管理'
                }
            },
            {
                path: '/activity',
                name: 'activity',
                component: () => import('@/views/activity/index.vue'),
                meta: {
                    title: '新闻活动'
                }
            },
            // {
            //     path: '/news',
            //     name: 'news',
            //     component: () => import('@/views/news/index.vue'),
            //     meta: {
            //         title: '新闻管理'
            //     }
            // },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;