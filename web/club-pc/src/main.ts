import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

import { createPinia } from 'pinia'
// 引入Element-Plus
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIcons from '@element-plus/icons-vue'

const app = createApp(App)
const pinia = createPinia()
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(pinia)
app.use(router)
app.mount('#app')

// 全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIcons)) {
    app.component(key, component)
  }
