<template>
  <el-container class="container">
    <el-header class="header">Header</el-header>
    <el-main class="main">Main</el-main>
    <el-footer class="footer">Footer</el-footer>
  </el-container>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.container {
  height: 100%;
  .header {
    background-color: #5D6085FF;
  }
  .main {
    background-color: #c0c4cc;
  }
  .footer {
    background-color: #2E3031FF;
  }
}
</style>
