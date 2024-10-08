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
        component: () => import("@/views/home/home.vue"),
        meta: { title: "首页" }
      },
      {
        path: "/club",
        name: "club",
        component: () => import("@/views/club/club.vue"),
        meta: { title: "社团" },
      },
      {
        path: "/activity",
        name: "activity",
        component: () => import("@/views/activity/activity.vue"),
        meta: { title: "活动" },
      },
      {
        path: "/news",
        name: "news",
        component: () => import("@/views/news/news.vue"),
        meta: { title: "新闻" },
      },
      {
        path: "/notice",
        name: "notice",
        component: () => import("@/views/notice/notice.vue"),
        meta: { title: "公告" },
      },
      {
        path: "/mine",
        name: "mine",
        component: () => import("@/views/mine/mine.vue"),
        meta: { title: "个人中心" },
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