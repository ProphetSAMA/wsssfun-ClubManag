import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

import { createPinia } from 'pinia'
import piniaPersistedstate from 'pinia-plugin-persistedstate';

// Element Plus 的组件和样式由 unplugin 按需注入，这里不再整体注册，
// 只需要把模板里真正用到的图标注册成全局组件（prefix-icon="User" 这类字符串靠它解析）。
import { Lock, User } from '@element-plus/icons-vue'

const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPersistedstate)
app.use(pinia)
app.use(router)

for (const [name, component] of Object.entries({ Lock, User })) {
  app.component(name, component)
}

app.mount('#app')
