<template>
  <el-icon @click="iconClick" :size="28" style="cursor: pointer;">
    <component :is="status ? Expand : Fold"></component>
  </el-icon>
</template>

<script setup lang="ts">
import { Expand, Fold } from "@element-plus/icons-vue";
import { computed } from "vue";
import { useMenuStore } from "@/store/menu";

const store = useMenuStore();

const status = computed(() => {
  return store.getIsCollapse;
});

const iconClick = () => {
  store.setIsCollapse();
};
</script>

<style scoped lang="scss">
</style>
