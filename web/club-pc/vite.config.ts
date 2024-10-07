import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  // 服务器设置
  server: {
    host: '0.0.0.0',
    port: 80,
    hmr: true,
    open: true
  },
  // 引用别名设置
  resolve: {
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname, './src')
      }
    ]
  }
})
