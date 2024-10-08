<template>
  <el-container class="container">
    <el-header class="header">
        <logo></logo>
        <tabs></tabs>
        <loginout></loginout>
    </el-header>
    <el-main class="main">
      <RouterView />
    </el-main>
    <el-footer class="footer">
      <div class="footer">
        <p>2024</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import logo from '@/views/layout/logo.vue'
import tabs from '@/views/layout/tabs.vue'
import loginout from '@/views/layout/loginout.vue';
</script>

<style scoped lang="scss">
.container {
  height: 100%;
  .header {
    background-color: #5D6085FF;
    display: flex;
    align-items: center;
    padding: 0px;
  }
  .main {
    padding: 0px 200px 0px 200px
  }
  .footer {
    text-align: center;
    padding: 10px 0px;
  }
}
</style>
