<template>
  <div class="mycenter-container">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      <el-empty description="请先登录后查看个人信息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.mycenter-container {
  padding: 10px;
}
</style>
