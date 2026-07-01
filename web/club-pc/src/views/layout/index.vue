<template>
  <el-container class="app-layout">
    <el-header class="app-header" height="var(--header-height)">
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
        <div class="footer-brand">
          <span class="footer-logo">🏛️</span>
          <span>高校社团信息化平台</span>
        </div>
        <div class="footer-copy">&copy; {{ new Date().getFullYear() }} All rights reserved</div>
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
  background: var(--bg-page);
}

.app-header {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 50%, #818cf8 100%);
  display: flex;
  align-items: center;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(79, 70, 229, 0.25);

  .header-inner {
    width: 100%;
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    padding: 0 var(--space-lg);
  }
}

.app-main {
  padding: 0;
  min-height: calc(100vh - var(--header-height) - 80px);

  .main-inner {
    max-width: 1280px;
    margin: 0 auto;
    padding: var(--space-xl) var(--space-lg);
  }
}

.app-footer {
  background: var(--text-primary);
  color: var(--text-muted);
  padding: var(--space-xl) var(--space-lg);

  .footer-inner {
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .footer-brand {
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    font-weight: 500;
    color: #e2e8f0;
  }

  .footer-logo {
    font-size: 20px;
  }

  .footer-copy {
    font-size: 13px;
    color: var(--text-muted);
  }
}
</style>
