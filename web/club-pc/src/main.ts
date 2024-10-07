import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

import { createPinia } from 'pinia'
// 引入Element-Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIcons from '@element-plus/icons-vue'

const app = createApp(App)
const pinia = createPinia()
app.use(ElementPlus)
app.use(pinia)
app.use(router)
app.mount('#app')

// 全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIcons)) {
    app.component(key, component)
  }
