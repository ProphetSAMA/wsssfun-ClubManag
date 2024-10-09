<template>
    <el-container class="myContainer">
      <el-aside class="myAside" width="230px">
        <Menu />
      </el-aside>
      <el-container>
        <el-header class="myHeader">
          <slot name="header">默认头部内容</slot>
        </el-header>
        <el-main class="myMain">
          <slot>默认主体内容</slot>
        </el-main>
      </el-container>
    </el-container>
</template>

<script setup lang="ts">
import Menu from "@/layout/menu.vue";
</script>

<style scoped lang="scss">
.myContainer {
  height: 100%;

  .myAside {
    background-color: #0f4980;
    // 添加响应式样式
    @media (max-width: 768px) {
      display: none; // 在小屏幕上隐藏
    }
  }

  .myHeader {
    background-color: #0f4980;
    color: white;
  }

  .myMain {
    background-color: #f0f0f0;
  }
}
</style>
