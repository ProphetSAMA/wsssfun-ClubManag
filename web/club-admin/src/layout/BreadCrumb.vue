<template>
  <el-breadcrumb class="bred" separator="/">
    <el-breadcrumb-item v-for="item in tabs">
      {{ item.meta.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { onMounted, Ref, ref, watch } from "vue";
import { RouteLocationMatched, useRoute } from "vue-router";

// 路由实例
const route = useRoute();

// 定义面包屑导航的数据类型
const tabs: Ref<RouteLocationMatched[]> = ref([]);
watch(
  () => route.path,
  () => getBredcrumb()
);

// 获取面包屑导航的数据
const getBredcrumb = () => {
  let mached = route.matched.filter((item) => item.meta && item.meta.title);
  const first = mached[0];
  if (first.path !== "/home") {
    // 构造面包屑导航第一个数据
    mached = [{ path: "/home", meta: { title: "首页" } } as any].concat(mached);
  }
  tabs.value = mached;
};
// 组件挂载完成后执行
onMounted(() => {
  getBredcrumb();
});
</script>

<style lang="scss" scoped>
:deep(.el-breadcrumb_inner) {
  color: white !important;
}

.bred {
  margin-left: 20px;
}

:deep(.el-breadcrumb_item) {
  font-size: 15px !important;
}
</style>
