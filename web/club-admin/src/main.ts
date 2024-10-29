import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

import { createPinia } from 'pinia'
import piniaPersistedstate from 'pinia-plugin-persistedstate';

// 引入Element-Plus
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIcons from '@element-plus/icons-vue'
import myConfirm from "@/utils/myConfirm";
const app = createApp(App)
// 创建pinia实例
const pinia = createPinia()
// 使用pinia插件
pinia.use(piniaPersistedstate)
// 使用Element-Plus
app.use(ElementPlus, {
    locale: zhCn,
})
// 使用pinia
app.use(pinia)
// 使用路由
app.use(router)
// 挂载
app.mount('#app')

// 全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIcons)) {
    app.component(key, component)
}
// 全局挂载自定义弹框
app.config.globalProperties.$myConfirm = myConfirm
