<template>
  <el-container class="myContainer">
    <el-aside class="myAside" width="auto">
      <Menu />
    </el-aside>
    <el-container>
      <el-header class="myHeader">
        <Collapse />
        <BreadCrumb />
      </el-header>
      <el-main class="myMain">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Menu from "@/layout/menu.vue";
import Collapse from "@/layout/Collapse.vue";
import BreadCrumb from "@/layout/BreadCrumb.vue";
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
    display: flex;
    align-items: center;
    background-color: #0f4980;
    color: white;
    padding: 0 10px;
  }

  .myMain {
    background-color: #f0f0f0;
    padding: 0;
  }
}
</style>
