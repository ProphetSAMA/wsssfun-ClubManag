<template>
  <div>
    <el-button type="primary"  icon="Plus" size="default">新增</el-button>
    <el-icon :size="20">
      <Edit />
    </el-icon>

  </div>
</template>

<script setup lang="ts">

import {Edit} from "@element-plus/icons-vue";
</script>

<style scoped lang="scss">

</style>