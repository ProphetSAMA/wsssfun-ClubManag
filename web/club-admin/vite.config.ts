import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

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
      '@': resolve(__dirname, 'src'), // 确保这里的 '@' 被正确配置
    },
  },
});
