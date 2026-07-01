<template>
  <div class="myactivity-container">
    <el-card>
      <template #header>
        <span>我的活动</span>
      </template>
      <el-empty description="请先登录后查看我的活动" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.myactivity-container {
  padding: 10px;
}
</style>
