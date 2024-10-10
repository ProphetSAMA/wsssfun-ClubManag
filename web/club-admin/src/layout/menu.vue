<script setup lang="ts">
import MenuLogo from "@/layout/MenuLogo.vue";
import { computed } from 'vue'
import {Avatar, ChatLineSquare, DataAnalysis, Histogram, HomeFilled, Operation, User} from '@element-plus/icons-vue'
import { useMenuStore } from "@/store/menu";

const store = useMenuStore();

const IsCollapse = computed(() => {
  return store.getIsCollapse;
});

</script>

<template>
  <MenuLogo/>
  <el-menu
      default-active="0"
      class="el-menu-vertical-demo"
      :collapse="IsCollapse"
      @open="handleOpen"
      @close="handleClose"
      active-text-color="#ffd04b"
      background-color="#0f4980"
  >
    <el-menu-item index="0">
      <el-icon>
        <HomeFilled/>
      </el-icon>
      <template #title>首页</template>
    </el-menu-item>
    <el-menu-item index="1">
      <el-icon>
        <User/>
      </el-icon>
      <template #title>用户管理</template>
    </el-menu-item>
    <el-menu-item index="2">
      <el-icon>
        <Menu/>
      </el-icon>
      <template #title>菜单管理</template>
    </el-menu-item>
    <el-menu-item index="3">
      <el-icon>
        <Operation/>
      </el-icon>
      <template #title>社团分类</template>
    </el-menu-item>
    <el-menu-item index="4">
      <el-icon>
        <Histogram/>
      </el-icon>
      <template #title>社团管理</template>
    </el-menu-item>
    <el-menu-item index="5">
      <el-icon>
        <Avatar/>
      </el-icon>
      <template #title>团员管理</template>
    </el-menu-item>
    <el-menu-item index="6">
      <el-icon>
        <DataAnalysis/>
      </el-icon>
      <template #title>活动管理</template>
    </el-menu-item>
    <el-menu-item index="7">
      <el-icon>
        <ChatLineSquare/>
      </el-icon>
      <template #title>新闻管理</template>
    </el-menu-item>
  </el-menu>
</template>

<style scoped lang="scss">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 230px;
  min-height: 400px;
}

.el-menu {
  border-right: none;
}

.el-menu .el-menu-item {
  color: #bfcbd9;
  font-size: 15px;
  font-weight: bold;
}

.el-menu-item.is-active {
  background-color: #143459 !important;
  color: #f07810;
}

:deep(.el-menu-item:hover) {
  background-color: #092e50 !important;
}
</style>