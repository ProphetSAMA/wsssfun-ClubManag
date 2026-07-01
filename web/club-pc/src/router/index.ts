import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import Layout from "@/views/layout/index.vue";
import { useUserStore } from "@/store/user";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/index.vue"),
    meta: { title: "登录" }
  },
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
        meta: { title: "个人中心", requiresAuth: true },
        redirect: "/mine/mycenter",
        children: [
          {
            path: "/mine/mycenter",
            name: "mycenter",
            component: () => import("@/views/mine/mycenter.vue"),
            meta: { title: "个人中心", requiresAuth: true }
          },
          {
            path: "/mine/myclub",
            name: "myclub",
            component: () => import("@/views/mine/myclub.vue"),
            meta: { title: "我的社团", requiresAuth: true }
          },
          {
            path: "/mine/myactivity",
            name: "myactivity",
            component: () => import("@/views/mine/myactivity.vue"),
            meta: { title: "我的活动", requiresAuth: true }
          },
        ]
      },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  if (to.path === '/login') {
    // 已登录则跳转首页
    if (userStore.isLoggedIn) {
      next('/home')
    } else {
      next()
    }
  } else if (to.meta.requiresAuth) {
    // 需要登录的页面，未登录则跳转登录页
    if (userStore.isLoggedIn) {
      next()
    } else {
      next('/login')
    }
  } else {
    next()
  }
});

export default router;
