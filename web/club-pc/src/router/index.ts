import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Layout from "@/views/layout/index.vue";

// 定义路由规则
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    component: Layout,
    redirect: "/home",
    children: [
      {
        path: "/home",
        name: "home",
        component: Layout,
        meta: { title: "首页" },
      }
    ],
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 导出路由实例
export default router;