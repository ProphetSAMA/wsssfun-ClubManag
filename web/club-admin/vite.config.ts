import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // Element Plus 按需引入：模板里用到哪个组件、代码里调到哪个 API，才打包对应代码和样式
    AutoImport({
      resolvers: [ElementPlusResolver()],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  server: {
    host: '0.0.0.0',
    port: 8080,
    hmr: true,
    open: true
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  build: {
    rollupOptions: {
      output: {
        // 拆开第三方依赖，避免整个应用挤进一个 1MB 的 index chunk
        manualChunks(id) {
          if (!id.includes('node_modules')) return;
          if (id.includes('@wangeditor')) return 'wangeditor';
          if (id.includes('element-plus')) return 'element-plus';
          if (id.includes('/vue/') || id.includes('vue-router') || id.includes('pinia')) return 'vue-vendor';
          return 'vendor';
        }
      }
    }
  }
});
