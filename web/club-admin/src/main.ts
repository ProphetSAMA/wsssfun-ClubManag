import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

import { createPinia } from 'pinia'
import piniaPersistedstate from 'pinia-plugin-persistedstate';

// Element Plus 的组件和样式由 unplugin 按需注入，这里不再整体注册，
// 只需要把模板里真正用到的图标注册成全局组件（<User /> 和 icon="search" 这类字符串都靠它解析）。
import {
  ArrowDown, Avatar, Close, DataAnalysis, Delete, Histogram,
  HomeFilled, Lock, Operation, Plus, Search, User,
} from '@element-plus/icons-vue'

import myConfirm from "@/utils/myConfirm";

const app = createApp(App)

// 创建pinia实例
const pinia = createPinia()
// 使用pinia插件
pinia.use(piniaPersistedstate)
// 使用pinia
app.use(pinia)
// 使用路由
app.use(router)

// 全局注册用到的小图标
for (const [name, component] of Object.entries({
  ArrowDown, Avatar, Close, DataAnalysis, Delete, Histogram,
  HomeFilled, Lock, Operation, Plus, Search, User,
})) {
  app.component(name, component)
}

// 全局挂载自定义弹框
app.config.globalProperties.$myConfirm = myConfirm

// 挂载
app.mount('#app')
