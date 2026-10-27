<script setup lang="ts">
// Element Plus 改成按需引入后，不再有 app.use(ElementPlus, { locale })，
// 语言包通过 ConfigProvider 提供（组件本身也会被按需自动引入）。
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>
</template>

<style scoped></style>
