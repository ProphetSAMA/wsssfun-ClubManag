import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 8080,
    hmr: true,
    open: true,
  },
  resolve: {
    alias: {
      find: '@',
      replacement: resolve(__dirname, 'src'),
    },
  },
})
