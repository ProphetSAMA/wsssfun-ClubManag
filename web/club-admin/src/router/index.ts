import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/home',
        name: 'home',
        component: Layout,
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;