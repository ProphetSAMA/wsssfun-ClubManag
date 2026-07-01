<template>
  <el-container class="app-layout">
    <el-header class="app-header" height="var(--header-h)">
      <div class="header-inner">
        <logo />
        <tabs />
        <loginout />
      </div>
    </el-header>
    <el-main class="app-main">
      <div class="main-inner">
        <RouterView />
      </div>
    </el-main>
    <el-footer class="app-footer" height="auto">
      <div class="footer-inner">
        <span>高校社团信息化平台</span>
        <span class="copy">&copy; {{ new Date().getFullYear() }}</span>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import logo from '@/views/layout/logo.vue'
import tabs from '@/views/layout/tabs.vue'
import loginout from '@/views/layout/loginout.vue'
</script>

<style scoped lang="scss">
.app-layout {
  min-height: 100vh;
  background: var(--bg);
}

.app-header {
  background: #2c3e6b;
  display: flex;
  align-items: center;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;

  .header-inner {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    padding: 0 24px;
  }
}

.app-main {
  padding: 0;
  min-height: calc(100vh - var(--header-h) - 60px);

  .main-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
  }
}

.app-footer {
  background: #2c3e6b;
  color: rgba(255, 255, 255, 0.5);
  padding: 16px 24px;
  font-size: 13px;

  .footer-inner {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .copy {
    font-size: 12px;
  }
}
</style>
