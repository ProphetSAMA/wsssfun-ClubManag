<template>
  <div class="myclub-container">
    <el-card>
      <template #header>
        <span>我的社团</span>
      </template>
      <el-empty description="请先登录后查看我的社团" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.myclub-container {
  padding: 10px;
}
</style>
